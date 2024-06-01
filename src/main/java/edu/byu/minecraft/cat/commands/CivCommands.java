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
import edu.byu.minecraft.cat.Utility;
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
     * Allows a player to create a civ.
     * @param ctx the context in which the command was created
     * @return 1 upon a success
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

        CivRequest request = new CivRequest(0, Utility.getTime(), player.getUuid(), civName, Utility.getPlayerLocation(player));
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

    public static Integer joinCiv(CommandContext<ServerCommandSource> ctx) {
        String civName = ctx.getArgument("civName", String.class);
        ctx.getSource().sendFeedback(()->Text.literal("Joining Civ " + civName), false);
        return 1;
    }
    public static Integer leaveCiv(CommandContext<ServerCommandSource> ctx) {
        String civName = ctx.getArgument("civName", String.class);
        ctx.getSource().sendFeedback(()->Text.literal("leave Civ "+ civName), false);
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
            if (!civ.active()) { continue; }
            ctx.getSource().sendFeedback(()->Text.literal(civ.name()), false);
        }
        return 1;
    }


}
