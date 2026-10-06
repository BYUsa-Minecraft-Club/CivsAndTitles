package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.commands.interactive.InteractiveDisplay;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.TitleDAO;
import edu.byu.minecraft.cat.dataaccess.UnlockedTitleDAO;
import edu.byu.minecraft.cat.model.Player;
import edu.byu.minecraft.cat.model.Title;
import edu.byu.minecraft.cat.model.UnlockedTitle;
import edu.byu.minecraft.cat.util.TitleUtilities;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static edu.byu.minecraft.cat.util.CommandUtilities.perform;

public class TitleCommands {
    public static void registerCommands(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext registryAccess,
            Commands.CommandSelection environment
    ) {
        dispatcher.register(literal("titles").requires(CommandSourceStack::isPlayer)
        //        .then(literal("list").executes(TitleCommands::listTitles))
                .then(literal("clear")
                        .requires(PermissionCheckers.APPLY_PERMISSION)
                        .executes(TitleCommands::clearTitle))
                .then(literal("change")
                        .requires(PermissionCheckers.APPLY_PERMISSION)
                        .then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::myTitles).executes(TitleCommands::changeTitle)))
//
//                .then(literal("showRank")
//                        .then(argument("showRank", BoolArgumentType.bool()).executes(TitleCommands::showRank)))
        );

        new InteractiveDisplay<>(Arrays.asList("titles", "display"), new InteractiveDisplay.DisplayProvider<String, Title>() {
            @Override
            public Component getSimpleText(Title title, CommandContext<CommandSourceStack> ctx) {
                MutableComponent text = Component.empty();
                text.append(title.format());
                text.append(Component.literal(" - "));
                ServerPlayer player = ctx.getSource().getPlayer();
                try {
                    UnlockedTitleDAO dao = CivsAndTitles.getDataAccess().getUnlockedTitleDAO();
                    Collection<UnlockedTitle> titles = dao.getAll(player.getUUID());
                    String equippedTitle = CivsAndTitles.getDataAccess().getPlayerDAO().get(player.getUUID()).title();
                    if (title.title().equals(equippedTitle)) {
                        text.append(Component.literal("Active"));
                    } else if (titles.stream().anyMatch((x) -> x.title().equals(title.title()))) {
                        text.append(Component.literal("Unlocked"));
                    } else {
                        text.append(Component.literal("Not Unlocked"));
                    }
                } catch (DataAccessException e) {
                    ctx.getSource().sendSuccess(() -> Component.literal("Unable to access the database. Try again later."), false);
                }
                return text;
            }

            @Override
            public Component getDetailedText(Title title, CommandContext<CommandSourceStack> ctx) {
                MutableComponent text = Component.empty();
                text.append(title.format());// Text.literal(title.title()).setStyle(Style.EMPTY.withColor(Formatting.valueOf(title.format().toUpperCase())).withBold(Boolean.TRUE)));
                text.append(Component.literal("\n"));
                text.append(Component.literal("Type: " + title.type().name() + "\n").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));
                text.append(Component.literal(title.description() + "\n"));
                ServerPlayer player = ctx.getSource().getPlayer();
                try {
                    UnlockedTitleDAO dao = CivsAndTitles.getDataAccess().getUnlockedTitleDAO();
                    Player playerInfo = CivsAndTitles.getDataAccess().getPlayerDAO().get(player.getUUID());

                    Collection<UnlockedTitle> titles = dao.getAll(player.getUUID());
                    List<UnlockedTitle> titleList = titles.stream().filter((x) -> x.title().equals(title.title())).toList();
                    if (title.title().equals(playerInfo.title())) {
                        text.append(Component.literal("Active"));
                        text.append("\n");
                        text.append(Component.literal("(Remove)").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withClickEvent(new ClickEvent.RunCommand("titles clear"))));

                    } else if (!titleList.isEmpty()) {
                        text.append(Component.literal("Unlocked " + titleList.getFirst().earned()));
                        text.append("\n");
                        text.append(Component.literal("(Set Active)").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withClickEvent(new ClickEvent.RunCommand("titles change " + title.title()))));
                    } else {
                        text.append(Component.literal("Not Unlocked"));
                    }
                    text.append("\n");
                    if (player.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.MODERATORS))) {
                        text.append(Component.literal("(Edit)").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withClickEvent(new ClickEvent.RunCommand("titles admin edit start " + title.title()))));
                    }
                } catch (DataAccessException e) {
                    ctx.getSource().sendSuccess(() -> Component.literal("Unable to access the database. Try again later."), false);
                }
                return text;
            }

            @Override
            public Collection<Title> getValues(CommandContext<CommandSourceStack> ctx) {
                TitleDAO titleDAO;
                try {
                    titleDAO = CivsAndTitles.getDataAccess().getTitleDAO();
                    return titleDAO.getAll();
                } catch (DataAccessException e) {
                    ctx.getSource().sendSuccess(() -> Component.literal("Unable to access the database. Try again later."), false);
                }
                return null;
            }

            @Override
            public Collection<String> getKeys(CommandContext<CommandSourceStack> ctx) {
                TitleDAO titleDAO;
                try {
                    titleDAO = CivsAndTitles.getDataAccess().getTitleDAO();
                    return titleDAO.getAll().stream().map(Title::title).toList();
                } catch (DataAccessException e) {
                    ctx.getSource().sendSuccess(() -> Component.literal("Unable to access the database. Try again later."), false);
                }
                return null;
            }

            @Override
            public Title getValue(String key) {
                TitleDAO titleDAO;
                try {
                    titleDAO = CivsAndTitles.getDataAccess().getTitleDAO();
                    return titleDAO.get(key);
                } catch (DataAccessException e) {
                    return null;
                }
            }

            @Override
            public String getKey(Title value) {
                return value.title();
            }
        }, new InteractiveDisplay.KeyInfo<String>() {
            @Override
            public String extractKey(CommandContext<CommandSourceStack> ctx) {
                return ctx.getArgument("title", String.class);
            }

            @Override
            public String getKeyName() {
                return "title";
            }

            @Override
            public ArgumentType<?> getArgumentType() {
                return StringArgumentType.string();
            }
        }).register(dispatcher);
    }

    public static Integer listTitles(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(()-> Component.literal("List titles"), false);
        try {
           Collection<Title> titles = CivsAndTitles.getDataAccess().getTitleDAO().getAll();
           for(Title title: titles)
           {
                ctx.getSource().sendSuccess(()-> Component.literal(title.title() + "  -  " + title.description()), false);
           }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    public static Integer changeTitle(CommandContext<CommandSourceStack> ctx) {
        String title = ctx.getArgument("title", String.class);

        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) {
            ctx.getSource().sendFailure(Component.literal("Must be a player to have a title"));
            return 0;
        }

        ctx.getSource().sendSuccess(()->Component.literal("Applying title " + title + "..."), false);

        return perform(ctx, TitleUtilities.applyTitle(player.getUUID(), title),
                () -> Component.literal("Applied title " + title),
                () -> Component.literal("You do not own that title"));
    }

    public static Integer clearTitle(CommandContext<CommandSourceStack> ctx) {

        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) {
            ctx.getSource().sendFailure(Component.literal("Must be a player to have a title"));
            return 0;
        }

        ctx.getSource().sendSuccess(()->Component.literal("Clearing current title..."), false);

        return perform(ctx, TitleUtilities.clearTitle(player.getUUID()),
                () -> Component.literal("Removed active title"),
                () -> Component.literal("This error should never display"));
    }
}
