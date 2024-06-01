package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Build;
import edu.byu.minecraft.cat.model.Civ;
import edu.byu.minecraft.cat.model.Title;
import edu.byu.minecraft.cat.model.UnlockedTitle;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
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
        try {
            Collection<Civ> civs = CivsAndTitles.getDataAccess().getCivDAO().getAll();
            for(Civ civ: civs)
            {
                builder.suggest(civ.name());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> allBuilds(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            Collection<Build> builds = CivsAndTitles.getDataAccess().getBuildDAO().getAll();
            for(Build build: builds)
            {
                builder.suggest(build.ID());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> myCivs(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            Collection<Civ> civs = CivsAndTitles.getDataAccess().getCivDAO().getForPlayer(player.getUuid());
            for(Civ civ: civs)
            {
                builder.suggest(civ.name());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> ownedCivs(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            Collection<Civ> civs = CivsAndTitles.getDataAccess().getCivDAO().getForPlayer(player.getUuid());
            for(Civ civ: civs) {
                if(civ.owner().equals(player.getUuid())){
                    builder.suggest(civ.name());
                }
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> myBuilds(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            Collection<Build> builds = CivsAndTitles.getDataAccess().getBuildDAO().getAllForBuilder(player.getUuid());
            for(Build build: builds)
            {
                builder.suggest(build.ID());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> myBuildRequests(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            Collection<Build> builds = CivsAndTitles.getDataAccess().getBuildDAO().getAllForSubmitter(player.getUuid());
            for(Build build: builds)
            {
                if(build.status() != Build.JudgeStatus.JUDGED)
                {
                    builder.suggest(build.ID());
                }
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
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
        try {
            Collection<UnlockedTitle> titles = CivsAndTitles.getDataAccess().getUnlockedTitleDAO().getAll(player.getUuid());
            for(UnlockedTitle title: titles)
            {
                builder.suggest(title.title());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> allTitles(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            Collection<Title> titles = CivsAndTitles.getDataAccess().getTitleDAO().getAll();
            for(Title title: titles)
            {
                builder.suggest(title.title());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return builder.buildFuture();
    }


}
