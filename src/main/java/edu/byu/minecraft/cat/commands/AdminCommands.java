package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import edu.byu.minecraft.cat.commands.interactive.*;
import edu.byu.minecraft.cat.commands.interactive.parameters.*;
import edu.byu.minecraft.cat.dataaccess.*;
import edu.byu.minecraft.cat.model.*;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.time.LocalDate;
import java.util.*;

import static edu.byu.minecraft.cat.CivsAndTitles.getDataAccess;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AdminCommands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
//        dispatcher.register(literal("civ").then(literal("admin").requires(ServerCommandSource::isExecutedByPlayer).requires(PermissionCheckers::isAdmin)
//                .then(literal("nukeCiv")
//                        .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::allCivs).executes(AdminCommands::nukeCiv)))
//                .then(literal("changeCivOwner")
//                        .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::allCivs).then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::allPlayers).executes(AdminCommands::changeCivOwner))))
//                 .then(literal("respondCivRequests")
//                        .then(argument("requestId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::myJoinRequests).then(argument("accept", BoolArgumentType.bool()).executes(AdminCommands::respondCivRequest))))
//                .then(literal("listCivRequests").executes(AdminCommands::listCivRequests))
//                .then(literal("approve")
//                        .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::allRequestedCivs).executes(AdminCommands::approveCiv)))
//            ));
//        dispatcher.register(literal("build").then(literal("admin").requires(ServerCommandSource::isExecutedByPlayer).requires(PermissionCheckers::isAdmin)
//                .then(literal("toggleBuilds")
//                        .then(argument("buildId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::allBuilds).executes(AdminCommands::toggleBuilds)))
//                .then(literal("addBuild").executes(AdminCommands::addBuild))
//                .then(literal("deleteBuild")
//                        .then(argument("buildId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::allBuilds).executes(AdminCommands::deleteBuild)))
//                .then(literal("modifyBuild")
//                        .then(argument("buildId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::allBuilds).executes(AdminCommands::modifyBuild))
//                )));
        dispatcher.register(literal("titles").then(literal("admin").requires(ServerCommandSource::isExecutedByPlayer).requires(src -> src.hasPermissionLevel(2))

       //         .then(literal("addTitle").then(argument("TitleName", StringArgumentType.string()).then(argument("TitleType",StringArgumentType.string()).suggests(SuggestionProviders::titleType).then(argument("Description", StringArgumentType.greedyString()).executes(AdminCommands::addTitle)))))
                .then(literal("giveTitle").then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::allPlayers).then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::allTitles).executes(AdminCommands::bestowTitle))))
                .then(literal("revokeTitle").then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::allPlayers).then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::allTitles).executes(AdminCommands::revokeTitle))))
                .then(literal("deleteTitle").then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::allTitles).executes(AdminCommands::removeTitle)))
                .then(literal("clearWorldTitles").executes(AdminCommands::clearWorldTitles))
        ));

        new InteractiveManager(Arrays.asList("titles", "admin", "create"))
                .addLine(new InteractiveTextLine(Text.literal("Title Creation")))
                .addLine(new InteractiveParameterLine(new InteractiveStringParameter("Name").setValidater((x)-> {
                    try {
                        return getDataAccess().getTitleDAO().get((String)x) == null;
                    } catch (DataAccessException e) {
                        throw new RuntimeException(e); // TODO what is the best think to handle in this error case
                    }
                })))
                .addLine(new InteractiveParameterLine(new InteractiveStringParameter("Description", true)))
                .addLine(new InteractiveParameterLine(new InteractiveStringParameter("Format", true)))
                .addLine(new InteractiveParameterLine(new InteractiveStringParameter("Type").setSuggestionProvider(SuggestionProviders::titleType).setValidater((x)->{
                    try {
                        Title.Type.valueOf((String) x);
                        return true;
                    }
                    catch (IllegalArgumentException ex) {
                        return false;
                    }

                })))
                .addLine(new InteractiveFinishLine()).setDataHandler(AdminCommands::finishTitleCreation).register(dispatcher);

        InteractiveParameter tileNameParam = new InteractiveStringParameter("Name").setValidater((x)-> {
            try {
                return getDataAccess().getTitleDAO().get((String)x) != null;
            } catch (DataAccessException e) {
                throw new RuntimeException(e); // TODO what is the best think to handle in this error case
            }
        }).setSuggestionProvider(SuggestionProviders::allTitles);
        new InteractiveManager(Arrays.asList("titles", "admin", "edit")).setStartArg(tileNameParam)
                .addLine(new InteractiveTextLine(Text.literal("Title Edit")))
                .addLine(new InteractiveDisplayLine(tileNameParam))
                .addLine(new InteractiveParameterLine(new InteractiveStringParameter("Description", true).setDefaultProvider(
                        (ctx)-> {
                            try {
                            return getDataAccess().getTitleDAO().get((String)ctx.getArgument("Name", String.class)).description();
                        } catch (DataAccessException e) {
                            throw new RuntimeException(e); // TODO what is the best think to handle in this error case
                        }}
                )))
                .addLine(new InteractiveParameterLine(new InteractiveStringParameter("Format", true).setDefaultProvider(
                        (ctx)-> {
                            try {
                                return getDataAccess().getTitleDAO().get((String)ctx.getArgument("Name", String.class)).color();
                            } catch (DataAccessException e) {
                                throw new RuntimeException(e); // TODO what is the best think to handle in this error case
                            }}
                )))
                .addLine(new InteractiveParameterLine(new InteractiveStringParameter("Type").setSuggestionProvider(SuggestionProviders::titleType).setDefaultProvider(
                        (ctx)-> {
                            try {
                                return getDataAccess().getTitleDAO().get((String)ctx.getArgument("Name", String.class)).type().name();
                            } catch (DataAccessException e) {
                                throw new RuntimeException(e); // TODO what is the best think to handle in this error case
                            }}
                ).setValidater((x)->{
                    try {
                        Title.Type.valueOf((String) x);
                        return true;
                    }
                    catch (IllegalArgumentException ex) {
                        return false;
                    }

                })))
                .addLine(new InteractiveFinishLine()).setDataHandler(AdminCommands::finishTitleEdit).register(dispatcher);



    }


    private static Integer finishTitleCreation(CommandContext<ServerCommandSource> ctx, Map<String, Object> parameters){
        String name = (String)parameters.get("Name");
        String format = (String) parameters.get("Format");
        String type = (String)parameters.get("Type");
        String description = (String)parameters.get("Description");


        try {
            TitleDAO titles = getDataAccess().getTitleDAO();
            Title newTitle = new Title(name, format, description, Title.Type.valueOf(type));
            titles.insert(newTitle);

            if(newTitle.type() == Title.Type.DEFAULT) // if default grant to all players
            {
                Collection<Player> players = getDataAccess().getPlayerDAO().getAll();
                UnlockedTitleDAO unlockedTitleDAO = getDataAccess().getUnlockedTitleDAO();;
                for (Player x: players){
                    unlockedTitleDAO.insert(new UnlockedTitle(x.uuid(), name, LocalDate.now().toString()));
                }
            }
            ctx.getSource().sendFeedback(()->Text.literal("Created Title: " + name), false);
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }

        return 1;
    }

    private static Integer finishTitleEdit(CommandContext<ServerCommandSource> ctx, Map<String, Object> parameters){
        String name = (String)parameters.get("Name");
        String format = (String) parameters.get("Format");
        String type = (String)parameters.get("Type");
        String description = (String)parameters.get("Description");


        try {
            TitleDAO titles = getDataAccess().getTitleDAO();
            Title newTitle = new Title(name, format, description, Title.Type.valueOf(type));
            titles.update(newTitle);

            if(newTitle.type() == Title.Type.DEFAULT) // if default grant to all players
            {
                Collection<Player> players = getDataAccess().getPlayerDAO().getAll();
                UnlockedTitleDAO unlockedTitleDAO = getDataAccess().getUnlockedTitleDAO();
                List<UUID> alreadyUnlocked = unlockedTitleDAO.getAll(name).stream().map(UnlockedTitle::uuid).toList();
                for (Player x: players.stream().filter((x) -> !alreadyUnlocked.contains(x.uuid())).toList()){
                    unlockedTitleDAO.insert(new UnlockedTitle(x.uuid(), name, LocalDate.now().toString()));
                }
            }
            ctx.getSource().sendFeedback(()->Text.literal("Submitted Edit for Title: " + name), false);
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }

        return 1;
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
        try {
            Title titleObj = getDataAccess().getTitleDAO().get(title);
            if(titleObj == null)
            {
                ctx.getSource().sendFeedback(()->Text.literal("Unknown Title " + title), false);
                return 0;
            }
            UUID playerId = getDataAccess().getPlayerDAO().getPlayerUUID(player);
            UnlockedTitleDAO unlockedTitleDAO = getDataAccess().getUnlockedTitleDAO();

            UnlockedTitle unlockedTitle = new UnlockedTitle(playerId, title, LocalDate.now().toString());
            unlockedTitleDAO.insert(unlockedTitle);
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
        }
        ctx.getSource().sendFeedback(()-> Text.literal("Giving " + player + " title "+ title), false);
        return 1;
    }



    /***
     * Adds a title into the system.
     * @param ctx
     * @return
     */
    public static Integer addTitle(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()-> Text.literal("Adding Title"), false);
        String titleName = ctx.getArgument("TitleName", String.class);
        String titleDescription = ctx.getArgument("Description", String.class);
        String titleType = ctx.getArgument("Type", String.class);


        TitleDAO titleDAO;
        try {
            titleDAO = getDataAccess().getTitleDAO();
            if(titleDAO.get(titleName) != null){
                ctx.getSource().sendFeedback(()->Text.literal("Title with name "+ titleName + "already exists"), false);
                return 0;
            }
            Title.Type type = Title.Type.valueOf(titleType);
            titleDAO.update(new Title(titleName, "Blue", titleDescription, type));
            if(type == Title.Type.DEFAULT) // if default grant to all players
            {
                Collection<Player> players = getDataAccess().getPlayerDAO().getAll();
                UnlockedTitleDAO unlockedTitleDAO = getDataAccess().getUnlockedTitleDAO();;
                for (Player x: players){
                    unlockedTitleDAO.insert(new UnlockedTitle(x.uuid(), titleName, LocalDate.now().toString()));
                }
            }
        } catch (DataAccessException ex){
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        } catch (IllegalArgumentException ex){
            ctx.getSource().sendFeedback(()->Text.literal("Title type " + titleType + " is not a vaild type"), false);
            return 0;
        }



        return 1;
    }

    /***
     * Removes a title from a player.
     * @param ctx
     * @return
     */
    public static Integer revokeTitle(CommandContext<ServerCommandSource> ctx) {
        String player = ctx.getArgument("playerName", String.class);
        String title = ctx.getArgument("title", String.class);
        try {
            Title titleObj = getDataAccess().getTitleDAO().get(title);
            if(titleObj == null)
            {
                ctx.getSource().sendFeedback(()->Text.literal("Unknown Title " + title), false);
                return 0;
            }
            if(titleObj.type() == Title.Type.DEFAULT)
            {
                ctx.getSource().sendFeedback(()->Text.literal("Can't revoke default title: "+ title), false);
                return 0;
            }
            PlayerDAO playerDAO = getDataAccess().getPlayerDAO();
            UUID playerId = playerDAO.getPlayerUUID(player);
            Player playerObj = playerDAO.get(playerId);
            UnlockedTitleDAO unlockedTitleDAO = getDataAccess().getUnlockedTitleDAO();
            unlockedTitleDAO.delete(playerId, title);
            if(playerObj.title().equals(title)) // if the title is equiped remove it
            {
                playerObj = playerObj.setTitle(null);
                playerDAO.update(playerObj);
            }
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
        }
        ctx.getSource().sendFeedback(()-> Text.literal("removing " + player + " title "+ title), false);
        return 1;
    }

    /***
     * Removes a title from the system.
     * @param ctx
     * @return
     */
    public static Integer removeTitle(CommandContext<ServerCommandSource> ctx) {
        String title = ctx.getArgument("title", String.class);
        try {
            TitleDAO titleDAO = getDataAccess().getTitleDAO();
            Title titleObj = titleDAO.get(title);
            if(titleObj == null)
            {
                ctx.getSource().sendFeedback(()->Text.literal("Unknown Title " + title), false);
                return 0;
            }
            titleDAO.delete(title);

        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
        }
        ctx.getSource().sendFeedback(()-> Text.literal("removing title "+ title), false);
        return 1;
    }
    public static Integer clearWorldTitles(CommandContext<ServerCommandSource> ctx) {
        try {
            Collection<Title> titles = getDataAccess().getTitleDAO().getAll();
            UnlockedTitleDAO unlockedTitleDAO = getDataAccess().getUnlockedTitleDAO();
            PlayerDAO playerDAO = getDataAccess().getPlayerDAO();
            Collection<Player> players = playerDAO.getAll();
            titles.stream().filter((x) -> x.type() == Title.Type.WORLD).forEach((x) -> {
                try {
                    unlockedTitleDAO.deleteAll(x.title());
                    players.stream().filter((p) -> p.title().equals(x.title())).forEach(
                            (p) -> {
                                Player updatedPlayer = p.setTitle(null);
                                try {
                                    playerDAO.update(updatedPlayer);
                                } catch (DataAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );

                } catch (DataAccessException ignored) {
                }
            });


        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
        }
        ctx.getSource().sendFeedback(()-> Text.literal("removing world titles from all players" ), false);
        return 1;
    }

    private static Integer approveCiv(CommandContext<ServerCommandSource> ctx) {
        String civName = ctx.getArgument("civName", String.class);
        try {
            Optional<CivRequest> opRequest = getDataAccess().getCivRequestDAO().getAll().stream()
                    .filter(r -> r.name().equals(civName)).findAny();
            if(opRequest.isEmpty()) {
                ctx.getSource().sendFeedback(()->Text.literal("No civ request with that name"), false);
                return -1;
            }
            else {
                CivRequest request = opRequest.get();
                Civ approvedCiv = new Civ(0, request.name(), 0, true, true, request.locationID(), request.requestDate());
                int civID = getDataAccess().getCivDAO().insert(approvedCiv);
                getDataAccess().getCivParticipantDAO().insert(new CivParticipantPlayer(civID,
                        request.submitter(), CivParticipantPlayer.Status.FOUNDER));
                getDataAccess().getCivParticipantDAO().insert(new CivParticipantPlayer(civID,
                        request.submitter(), CivParticipantPlayer.Status.OWNER));
                getDataAccess().getCivRequestDAO().delete(request.ID());
                return 1;
            }
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }
    }

    private static String nukeConfirmationString = "";
    private static long nukeConfirmationInt = 0;
    /***
     * Deletes a civ from the database.
     * Must be run twice within one minute to execute.
     * Will fail if the civ does not exist.
     * @param ctx
     * @return
     */
    public static Integer nukeCiv(CommandContext<ServerCommandSource> ctx) {
        String civName = ctx.getArgument("civName", String.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Nuking Civ " + civName), false);
        return 1;
    }


    /***
     * Overrides the owner of a civ.
     * Must be run twice within one minute to execute.
     * Will fail if the civ doesn't exist or if the name given isn't a member of the civ.
     * @param ctx
     * @return
     */
    public static Integer changeCivOwner(CommandContext<ServerCommandSource> ctx) {
        String player = ctx.getArgument("playerName", String.class);
        String civ = ctx.getArgument("civName", String.class);
        ctx.getSource().sendFeedback(()-> Text.literal("change owner of " + civ + " to "+ player), false);
        return 1;
    }

    /***
     * Removes a build from the database.
     * Civs and players that benefited from the build will have their points reduced.
     * Deletes the associated build scores too.
     * Must be run twice within one minute to execute.
     * Will fail if the build doesn't exist.
     * @param ctx
     * @return
     */
    public static Integer deleteBuild(CommandContext<ServerCommandSource> ctx) {
        Integer buildId = ctx.getArgument("buildId", Integer.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Deleting build #" + buildId), false);
        return 1;
    }

    /***
     * Allows an admin to accept a civ request.
     * @param ctx
     * @return
     */
    public static Integer respondCivRequest(CommandContext<ServerCommandSource> ctx) {
        Integer requestId = ctx.getArgument("requestId", Integer.class);
        boolean accept = ctx.getArgument("accept", Boolean.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Response to request  " + requestId + " "+ accept), false);
        //TODO
        return 1;
    }

    /***
     * Lists all active civ requests with their IDs.
     * @param ctx
     * @return
     */
    public static Integer listCivRequests(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()-> Text.literal("Listing Civ requests"), false);
        return 1;
    }

    /***
     * Modifies a build in one of the following ways:
     * - Name
     * - Location
     * - Builders
     * - Score
     * - Civ
     * Will fail if any of the inputs are invalid
     * @param ctx
     * @return
     */
    public static Integer modifyBuild(CommandContext<ServerCommandSource> ctx) {
        Integer buildId = ctx.getArgument("buildId", Integer.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Modifying build #" + buildId), false);
        return 1;
    }

    /***
     * Activates builds upto the build ID, making them visible to the build judges.
     * @param ctx
     * @return
     */
    public static Integer toggleBuilds(CommandContext<ServerCommandSource> ctx) {
        Integer buildId = ctx.getArgument("buildId", Integer.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Toggling builds up to build #" + buildId), false);
        return 1;
    }

    /***
     * Injects a build into the system. It bypasses the build judging system.
     * @param ctx
     * @return
     */
    public static Integer addBuild(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()-> Text.literal("Adding build"), false);
        return 1;
    }


}
