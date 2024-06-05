package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.CivDAO;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.JoinRequestDAO;
import edu.byu.minecraft.cat.model.Civ;
import edu.byu.minecraft.cat.model.JoinRequest;
import edu.byu.minecraft.cat.util.CivUtilities;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CivLeaderCommands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("civ").requires(ServerCommandSource::isExecutedByPlayer).requires(PermissionCheckers::isCivLeader)
                .then(literal("respondJoinRequest")
                        .then(argument("requestId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::myJoinRequests).then(argument("accept", BoolArgumentType.bool()).executes(CivLeaderCommands::respondJoinRequest))))
                .then(literal("listJoinRequests").executes(CivLeaderCommands::listJoinRequests))
                .then(literal("addLeader")
                        .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::ledCivs)
                        .then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::civPlayers).executes(CivLeaderCommands::addLeader))))
                .then(literal("removeMember")
                        .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::ledCivs)
                                .then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::civPlayers).executes(CivLeaderCommands::removeMember)))
        ));

        dispatcher.register(literal("civ").requires(ServerCommandSource::isExecutedByPlayer).requires(PermissionCheckers::isCivOwner)
                .then(literal("changeOwner")
                        .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::ownedCivs)
                        .then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::civLeaders).executes(CivLeaderCommands::changeOwner))))
                .then(literal("deleteCiv")
                        .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::ownedCivs).executes(CivLeaderCommands::deleteCiv)))
                .then(literal("removeLeader")
                        .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::ownedCivs)
                        .then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::civLeaders).executes(CivLeaderCommands::removeLeader))))
        );
    }

    public static Integer respondJoinRequest(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Integer requestId = ctx.getArgument("requestId", Integer.class);
        boolean accept = ctx.getArgument("accept", Boolean.class);
        UUID player = ctx.getSource().getPlayer().getUuid();
        ctx.getSource().sendFeedback(()-> Text.literal("Response to request  " + requestId + " "+ accept), false);
        JoinRequest request = null;

        try {
            JoinRequestDAO requestDAO =  CivsAndTitles.getDataAccess().getJoinRequestDAO();
            request =requestDAO.get(requestId);
            if (request == null)
            {
                throw new CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(), Text.literal("Invalid request ID"));
            }
            if (CivUtilities.isPlayerCivLeader(player, request.civID()))
            {
                CivDAO civDAO = CivsAndTitles.getDataAccess().getCivDAO();
                Civ civ = civDAO.get(request.civID());
                civ.members().add(player);
                civDAO.update(civ);
                requestDAO.delete(requestId);
            }
            else
            {
                throw new CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(), Text.literal("Insufficient Permissions"));
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }
    public static Integer listJoinRequests(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()-> Text.literal("List join requests"), false);
        return 1;
    }

    public static Integer addLeader(CommandContext<ServerCommandSource> ctx) {
        String playerName = ctx.getArgument("playerName", String.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Add "+ playerName + "as leader"), false);
        return 1;
    }
    public static Integer removeMember(CommandContext<ServerCommandSource> ctx) {
        String playerName = ctx.getArgument("playerName", String.class);
        ctx.getSource().sendFeedback(()-> Text.literal("remove "+ playerName + "from civ"), false);
        return 1;
    }

    //OwnerCommands
    public static Integer changeOwner(CommandContext<ServerCommandSource> ctx) {
        String playerName = ctx.getArgument("playerName", String.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Change owner to "+ playerName), false);
        return 1;
    }

    public static Integer deleteCiv(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()-> Text.literal("Deleting Civ"), false);

        return 1;
    }

    public static Integer removeLeader(CommandContext<ServerCommandSource> ctx) {
        String playerName = ctx.getArgument("playerName", String.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Removing "+ playerName+ " as leader"), false);
        return 1;
    }
}
