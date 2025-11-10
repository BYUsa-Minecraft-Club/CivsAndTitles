package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.commands.interactive.InteractiveDisplay;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.PlayerDAO;
import edu.byu.minecraft.cat.dataaccess.TitleDAO;
import edu.byu.minecraft.cat.dataaccess.UnlockedTitleDAO;
import edu.byu.minecraft.cat.model.Player;
import edu.byu.minecraft.cat.model.Title;
import edu.byu.minecraft.cat.model.UnlockedTitle;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TitleCommands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("titles").requires(ServerCommandSource::isExecutedByPlayer)
        //        .then(literal("list").executes(TitleCommands::listTitles))
                .then(literal("clear").executes(TitleCommands::clearTitle))
                .then(literal("change")
                        .then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::myTitles).requires(PermissionCheckers::hasTitle).executes(TitleCommands::changeTitle)))
//
//                .then(literal("showRank")
//                        .then(argument("showRank", BoolArgumentType.bool()).executes(TitleCommands::showRank)))
        );

        new InteractiveDisplay<String, Title>(Arrays.asList("titles", "display"), new InteractiveDisplay.DisplayProvider<String, Title>() {
            @Override
            public Text getSimpleText(Title title , CommandContext<ServerCommandSource> ctx) {
                MutableText text = Text.empty();
                text.append(title.format());//Text.literal(title.title()).setStyle(Style.EMPTY.withColor(Formatting.valueOf(title.format().toUpperCase())).withBold(Boolean.TRUE)));
                text.append(Text.literal(" - "));
                ServerPlayerEntity player = ctx.getSource().getPlayer();
                try {
                    UnlockedTitleDAO dao = CivsAndTitles.getDataAccess().getUnlockedTitleDAO();
                    Collection<UnlockedTitle> titles = dao.getAll(player.getUuid());
                    String equippedTitle = CivsAndTitles.getDataAccess().getPlayerDAO().get(player.getUuid()).title();
                    if(title.title().equals(equippedTitle)){
                        text.append(Text.literal("Active"));
                    } else if(titles.stream().anyMatch((x)-> x.title().equals(title.title()))){
                        text.append(Text.literal("Unlocked"));
                    }
                    else {
                        text.append(Text.literal("Not Unlocked"));
                    }
                } catch (DataAccessException e) {
                    ctx.getSource().sendFeedback(() -> Text.literal("Unable to access the database. Try again later."), false);
                }
                return text;
            }

            @Override
            public Text getDetailedText(Title title, CommandContext<ServerCommandSource> ctx) {
                MutableText text = Text.empty();
                text.append(title.format());// Text.literal(title.title()).setStyle(Style.EMPTY.withColor(Formatting.valueOf(title.format().toUpperCase())).withBold(Boolean.TRUE)));
                text.append(Text.literal( "\n"));
                text.append(Text.literal( "Type: " + title.type().name() +"\n").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
                text.append(Text.literal(title.description() + "\n"));
                ServerPlayerEntity player = ctx.getSource().getPlayer();
                try {
                    UnlockedTitleDAO dao = CivsAndTitles.getDataAccess().getUnlockedTitleDAO();
                    Player playerInfo = CivsAndTitles.getDataAccess().getPlayerDAO().get(player.getUuid());

                    Collection<UnlockedTitle> titles = dao.getAll(player.getUuid());
                    List<UnlockedTitle> titleList = titles.stream().filter((x)-> x.title().equals(title.title())).toList();
                    if(title.title().equals(playerInfo.title())){
                        text.append(Text.literal("Active"));
                        text.append("\n");
                        text.append(Text.literal("(Remove)").setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent.RunCommand("titles clear"))));

                    } else if(!titleList.isEmpty()){
                        text.append(Text.literal("Unlocked " + titleList.getFirst().earned()));
                        text.append("\n");
                        text.append(Text.literal("(Set Active)").setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent.RunCommand("titles change " +title.title()))));
                    }
                    else {
                        text.append(Text.literal("Not Unlocked"));
                    }
                    text.append("\n");
                    if(player.getPermissionLevel() >= 2) {
                        text.append(Text.literal("(Edit)").setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent.RunCommand("titles admin edit " +title.title()))));
                    }
                } catch (DataAccessException e) {
                    ctx.getSource().sendFeedback(() -> Text.literal("Unable to access the database. Try again later."), false);
                }
                return text;
            }

            @Override
            public Collection<Title> getValues(CommandContext<ServerCommandSource> ctx) {
                TitleDAO titleDAO;
                try {
                    titleDAO = CivsAndTitles.getDataAccess().getTitleDAO();
                    return titleDAO.getAll();
                } catch (DataAccessException e) {
                    ctx.getSource().sendFeedback(() -> Text.literal("Unable to access the database. Try again later."), false);
                }
                return null;
            }

            @Override
            public Collection<String> getKeys(CommandContext<ServerCommandSource> ctx) {
                TitleDAO titleDAO;
                try {
                    titleDAO = CivsAndTitles.getDataAccess().getTitleDAO();
                    return titleDAO.getAll().stream().map(Title::title).toList();
                } catch (DataAccessException e) {
                    ctx.getSource().sendFeedback(() -> Text.literal("Unable to access the database. Try again later."), false);
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
            public String extractKey(CommandContext<ServerCommandSource> ctx) {
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

    public static Integer listTitles(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()-> Text.literal("List titles"), false);
        try {
           Collection<Title> titles = CivsAndTitles.getDataAccess().getTitleDAO().getAll();
           for(Title title: titles)
           {
                ctx.getSource().sendFeedback(()-> Text.literal(title.title() + "  -  " + title.description()), false);
           }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    public static Integer changeTitle(CommandContext<ServerCommandSource> ctx) {
        String title = ctx.getArgument("title", String.class);
        ctx.getSource().sendFeedback(()->Text.literal("Change title to " + title), false);

        Entity entity = ctx.getSource().getEntity();
        if (entity == null)
        {
            return 0;
        }

        try {
            PlayerDAO playerDAO = CivsAndTitles.getDataAccess().getPlayerDAO();
            Player player = playerDAO.get(entity.getUuid());
            Collection<UnlockedTitle> validTitle = CivsAndTitles.getDataAccess().getUnlockedTitleDAO().getAll(entity.getUuid());
            if(validTitle.stream().anyMatch(ut->ut.title().equals(title)))
            {
                player = player.setTitle(title);
                playerDAO.update(player);
            }
            else
            {
                ctx.getSource().sendFeedback(()->Text.literal("Not an unlocked title: " + title), false);
                return 0;
            }

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    public static Integer clearTitle(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()->Text.literal("Remove active title"), false);
        Entity entity = ctx.getSource().getEntity();
        if (entity == null)
        {
            return 0;
        }
        try {
            PlayerDAO playerDAO = CivsAndTitles.getDataAccess().getPlayerDAO();
            Player player = playerDAO.get(entity.getUuid());
            player = player.setTitle(null);
            playerDAO.update(player);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

//    public static Integer showRank(CommandContext<ServerCommandSource> ctx) {
//        Boolean showRank = ctx.getArgument("showRank", Boolean.class);
//        ctx.getSource().sendFeedback(()->Text.literal("Change show rank to " + showRank), false);
//        return 1;
//    }



}
