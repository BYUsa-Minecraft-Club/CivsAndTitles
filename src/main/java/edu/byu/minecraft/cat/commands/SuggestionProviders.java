package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.TitleDAO;
import edu.byu.minecraft.cat.model.*;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
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

    public static CompletableFuture<Suggestions> titleType(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
            Stream<String> titleTypes = Arrays.stream(Title.Type.values()).map(Title.Type::name);
            return suggest(filter(titleTypes, builder), builder, String.class);
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

    public static CompletableFuture<Suggestions> playersTitles(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        String playerName = ctx.getArgument("playerName", String.class);
        try {
            UUID uuid = CivsAndTitles.getDataAccess().getPlayerDAO().getPlayerUUID(playerName);
            Stream<String> titles = CivsAndTitles.getDataAccess().getUnlockedTitleDAO().getAll(uuid).stream().map(UnlockedTitle::title);
            return suggest(filter(titles, builder), builder, String.class);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Suggestions> playersRemovableTitles(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        String playerName = ctx.getArgument("playerName", String.class);
        try {
            UUID uuid = CivsAndTitles.getDataAccess().getPlayerDAO().getPlayerUUID(playerName);
            TitleDAO titleDAO = CivsAndTitles.getDataAccess().getTitleDAO();
            Stream<String> titles = CivsAndTitles.getDataAccess().getUnlockedTitleDAO().getAll(uuid).stream().filter(x -> {
                try {
                    return titleDAO.get(x.title()).type() != Title.Type.DEFAULT;
                } catch (DataAccessException e) {
                    return false;
                }
            }).map(UnlockedTitle::title);
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
