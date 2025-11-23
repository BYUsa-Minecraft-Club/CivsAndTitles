package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.TitleDAO;
import edu.byu.minecraft.cat.model.*;
import edu.byu.minecraft.cat.util.TitleUtilities;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SuggestionProviders {
    private static CompletableFuture<Suggestions> asyncSuggest(SuggestionsBuilder builder, Supplier<Collection<String>> query) {
        return CompletableFuture.supplyAsync(() -> {
            for (String s : query.get()) {
                builder.suggest(s);
            }
            return builder.build();
        });
    }
    public static CompletableFuture<Suggestions> allPlayers(CommandContext<ServerCommandSource> ignoredCtx, SuggestionsBuilder builder) {
        return asyncSuggest(builder, () -> {
            try {
                return CivsAndTitles.getDataAccess().getPlayerDAO().getAll().stream().map(Player::name).toList();
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static CompletableFuture<Suggestions> titleType(CommandContext<ServerCommandSource> ignoredCtx, SuggestionsBuilder builder) {
        Arrays.stream(Title.Type.values()).map(Title.Type::name).forEach(builder::suggest);
        return builder.buildFuture();
    }


    public static CompletableFuture<Suggestions> myTitles(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player == null) return builder.buildFuture();

        return asyncSuggest(builder, () -> {
            try {
                return TitleUtilities.getAllUsableTitles(player.getUuid());
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static CompletableFuture<Suggestions> allTitles(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        return asyncSuggest(builder, () -> {
            try {
                Stream<String> titles = CivsAndTitles.getDataAccess().getTitleDAO().getAll().stream().map(Title::title);
                return titles.toList();
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static CompletableFuture<Suggestions> playersTitles(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        String playerName = ctx.getArgument("playerName", String.class);
        return asyncSuggest(builder, () -> {
            try {
                UUID uuid = CivsAndTitles.getDataAccess().getPlayerDAO().getPlayerUUID(playerName);
                Stream<String> titles = CivsAndTitles.getDataAccess().getUnlockedTitleDAO().getAll(uuid).stream().map(UnlockedTitle::title);
                return titles.toList();
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static CompletableFuture<Suggestions> playerUnawardedTitles(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        String playerName = ctx.getArgument("playerName", String.class);
        return asyncSuggest(builder, () -> {
            try {
                UUID uuid = CivsAndTitles.getDataAccess().getPlayerDAO().getPlayerUUID(playerName);
                TitleDAO titleDAO = CivsAndTitles.getDataAccess().getTitleDAO();
                Set<String> owned = CivsAndTitles.getDataAccess().getUnlockedTitleDAO().getAll(uuid).stream()
                        .map(UnlockedTitle::title).collect(Collectors.toSet());
                Stream<String> unowned = CivsAndTitles.getDataAccess().getTitleDAO().getAll().stream()
                        .map(Title::title).filter(i -> {
                            try {
                                return !owned.contains(i) && titleDAO.get(i).type() != Title.Type.DEFAULT;
                            } catch (DataAccessException e) {
                                return !owned.contains(i);
                            }
                        });
                return unowned.toList();
            } catch (DataAccessException e) {
                return List.of();
            }
        });
    }

    public static CompletableFuture<Suggestions> playersRemovableTitles(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        String playerName = ctx.getArgument("playerName", String.class);
        return asyncSuggest(builder, () -> {
            try {
                UUID uuid = CivsAndTitles.getDataAccess().getPlayerDAO().getPlayerUUID(playerName);
                Stream<String> titles = CivsAndTitles.getDataAccess().getUnlockedTitleDAO().getAll(uuid).stream().map(UnlockedTitle::title);
                return titles.toList();
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
