package edu.byu.minecraft.cat.commands.interactive;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

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
        Component getSimpleText(T t, CommandContext<CommandSourceStack> ctx);
        Component getDetailedText(T t, CommandContext<CommandSourceStack> ctx);
        Collection<T> getValues(CommandContext<CommandSourceStack> ctx);
        Collection<K> getKeys(CommandContext<CommandSourceStack> ctx);
        T getValue(K key);

        K getKey(T value);
    }

    public interface KeyInfo <K>{
        K extractKey(CommandContext<CommandSourceStack> ctx);
        String getKeyName();
        ArgumentType<?> getArgumentType();
    }

    public InteractiveDisplay (List<String> basePath,  DisplayProvider<K, T> provider, KeyInfo<K> keyInfo) {
        this.provider = provider;
        this.basePath = basePath;
        this.keyInfo = keyInfo;
    }
    private CompletableFuture<Suggestions> suggestionProvider(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        Stream<K> keys = provider.getKeys(ctx).stream();
        keys = keys.filter(s -> SharedSuggestionProvider.matchesSubStr(builder.getRemaining().toLowerCase(), s.toString().toLowerCase()));
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
    private Integer showList (CommandContext<CommandSourceStack> ctx) {
        for(T val: provider.getValues(ctx)){
            MutableComponent root = Component.literal("");
            Component text = provider.getSimpleText(val, ctx);
            Component button = Component.literal("  (details)").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withClickEvent(new ClickEvent.RunCommand(makeDisplayIndCommand(provider.getKey(val)))));
            root.append(text);
            root.append(button);
            ctx.getSource().sendSuccess(()-> root, false);
        }
        return 1;
    }
    private Integer showIndividual(CommandContext<CommandSourceStack> ctx){
        K key = keyInfo.extractKey(ctx);
        T val = provider.getValue(key);
        if(val == null)
        {
            ctx.getSource().sendSuccess(()-> Component.literal("Invalid " + keyInfo.getKeyName() + ": " + key.toString()), false);
            return 0;
        }
        ctx.getSource().sendSuccess(()-> provider.getDetailedText(val, ctx), false);
        return 1;
    }
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> base = null;
        ArgumentBuilder<CommandSourceStack, ?> tail = null;
        RequiredArgumentBuilder<CommandSourceStack, ?> arg = null;

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
        base.requires(CommandSourceStack::isPlayer);

        dispatcher.register(base);
    }
}
