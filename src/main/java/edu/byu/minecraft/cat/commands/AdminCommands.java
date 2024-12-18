package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Civ;
import edu.byu.minecraft.cat.model.CivParticipantPlayer;
import edu.byu.minecraft.cat.model.CivRequest;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.stream.Stream;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AdminCommands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("civ").then(literal("admin").requires(ServerCommandSource::isExecutedByPlayer).requires(PermissionCheckers::isAdmin)
                .then(literal("nukeCiv")
                        .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::allCivs).executes(AdminCommands::nukeCiv)))
                .then(literal("changeCivOwner")
                        .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::allCivs).then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::allPlayers).executes(AdminCommands::changeCivOwner))))
                 .then(literal("respondCivRequests")
                        .then(argument("requestId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::myJoinRequests).then(argument("accept", BoolArgumentType.bool()).executes(AdminCommands::respondCivRequest))))
                .then(literal("listCivRequests").executes(AdminCommands::listCivRequests))
                .then(literal("approve")
                        .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::allRequestedCivs).executes(AdminCommands::approveCiv)))
            ));
        dispatcher.register(literal("build").then(literal("admin").requires(ServerCommandSource::isExecutedByPlayer).requires(PermissionCheckers::isAdmin)
                .then(literal("toggleBuilds")
                        .then(argument("buildId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::allBuilds).executes(AdminCommands::toggleBuilds)))
                .then(literal("addBuild").executes(AdminCommands::addBuild))
                .then(literal("deleteBuild")
                        .then(argument("buildId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::allBuilds).executes(AdminCommands::deleteBuild)))
                .then(literal("modifyBuild")
                        .then(argument("buildId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::allBuilds).executes(AdminCommands::modifyBuild))
                )));
        dispatcher.register(literal("titles").then(literal("admin").requires(ServerCommandSource::isExecutedByPlayer).requires(PermissionCheckers::isAdmin)

                        .then(literal("addTitle").executes(AdminCommands::addBuild))
                .then(literal("giveTitle").then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::allPlayers).then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::allTitles).executes(AdminCommands::bestowTitle))))
        ));
    }

    private static Integer approveCiv(CommandContext<ServerCommandSource> ctx) {
        String civName = ctx.getArgument("civName", String.class);
        try {
            Optional<CivRequest> opRequest = CivsAndTitles.getDataAccess().getCivRequestDAO().getAll().stream()
                    .filter(r -> r.name().equals(civName)).findAny();
            if(opRequest.isEmpty()) {
                ctx.getSource().sendFeedback(()->Text.literal("No civ request with that name"), false);
                return -1;
            }
            else {
                CivRequest request = opRequest.get();
                Civ approvedCiv = new Civ(0, request.name(), 0, true, true, request.locationID(), request.requestDate());
                int civID = CivsAndTitles.getDataAccess().getCivDAO().insert(approvedCiv);
                CivsAndTitles.getDataAccess().getCivParticipantDAO().insert(new CivParticipantPlayer(civID,
                        request.submitter(), CivParticipantPlayer.Status.FOUNDER));
                CivsAndTitles.getDataAccess().getCivParticipantDAO().insert(new CivParticipantPlayer(civID,
                        request.submitter(), CivParticipantPlayer.Status.OWNER));
                CivsAndTitles.getDataAccess().getCivRequestDAO().delete(request.ID());
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
     * Gives a player a title.
     * Will fail if the player or the title doesn't exist.
     * @param ctx
     * @return
     */
    public static Integer bestowTitle(CommandContext<ServerCommandSource> ctx) {
        String player = ctx.getArgument("playerName", String.class);
        String title = ctx.getArgument("title", String.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Giving " + player + "title "+ title), false);
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

    /***
     * Adds a title into the system.
     * @param ctx
     * @return
     */
    public static Integer addTitle(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()-> Text.literal("Adding Title"), false);
        return 1;
    }

    /***
     * Deletes a title from the system.
     * @param ctx
     * @return
     */
    public static Integer removeTitle(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()-> Text.literal("Adding Title"), false);
        return 1;
    }
}
