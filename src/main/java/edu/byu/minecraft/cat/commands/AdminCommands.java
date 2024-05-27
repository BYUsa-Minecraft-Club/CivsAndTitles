package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

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
                .then(literal("giveTitle").then(argument("playerName", StringArgumentType.string()).suggests(SuggestionProviders::allPlayers).then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::allTitles).executes(AdminCommands::giveTitle))))
        ));
    }

    public static Integer nukeCiv(CommandContext<ServerCommandSource> ctx) {
        String civName = ctx.getArgument("civName", String.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Nuking Civ " + civName), false);
        return 1;
    }

    public static Integer giveTitle(CommandContext<ServerCommandSource> ctx) {
        String player = ctx.getArgument("playerName", String.class);
        String title = ctx.getArgument("title", String.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Giving " + player + "title "+ title), false);
        return 1;
    }


    public static Integer changeCivOwner(CommandContext<ServerCommandSource> ctx) {
        String player = ctx.getArgument("playerName", String.class);
        String civ = ctx.getArgument("civName", String.class);
        ctx.getSource().sendFeedback(()-> Text.literal("change owner of " + civ + " to "+ player), false);
        return 1;
    }

    public static Integer deleteBuild(CommandContext<ServerCommandSource> ctx) {
        Integer buildId = ctx.getArgument("buildId", Integer.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Deleting build #" + buildId), false);
        return 1;
    }

    public static Integer respondCivRequest(CommandContext<ServerCommandSource> ctx) {
        Integer requestId = ctx.getArgument("requestId", Integer.class);
        boolean accept = ctx.getArgument("accept", Boolean.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Response to request  " + requestId + " "+ accept), false);
        //TODO
        return 1;
    }

    public static Integer listCivRequests(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()-> Text.literal("Listing Civ requests"), false);
        return 1;
    }

    public static Integer modifyBuild(CommandContext<ServerCommandSource> ctx) {
        Integer buildId = ctx.getArgument("buildId", Integer.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Modifying build #" + buildId), false);
        return 1;
    }


    public static Integer toggleBuilds(CommandContext<ServerCommandSource> ctx) {
        Integer buildId = ctx.getArgument("buildId", Integer.class);
        ctx.getSource().sendFeedback(()-> Text.literal("Toggling builds up to build #" + buildId), false);
        return 1;
    }


    public static Integer addBuild(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()-> Text.literal("Adding build"), false);
        return 1;
    }


    public static Integer addTitle(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()-> Text.literal("Adding Title"), false);
        return 1;
    }

}
