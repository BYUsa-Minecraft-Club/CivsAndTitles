package edu.byu.minecraft.cat.commands.interactive;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;


public class InteractiveDisplay <K, T> {
    private final List<String> basePath;
    private final DisplayProvider<K,T> provider;
    private final KeyInfo<K> keyInfo;
    public interface DisplayProvider<K, T> {
        Text getSimpleText(T t, CommandContext<ServerCommandSource> ctx);
        Text getDetailedText(T t, CommandContext<ServerCommandSource> ctx);
        Collection<T> getValues(CommandContext<ServerCommandSource> ctx);
        Collection<K> getKeys(CommandContext<ServerCommandSource> ctx);
        T getValue(K key);

        K getKey(T value);
    }

    public interface KeyInfo <K>{
        K extractKey(CommandContext<ServerCommandSource> ctx);
        String getKeyName();
        ArgumentType<?> getArgumentType();
    }

    public InteractiveDisplay (List<String> basePath,  DisplayProvider<K, T> provider, KeyInfo<K> keyInfo) {
        this.provider = provider;
        this.basePath = basePath;
        this.keyInfo = keyInfo;
    }
    private CompletableFuture<Suggestions> suggestionProvider(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        Stream<K> keys = provider.getKeys(ctx).stream();
        keys = keys.filter(s -> CommandSource.shouldSuggest(builder.getRemaining().toLowerCase(), s.toString().toLowerCase()));
        keys.forEach(i -> builder.suggest(i.toString()));
        return builder.buildFuture();
    }
    private String makeDisplayIndCommand(K key){
        StringBuilder builder = new StringBuilder();
        builder.append("/");
        for(String path : basePath){
            builder.append(path);
            builder.append(" ");
        }
        builder.append("detail ");
        builder.append(key);
        return builder.toString();
    }
    private Integer showList (CommandContext<ServerCommandSource> ctx) {
        for(T val: provider.getValues(ctx)){
            MutableText root = Text.literal("");
            Text text = provider.getSimpleText(val, ctx);
            Text button = Text.literal("  (details)").setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent.RunCommand(makeDisplayIndCommand(provider.getKey(val)))));
            root.append(text);
            root.append(button);
            ctx.getSource().sendFeedback(()-> root, false);
        }
        return 1;
    }
    private Integer showIndividual(CommandContext<ServerCommandSource> ctx){
        K key = keyInfo.extractKey(ctx);
        T val = provider.getValue(key);
        if(val == null)
        {
            ctx.getSource().sendFeedback(()-> Text.literal("Invalid " + keyInfo.getKeyName() + ": " + key.toString()), false);
            return 0;
        }
        ctx.getSource().sendFeedback(()-> provider.getDetailedText(val, ctx), false);
        return 1;
    }
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> base = null;
        ArgumentBuilder<ServerCommandSource, ?> tail = null;
        RequiredArgumentBuilder<ServerCommandSource, ?> arg = null;

        base = literal(basePath.getLast());
        tail = literal("detail");
        arg = argument(keyInfo.getKeyName(), keyInfo.getArgumentType());
        arg.suggests(this::suggestionProvider);
        arg.executes(this::showIndividual);
        tail.then(arg);
        base.then(tail);
        tail = literal("list");
        tail.executes(this::showList);
        tail.then(arg);
        base.then(tail);
        for(int i = basePath.size() -2; i >= 0; i--) {
            tail = base;
            base = literal(basePath.get(i));
            base.then(tail);
        }
        base.requires(ServerCommandSource::isExecutedByPlayer);

        dispatcher.register(base);
    }
}
