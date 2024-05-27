package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
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
public class CivCommands {
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

    public static Integer createCiv(CommandContext<ServerCommandSource> ctx) {
        String civName = ctx.getArgument("civName", String.class);
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


}
