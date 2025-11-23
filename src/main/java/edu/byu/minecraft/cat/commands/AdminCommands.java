package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import edu.byu.minecraft.cat.commands.interactive.*;
import edu.byu.minecraft.cat.commands.interactive.parameters.*;
import edu.byu.minecraft.cat.dataaccess.*;
import edu.byu.minecraft.cat.model.*;
import edu.byu.minecraft.cat.util.TitleUtilities;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

import static edu.byu.minecraft.cat.CivsAndTitles.getDataAccess;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static edu.byu.minecraft.cat.util.CommandUtilities.perform;

public class AdminCommands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("titles").then(literal("admin").requires(ServerCommandSource::isExecutedByPlayer).requires(src -> src.hasPermissionLevel(2))

                .then(literal("giveTitle").then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::allPlayers).then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::playerUnawardedTitles).executes(AdminCommands::bestowTitle))))
                .then(literal("revokeTitle").then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::allPlayers).then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::playersRemovableTitles).executes(AdminCommands::revokeTitle))))
                .then(literal("deleteTitle").then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::allTitles).executes(AdminCommands::removeTitle)))
                .then(literal("clearWorldTitles").executes(AdminCommands::clearWorldTitles))
        ));

        new InteractiveManager(Arrays.asList("titles", "admin", "create"))
                .addLine(new InteractiveTextLine(Text.literal("Title Creation")))
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
                .addLine(new InteractiveFinishLine()).setDataHandler(AdminCommands::finishTitleCreation).register(dispatcher, registryAccess);

        InteractiveParameter<String> titleNameParam = new InteractiveStringParameter("Name").setValidator((x)-> {
            try {
                return getDataAccess().getTitleDAO().get((String)x) != null;
            } catch (DataAccessException e) {
                throw new RuntimeException(e); // TODO what is the best think to handle in this error case
            }
        }).setSuggestionProvider(SuggestionProviders::allTitles);
        new InteractiveManager(Arrays.asList("titles", "admin", "edit")).setStartArg(titleNameParam)
                .addLine(new InteractiveTextLine(Text.literal("Title Edit")))
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
                                Optional<Identifier> advancement = getDataAccess().getTitleDAO().get(ctx.getArgument("Name", String.class)).advancement();
                                if (advancement.isPresent()) return new AdvancementEntry(getDataAccess().getTitleDAO().get(ctx.getArgument("Name", String.class)).advancement().orElse(null), null);
                                else return null;
                            } catch (DataAccessException e) {
                                throw new RuntimeException(e);
                            }
                        })))
                .addLine(new InteractiveFinishLine()).setDataHandler(AdminCommands::finishTitleEdit).register(dispatcher, registryAccess);



    }

    private static Integer finishTitleCreation(CommandContext<ServerCommandSource> ctx, Map<String, Object> parameters){
        String name = (String)parameters.get("Name");
        Text format = (Text) parameters.get("Format");
        String type = (String)parameters.get("Type");
        String description = (String)parameters.get("Description");
        AdvancementEntry advancementEntry = (AdvancementEntry) parameters.get("Advancement");
        Optional<Identifier> advancement = advancementEntry == null ? Optional.empty() : Optional.of(advancementEntry.id());

        Title newTitle = new Title(name, format, description, Title.Type.valueOf(type), advancement);

        ctx.getSource().sendFeedback(() -> Text.of("Creating new title " + name + "..."), false);

        return perform(ctx, TitleUtilities.addTitle(newTitle),
                () -> Text.of("Created new Title " + name),
                () -> Text.of("Title " + name + " already exists"));
    }

    private static Integer finishTitleEdit(CommandContext<ServerCommandSource> ctx, Map<String, Object> parameters){
        String name = (String)parameters.get("Name");
        Text format = (Text) parameters.get("Format");
        String type = (String)parameters.get("Type");
        String description = (String)parameters.get("Description");
        AdvancementEntry advancementEntry = (AdvancementEntry) parameters.get("Advancement");
        Optional<Identifier> advancement = advancementEntry == null ? Optional.empty() : Optional.of(advancementEntry.id());

        Title newTitle = new Title(name, format, description, Title.Type.valueOf(type), advancement);

        ctx.getSource().sendFeedback(() -> Text.of("Editing title " + name + "..."), false);

        return perform(ctx, TitleUtilities.editTitle(newTitle),
                () -> Text.of("Successfully modified Title " + name),
                () -> Text.of("Title " + name + " does not exist"));
    }

    /***
     * Gives a player a title.
     * Will fail if the player or the title doesn't exist.
     * @param ctx
     * @return
     */
    public static Integer bestowTitle(CommandContext<ServerCommandSource> ctx) {
        String player = ctx.getArgument("playerName", String.class);
        String title = ctx.getArgument("title", String.class);

        ctx.getSource().sendFeedback(() -> Text.of("Awarding title " + title + " to " + player + "..."), false);

        return perform(ctx, TitleUtilities.awardTitle(player, title),
                () -> Text.of("Awarded title " + title + " to " + player),
                () -> Text.of(player + " already has title " + title));
    }

    /***
     * Removes a title from a player.
     * @param ctx
     * @return
     */
    public static Integer revokeTitle(CommandContext<ServerCommandSource> ctx) {
        String player = ctx.getArgument("playerName", String.class);
        String title = ctx.getArgument("title", String.class);

        ctx.getSource().sendFeedback(() -> Text.of("Revoking title " + title + "from " + player + "..."), false);

        return perform(ctx, TitleUtilities.revokeTitle(player, title),
                () -> Text.of("Removed title " + title + " from " + player),
                () -> Text.of(player + " does not have title " + title));
    }

    /***
     * Removes a title from the system.
     * @param ctx
     * @return
     */
    public static Integer removeTitle(CommandContext<ServerCommandSource> ctx) {
        String title = ctx.getArgument("title", String.class);

        ctx.getSource().sendFeedback(() -> Text.of("Deleting title " + title + "..."), false);

        return perform(ctx, TitleUtilities.deleteTitle(title),
                () -> Text.of("Deleted title " + title),
                () -> Text.of("Title " + title + " does not exist"));
    }

    public static Integer clearWorldTitles(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(() -> Text.of("Clearing world titles..."), false);
        return perform(ctx, TitleUtilities.clearWorldTitles(),
                () -> Text.of("Cleared all world titles"),
                () -> Text.of("This error message will never appear"));
    }
}
