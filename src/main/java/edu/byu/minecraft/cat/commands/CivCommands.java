package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.CivDAO;
import edu.byu.minecraft.cat.dataaccess.CivRequestDAO;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Civ;
import edu.byu.minecraft.cat.model.CivRequest;
import edu.byu.minecraft.cat.util.Utilities;
import edu.byu.minecraft.cat.util.CommandUtilities;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.TreeSet;
public class CivCommands {
    /***
     * Sets up all of the commands in this class
     * @param dispatcher the dispatcher
     * @param registryAccess the registry access
     * @param environment the environment
     */
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("civ").requires(ServerCommandSource::isExecutedByPlayer)
                        .then(literal("join")
                                .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::allCivs).executes(CivCommands::joinCiv)))
                        .then(literal("create")
                                .then(argument("civName", StringArgumentType.string()).executes(CivCommands::createCiv)))
                        .then(literal("leave")
                                .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::myCivs).requires(PermissionCheckers::isInCiv).executes(CivCommands::leaveCiv)))
        );
    }

    /***
     * Creates a request to create a civ
     * It will fail if run by the console or if a civ or civ request already exists.
     * @param ctx the context in which the command was run.
     * @return 1 upon a success.
     */
    public static Integer createCiv(CommandContext<ServerCommandSource> ctx) {
        String civName = ctx.getArgument("civName", String.class);
        CivDAO civDAO;
        CivRequestDAO civRequestDAO;
        try {
            civDAO = CivsAndTitles.getDataAccess().getCivDAO();
            civRequestDAO = CivsAndTitles.getDataAccess().getCivRequestDAO();
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }

        // Checks that the source is a player (Can't be run by the console)
        if (!ctx.getSource().isExecutedByPlayer()) {
            ctx.getSource().sendFeedback(()->Text.literal("This command can only be executed by a player"), false);
            return 0;
        }

        // Check that there doesn't already exist a civ or civ request with the given name
        TreeSet<Civ> civs;
        TreeSet<CivRequest> requests;
        try {
            civs = (TreeSet<Civ>) civDAO.getAll();
            requests = (TreeSet<CivRequest>) civRequestDAO.getAll();
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }
        for (Civ civ : civs) {
            if (civName.equalsIgnoreCase(civ.name())) {
                ctx.getSource().sendFeedback(()->Text.literal("A civ with the name " + civName + " already exists."), false);
            }
        }
        for (CivRequest request : requests) {
            if (civName.equalsIgnoreCase(request.name())) {
                ctx.getSource().sendFeedback(()->Text.literal("A civ with the name " + civName + " already exists."), false);
            }
        }

        ServerPlayerEntity player = ctx.getSource().getPlayer();

        CivRequest request = new CivRequest(0, Utilities.getTime(), player.getUuid(), civName, Utilities.getPlayerLocation(player));
        try {
            int id = civRequestDAO.insert(request);
            ctx.getSource().sendFeedback(()->Text.literal("Creating a new civ request for " + civName + "with ID" + id), false);
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }

        ctx.getSource().sendFeedback(()->Text.literal("Creating Civ " + civName), false);
        return 1;
    }

    /***
     * Creates a request to join a civ
     * Will fail if run the console, if the civ does not exist, if the player already requested to join the civ, or if the player is part of the civ
     * @param ctx the context in which the command was run
     * @return 1 upon a success
     */
    public static Integer joinCiv(CommandContext<ServerCommandSource> ctx) {
        String civName = ctx.getArgument("civName", String.class);

        // Checks that the source is a player (Can't be run by the console)
        if (!ctx.getSource().isExecutedByPlayer()) {
                CommandUtilities.printFeedback(ctx, "This command must be executed by a player");
            return 0;
        }

        // Gets the Civ
        CivDAO civDAO;
        Civ civ;
        try {
            civDAO = CivsAndTitles.getDataAccess().getCivDAO();
            civ = civDAO.getForName(civName);
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }

        // Checks that the civ exists
        if (civ == null) {
            CommandUtilities.printFeedback(ctx, civName + " does not exist");
            return 0;
        }

        // Checks that the civ is active
        if (!civ.isActive()) {
            CommandUtilities.printFeedback(ctx, civName + " is not an active civ. Contact an Admin for more details");
            return 0;
        }

        // Checks if the player is part of the civ already
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        // TODO: Finish

        // Checks if the player already requested to join the civ
        TreeSet<CivRequest> requestsByPlayer;
        CivRequestDAO requestDAO;
        try {
             requestDAO = CivsAndTitles.getDataAccess().getCivRequestDAO();
             requestsByPlayer = (TreeSet<CivRequest>) requestDAO.getForPlayer(player.getUuid());
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }
        // TODO: Finish

        ctx.getSource().sendFeedback(()->Text.literal("Joining Civ " + civName), false);
        return 1;
    }

    /***
     * Leaves a civ if you are a member of the civ.
     * Will fail if you are the owner of the civ.
     * @param ctx the context in which the command was run.
     * @return 1 upon a success
     */
    public static Integer leaveCiv(CommandContext<ServerCommandSource> ctx) {
        String civName = ctx.getArgument("civName", String.class);
        ctx.getSource().sendFeedback(()->Text.literal("leave Civ "+ civName), false);
        // Checks that the source is a player (Can't be run by the console)
        if (!ctx.getSource().isExecutedByPlayer()) {
            ctx.getSource().sendFeedback(()->Text.literal("This command can only be executed by a player"), false);
            return 0;
        }
        return 1;
    }

    /***
     * lists all the active civs on the server
     * @param ctx the context returned by the server
     * @return 1 if successful, 0 upon a database error
     */
    public static Integer listCivs(CommandContext<ServerCommandSource> ctx) {
        CivDAO civDAO;
        TreeSet<Civ> civs;
        try {
            civDAO = CivsAndTitles.getDataAccess().getCivDAO();
            civs = (TreeSet<Civ>)civDAO.getAll();
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }
        if (civs.isEmpty()) {
            ctx.getSource().sendFeedback(()->Text.literal("No civs have been founded yet. Maybe you'll be the first?"), false);
            return 0;
        }
        for (Civ civ : civs) {
            if (!civ.isActive()) { continue; }
            ctx.getSource().sendFeedback(()->Text.literal(civ.name()), false);
        }
        return 1;
    }

    /***
     * Cancels a civ join request.
     * Will fail if executed by the server or if the request doesn't exist
     * @param ctx the context in which the command was run.
     * @return 1 upon a success.
     */
    public static Integer cancelJoinRequest(CommandContext<ServerCommandSource> ctx) {
        CommandUtilities.printFeedback(ctx, "You called the command :)");
        return 0;
    }

    /***
     * Cancels a civ create request.
     * Will fail if executed by the server or if the request doesn't exist.
     * @param ctx the context in which the command was run.
     * @return 1 upon a success.
     */
    public static Integer cancelCreateRequest(CommandContext<ServerCommandSource> ctx) {
        CommandUtilities.printFeedback(ctx, "You called the command :)");
        return 0;
    }

    /***
     * Lists all information about a civ, including its location, membership, status, and builds.
     * Will fail if the civ does not exist.
     * @param ctx the context in which the command was run.
     * @return 1 upon a success.
     */
    public static Integer info(CommandContext<ServerCommandSource> ctx) {
        CommandUtilities.printFeedback(ctx, "You called the command :)");
        return 0;
    }

    /***
     * Lists all civ join and civ create requests by the player.
     * If executed by the console, it will show all active requests.
     * @param ctx
     * @return
     */
    public static Integer listRequests(CommandContext<ServerCommandSource> ctx) {
        CommandUtilities.printFeedback(ctx, "You called the command :)");
        return 0;
    }
}
