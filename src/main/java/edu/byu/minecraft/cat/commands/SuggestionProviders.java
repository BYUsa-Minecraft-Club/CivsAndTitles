package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.*;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

public class SuggestionProviders {
    public static CompletableFuture<Suggestions> allPlayers(CommandContext<ServerCommandSource> ignoredCtx, SuggestionsBuilder builder) {
        try {
            Stream<String> playerNames = CivsAndTitles.getDataAccess().getPlayerDAO().getAll().stream().map(Player::name);
            return suggest(filter(playerNames, builder), builder, String.class);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Suggestions> onlinePlayers(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        Stream<String> playerNames = ctx.getSource().getServer().getPlayerManager().getPlayerList().stream()
                .map(p -> p.getName().getString());
        return suggest(filter(playerNames, builder), builder, String.class);
    }

    public static CompletableFuture<Suggestions> allCivs(CommandContext<ServerCommandSource> ignoredCtx, SuggestionsBuilder builder) {
        try {
            Stream<String> civNames = CivsAndTitles.getDataAccess().getCivDAO().getAll().stream().map(Civ::name);
            return suggest(filter(civNames, builder), builder, String.class);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Suggestions> allRequestedCivs(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            Stream<String> civNames = CivsAndTitles.getDataAccess().getCivRequestDAO().getAll().stream().map(CivRequest::name);
            return suggest(filter(civNames, builder), builder, String.class);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Suggestions> allBuilds(CommandContext<ServerCommandSource> ignoredCtx, SuggestionsBuilder builder) {
        try {
            Stream<Integer> buildIDs = CivsAndTitles.getDataAccess().getBuildDAO().getAll().stream().map(Build::ID);
            return suggest(filter(buildIDs, builder), builder, Integer.class);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Suggestions> myCivs(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            Stream<String> civNames = CivsAndTitles.getDataAccess().getCivDAO().getForPlayer(player.getUuid()).stream().map(Civ::name);
            return suggest(filter(civNames, builder), builder, String.class);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Suggestions> ownedCivs(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            Stream<String> civNames = CivsAndTitles.getDataAccess().getCivParticipantDAO()
                    .getAllForPlayerStatus(player.getUuid(), CivParticipantPlayer.Status.OWNER).stream()
                    .map(cpp -> {
                        try {
                            return CivsAndTitles.getDataAccess().getCivDAO().get(cpp.civID());
                        } catch (DataAccessException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(Civ::name);
            return suggest(filter(civNames, builder), builder, String.class);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Suggestions> ledCivs(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            Collection<CivParticipantPlayer> ownedCivs = CivsAndTitles.getDataAccess().getCivParticipantDAO()
                            .getAllForPlayerStatus(player.getUuid(), CivParticipantPlayer.Status.OWNER);
            Collection<CivParticipantPlayer> ledCivs = CivsAndTitles.getDataAccess().getCivParticipantDAO()
                    .getAllForPlayerStatus(player.getUuid(), CivParticipantPlayer.Status.LEADER);
            Stream<String> civNames = Stream.concat(ownedCivs.stream(), ledCivs.stream())
                    .map(cpp -> {
                        try {
                            return CivsAndTitles.getDataAccess().getCivDAO().get(cpp.civID());
                        } catch (DataAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }).map(Civ::name);
            return suggest(filter(civNames, builder), builder, String.class);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static CompletableFuture<Suggestions> myBuilds(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            Stream<Integer> buildIDs = CivsAndTitles.getDataAccess().getBuildDAO().getAllForBuilder(player.getUuid())
                    .stream().map(Build::ID);
            return suggest(filter(buildIDs, builder), builder, Integer.class);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Suggestions> myBuildRequests(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            Stream<Integer> buildIDs = CivsAndTitles.getDataAccess().getBuildDAO().getAllForSubmitter(player.getUuid())
                    .stream().filter(build -> build.status() != Build.JudgeStatus.JUDGED).map(Build::ID);
            return suggest(filter(buildIDs, builder), builder, Integer.class);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
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
            Stream<String> titles = CivsAndTitles.getDataAccess().getUnlockedTitleDAO().getAll(player.getUuid())
                    .stream().map(UnlockedTitle::title);
            return suggest(filter(titles, builder), builder, String.class);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Suggestions> allTitles(CommandContext<ServerCommandSource> ignoredCtx, SuggestionsBuilder builder) {
        try {
            Stream<String> titles = CivsAndTitles.getDataAccess().getTitleDAO().getAll().stream().map(Title::title);
            return suggest(filter(titles, builder), builder, String.class);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Stream<T> filter(Stream<T> stream, SuggestionsBuilder builder) {
        return stream.filter(s -> CommandSource.shouldSuggest(builder.getRemaining().toLowerCase(), s.toString().toLowerCase()));
    }

    private static <T> CompletableFuture<Suggestions> suggest(Stream<T> stream, SuggestionsBuilder builder, Class<T> type) {
        if(type == Integer.class) {
            stream.forEach(i -> builder.suggest((int) i));
        }
        else if(type == String.class) {
            stream.forEach(s -> builder.suggest((String) s));
        }
        else {
            return suggest(stream.map(Object::toString), builder, String.class);
        }
        return builder.buildFuture();
    }
}
