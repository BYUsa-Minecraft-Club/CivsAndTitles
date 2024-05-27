package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;
import net.minecraft.text.Text;


import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
public class BuildCommands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("build").requires(ServerCommandSource::isExecutedByPlayer)
                .then(literal("request")
                        .then(argument("civName", StringArgumentType.greedyString()).suggests(SuggestionProviders::allCivs).executes(BuildCommands::buildJudgeRequest)))
                .then(literal("cancel")
                        .then(argument("buildId",  IntegerArgumentType.integer(1)).suggests(SuggestionProviders::myBuildRequests).executes(BuildCommands::cancelJudgeRequest)))
                .then(literal("listRequests").executes(BuildCommands::listRequests))
        );

        dispatcher.register(literal("build").requires(ServerCommandSource::isExecutedByPlayer).then(literal("judge").requires(PermissionCheckers::isBuildJudge)
                .then(literal("listActive").executes(BuildCommands::listActiveRequest))
                .then(literal("judgeMode").executes(BuildCommands::judgeMode))
                .then(literal("submitScore").executes(BuildCommands::submitScore))
        ));
    }

    public static Integer buildJudgeRequest(CommandContext<ServerCommandSource> ctx) {
        String civName = ctx.getArgument("civName", String.class);
        ctx.getSource().sendFeedback(()->Text.literal("Build Request for civ " + civName), false);
        //TODO
        return 1;
    }

    public static Integer cancelJudgeRequest(CommandContext<ServerCommandSource> ctx) {
        Integer buildId = ctx.getArgument("buildId", Integer.class);
        ctx.getSource().sendFeedback(()->Text.literal("Cancel Build Request " + buildId), false);
        //TODO
        return 1;
    }

    public static Integer listRequests(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()->Text.literal("Listing Build Requests"), false);
        return 1;
    }


    // Build Judge Commands
    public static Integer listActiveRequest(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()->Text.literal("Listing Active Requests"), false);
        return 1;
    }

    public static Integer judgeMode(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()->Text.literal("Activating Judge Mode"), false);
        return 1;
    }

    public static Integer submitScore(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()->Text.literal("Submitting score for build"), false);
        return 1;
    }



}
