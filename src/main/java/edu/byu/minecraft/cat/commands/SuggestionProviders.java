package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SuggestionProviders {
    public static CompletableFuture<Suggestions> allPlayers(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        // TODO
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> onlinePlayers(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        for (ServerPlayerEntity p : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
            builder.suggest(p.getName().getString());
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> allCivs(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        //TODO
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> allBuilds(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        //TODO
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> myCivs(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        //TODO
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> ownedCivs(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        //TODO
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> myBuilds(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        //TODO
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> myBuildRequests(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        //TODO
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> myJoinRequests(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        //TODO
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> civPlayers(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        //TODO
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> civLeaders(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        //TODO
        return builder.buildFuture();
    }


    public static CompletableFuture<Suggestions> myTitles(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        //TODO
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> allTitles(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        //TODO
        return builder.buildFuture();
    }


}
