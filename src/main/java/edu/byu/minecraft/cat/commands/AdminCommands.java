package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import edu.byu.minecraft.cat.commands.interactive.*;
import edu.byu.minecraft.cat.commands.interactive.parameters.*;
import edu.byu.minecraft.cat.dataaccess.*;
import edu.byu.minecraft.cat.model.*;
import edu.byu.minecraft.cat.util.TitleUtilities;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.*;

import static edu.byu.minecraft.cat.CivsAndTitles.getDataAccess;
import static edu.byu.minecraft.cat.util.CommandUtilities.perform;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class AdminCommands {
    public static void registerCommands(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext registryAccess,
            Commands.CommandSelection environment
    ) {
        dispatcher.register(literal("titles").then(literal("admin").requires(CommandSourceStack::isPlayer).requires(PermissionCheckers.ADMIN_PERMISSION)
                .then(literal("giveTitle")
                        .requires(PermissionCheckers.AWARD_PERMISSION)
                        .then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::allPlayers).then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::playerUnawardedTitles).executes(AdminCommands::bestowTitle))))
                .then(literal("revokeTitle")
                        .requires(PermissionCheckers.AWARD_PERMISSION)
                        .then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::allPlayers).then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::playersRemovableTitles).executes(AdminCommands::revokeTitle))))
                .then(literal("deleteTitle")
                        .requires(PermissionCheckers.MODIFY_PERMISSION)
                        .then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::allTitles).executes(AdminCommands::removeTitle)))
                .then(literal("clearWorldTitles")
                        .requires(PermissionCheckers.CLEAR_WORLD_TITLES_PERMISSION)
                        .executes(AdminCommands::clearWorldTitles))
        ));

        new InteractiveManager(Arrays.asList("titles", "admin", "create"))
                .addLine(new InteractiveTextLine(Component.literal("Title Creation")))
                .addLine(new InteractiveParameterLine<>(new InteractiveStringParameter("Name").setValidator((x)-> {
                    try {
                        return getDataAccess().getTitleDAO().get(x) == null;
                    } catch (DataAccessException e) {
                        throw new RuntimeException(e); // TODO what is the best think to handle in this error case
                    }
                })))
                .addLine(new InteractiveParameterLine<>(new InteractiveStringParameter("Description", true)))
                .addLine(new InteractiveParameterLine<>(new InteractiveTextParameter("Format")))
                .addLine(new InteractiveParameterLine<>(new InteractiveStringParameter("Type").setSuggestionProvider(SuggestionProviders::titleType).setValidator((x)->{
                    try {
                        Title.Type.valueOf(x);
                        return true;
                    }
                    catch (IllegalArgumentException ex) {
                        return false;
                    }

                })))
                .addLine(new InteractiveParameterLine<>(new InteractiveAdvancementParameter("Advancement").setOptional(true)))
                .addLine(new InteractiveFinishLine()).setDataHandler(AdminCommands::finishTitleCreation).register(dispatcher, registryAccess, PermissionCheckers.MODIFY_PERMISSION);

        InteractiveParameter<String> titleNameParam = new InteractiveStringParameter("Name").setValidator((x)-> {
            try {
                return getDataAccess().getTitleDAO().get((String)x) != null;
            } catch (DataAccessException e) {
                throw new RuntimeException(e); // TODO what is the best think to handle in this error case
            }
        }).setSuggestionProvider(SuggestionProviders::allTitles);
        new InteractiveManager(Arrays.asList("titles", "admin", "edit")).setStartArg(titleNameParam)
                .addLine(new InteractiveTextLine(Component.literal("Title Edit")))
                .addLine(new InteractiveDisplayLine<>(titleNameParam))
                .addLine(new InteractiveParameterLine<>(new InteractiveStringParameter("Description", true).setDefaultProvider(
                        (ctx)-> {
                            try {
                            return getDataAccess().getTitleDAO().get((String)ctx.getArgument("Name", String.class)).description();
                        } catch (DataAccessException e) {
                            throw new RuntimeException(e); // TODO what is the best think to handle in this error case
                        }}
                )))
                .addLine(new InteractiveParameterLine<>(new InteractiveTextParameter("Format").setDefaultProvider(
                        (ctx)-> {
                            try {
                                return getDataAccess().getTitleDAO().get((String)ctx.getArgument("Name", String.class)).format();
                            } catch (DataAccessException e) {
                                throw new RuntimeException(e); // TODO what is the best think to handle in this error case
                            }}
                )))
                .addLine(new InteractiveParameterLine<>(new InteractiveStringParameter("Type").setSuggestionProvider(SuggestionProviders::titleType).setDefaultProvider(
                        (ctx)-> {
                            try {
                                return getDataAccess().getTitleDAO().get((String)ctx.getArgument("Name", String.class)).type().name();
                            } catch (DataAccessException e) {
                                throw new RuntimeException(e); // TODO what is the best think to handle in this error case
                            }}
                ).setValidator((x)->{
                    try {
                        Title.Type.valueOf(x);
                        return true;
                    }
                    catch (IllegalArgumentException ex) {
                        return false;
                    }

                })))
                .addLine(new InteractiveParameterLine<>(new InteractiveAdvancementParameter("Advancement").setOptional(true)
                        .setDefaultProvider((ctx) -> {
                            try {
                                TitleDAO titleDAO = getDataAccess().getTitleDAO();
                                return titleDAO.get(ctx.getArgument("Name", String.class)).advancement()
                                        .flatMap(id -> {
                                            try {
                                                return titleDAO.get(ctx.getArgument("Name", String.class)).advancement();
                                            } catch (DataAccessException e) {
                                                return Optional.empty();
                                            }
                                        })
                                        .flatMap(id -> Optional.ofNullable(ctx.getSource().getServer().getAdvancements().get(id)))
                                        .orElse(null);
                            } catch (DataAccessException e) {
                                throw new RuntimeException(e);
                            }
                        })))
                .addLine(new InteractiveFinishLine()).setDataHandler(AdminCommands::finishTitleEdit).register(dispatcher, registryAccess, PermissionCheckers.MODIFY_PERMISSION);



    }

    private static Integer finishTitleCreation(CommandContext<CommandSourceStack> ctx, Map<String, Object> parameters){
        String name = (String)parameters.get("Name");
        Component format = (Component) parameters.get("Format");
        String type = (String)parameters.get("Type");
        String description = (String)parameters.get("Description");
        AdvancementHolder advancementEntry = (AdvancementHolder) parameters.get("Advancement");
        Optional<Identifier> advancement = advancementEntry == null ? Optional.empty() : Optional.of(advancementEntry.id());

        Title newTitle = new Title(name, format, description, Title.Type.valueOf(type), advancement);

        ctx.getSource().sendSuccess(() -> Component.literal("Creating new title " + name + "..."), false);

        return perform(ctx, TitleUtilities.addTitle(newTitle),
                () -> Component.literal("Created new Title " + name),
                () -> Component.literal("Title " + name + " already exists"));
    }

    private static Integer finishTitleEdit(CommandContext<CommandSourceStack> ctx, Map<String, Object> parameters){
        String name = (String)parameters.get("Name");
        Component format = (Component) parameters.get("Format");
        String type = (String)parameters.get("Type");
        String description = (String)parameters.get("Description");
        AdvancementHolder advancementEntry = (AdvancementHolder) parameters.get("Advancement");
        Optional<Identifier> advancement = advancementEntry == null ? Optional.empty() : Optional.of(advancementEntry.id());

        Title newTitle = new Title(name, format, description, Title.Type.valueOf(type), advancement);

        ctx.getSource().sendSuccess(() -> Component.literal("Editing title " + name + "..."), false);

        return perform(ctx, TitleUtilities.editTitle(newTitle),
                () -> Component.literal("Successfully modified Title " + name),
                () -> Component.literal("Title " + name + " does not exist"));
    }

    /***
     * Gives a player a title.
     * Will fail if the player or the title doesn't exist.
     */
    public static Integer bestowTitle(CommandContext<CommandSourceStack> ctx) {
        String player = ctx.getArgument("playerName", String.class);
        String title = ctx.getArgument("title", String.class);

        ctx.getSource().sendSuccess(() -> Component.literal("Awarding title " + title + " to " + player + "..."), false);

        return perform(ctx, TitleUtilities.awardTitle(player, title),
                () -> Component.literal("Awarded title " + title + " to " + player),
                () -> Component.literal(player + " already has title " + title));
    }

    /***
     * Removes a title from a player.
     */
    public static Integer revokeTitle(CommandContext<CommandSourceStack> ctx) {
        String player = ctx.getArgument("playerName", String.class);
        String title = ctx.getArgument("title", String.class);

        ctx.getSource().sendSuccess(() -> Component.literal("Revoking title " + title + "from " + player + "..."), false);

        return perform(ctx, TitleUtilities.revokeTitle(player, title),
                () -> Component.literal("Removed title " + title + " from " + player),
                () -> Component.literal(player + " does not have title " + title));
    }

    /***
     * Removes a title from the system.
     */
    public static Integer removeTitle(CommandContext<CommandSourceStack> ctx) {
        String title = ctx.getArgument("title", String.class);

        ctx.getSource().sendSuccess(() -> Component.literal("Deleting title " + title + "..."), false);

        return perform(ctx, TitleUtilities.deleteTitle(title),
                () -> Component.literal("Deleted title " + title),
                () -> Component.literal("Title " + title + " does not exist"));
    }

    public static Integer clearWorldTitles(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(() -> Component.literal("Clearing world titles..."), false);
        return perform(ctx, TitleUtilities.clearWorldTitles(),
                () -> Component.literal("Cleared all world titles"),
                () -> Component.literal("This error message will never appear"));
    }
}
