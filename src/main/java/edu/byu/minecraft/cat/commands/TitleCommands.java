package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TitleCommands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("titles").requires(ServerCommandSource::isExecutedByPlayer)
                .then(literal("list").executes(TitleCommands::listTitles))
                .then(literal("change")
                        .then(argument("title", StringArgumentType.string()).suggests(SuggestionProviders::myTitles).requires(PermissionCheckers::hasTitle).executes(TitleCommands::changeTitle)))
                .then(literal("showRank")
                        .then(argument("showRank", BoolArgumentType.bool()).executes(TitleCommands::showRank)))
        );
    }

    public static Integer listTitles(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()-> Text.literal("List titles"), false);
        return 1;
    }

    public static Integer changeTitle(CommandContext<ServerCommandSource> ctx) {
        String title = ctx.getArgument("title", String.class);
        ctx.getSource().sendFeedback(()->Text.literal("Change title to " + title), false);
        return 1;
    }

    public static Integer showRank(CommandContext<ServerCommandSource> ctx) {
        Boolean showRank = ctx.getArgument("showRank", Boolean.class);
        ctx.getSource().sendFeedback(()->Text.literal("Change show rank to " + showRank), false);
        return 1;
    }



}
