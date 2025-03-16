package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.argument.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;

import java.util.*;
import java.util.List;
import java.util.function.Function;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class InteractiveCommands {
    public record ArgumentInfo (String argumentName, ArgumentType<?> type, Function<CommandContext<ServerCommandSource>,?> defaultProvider, SuggestionProvider<ServerCommandSource> suggester){}
    private record CommandBuilder (UUID requester, NbtCompound compound) {}

    Map<Integer, CommandBuilder> inProgress;
    final private List<String> literals;
    final private Collection<ArgumentInfo> argumentInfos;

    final private Map<String, ArgumentInfo> argumentInfoMap;
    final private String name;

    private int currentId;
    public InteractiveCommands(String name, List<String> literals, Collection<ArgumentInfo> arguments) {
        this.literals = literals;
        this.argumentInfos = arguments;
        this.name = name;
        this.currentId = 0;
        this.inProgress = new HashMap<>();
        argumentInfoMap = new HashMap<>();
        for (ArgumentInfo info : arguments){
            argumentInfoMap.put(info.argumentName, info);
        }

    }
    private void addArgToNbtCompound(NbtCompound compound,String key, Object object){
        if (object instanceof Integer) {
            compound.putInt(key, (Integer) object);
        }
        else if (object instanceof String) {
            compound.putString(key, (String) object);
        }
        else if(object instanceof int[]){
            compound.putIntArray(key, (int[]) object);
        } else if (object instanceof NbtElement){
            compound.put(key, (NbtElement) object);
        } else {
            compound.putString(key, object.toString());
        }
    }

    private String makeSetCommand(int buildingId, String arg) {
        StringBuilder builder = new StringBuilder();
        builder.append("/");
        for (String literal : literals){
            builder.append(literal);
            builder.append(" ");
        }
        builder.append("set ");
        builder.append(buildingId);
        builder.append(" ");
        builder.append(arg);
        return builder.toString();
    }

    private String makeFinalCommand(int buildingId) {
        return "/build request_detailed raw " +
                inProgress.get(buildingId).compound.toString();
    }

    private Text buildArgString(Integer buildingId, String arg, ServerCommandSource source) {
        CommandBuilder cBuilder = inProgress.get(buildingId);
        MutableText root = Text.literal("");
        MutableText argText = Text.literal(arg+": ");
        MutableText valueText;
        if(cBuilder.compound.contains(arg)) {
            valueText = Text.literal(cBuilder.compound.get(arg).asString());
            valueText.setStyle(Style.EMPTY.withColor(Formatting.GREEN));
        }
        else {
            valueText = Text.literal("UNSET");
            valueText.setStyle(Style.EMPTY.withColor(Formatting.RED));
        }
        MutableText clickText = Text.literal(" (SET)");
        clickText.setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent(
                ClickEvent.Action.SUGGEST_COMMAND, makeSetCommand(buildingId, arg)
        )));
        return root.append(argText).append(valueText).append(clickText);
    }

    private void printCurrentState(ServerCommandSource source, Integer buildingId) {
        CommandBuilder builder = inProgress.get(buildingId);
        source.sendFeedback(()-> Text.literal("Current State of "+ name), false);
        boolean ready = true;
        for (ArgumentInfo info: argumentInfos) {
            if(!builder.compound.contains(info.argumentName)){
                ready = false;
            }
            source.sendFeedback(()->buildArgString(buildingId, info.argumentName, source), false);
        }
        MutableText completeText;
        if(ready) {
            completeText = Text.literal("(SUBMIT)").setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, makeFinalCommand(buildingId))));
        }
        else {
            completeText = Text.literal("INCOMPLETE").setStyle(Style.EMPTY.withColor(Formatting.RED));
        }
        source.sendFeedback(()->completeText, false);
    }

    private Integer startInteractive(CommandContext<ServerCommandSource> ctx){
        UUID player = ctx.getSource().getPlayer().getUuid();

        NbtCompound defaults = new NbtCompound();
        for (ArgumentInfo info: argumentInfos){
            if (info.defaultProvider != null){
                addArgToNbtCompound(defaults, info.argumentName, info.defaultProvider.apply(ctx));
            }
        }

        inProgress.put(currentId, new CommandBuilder(player, defaults));
        printCurrentState(ctx.getSource(), currentId);
        currentId += 1;
        return 1;
    }
    private Integer setArgument(CommandContext<ServerCommandSource> ctx){

       Integer id = ctx.getArgument("interactiveId", Integer.class);
       if (!inProgress.containsKey(id)){
           ctx.getSource().sendFeedback(()-> Text.literal("invalid interactive id"), false);
            return 0;
       }
        CommandBuilder builder = inProgress.get(id);
        if (!builder.requester.equals(ctx.getSource().getPlayer().getUuid()))
        {
            ctx.getSource().sendFeedback(()-> Text.literal("You are not the owner of interactive id"), false);
            return 0;
        }
       List<ParsedCommandNode<ServerCommandSource>> nodes = ctx.getNodes();
       String arg =  nodes.get(nodes.size()-2).getNode().getName();
       ctx.getSource().sendFeedback(()-> Text.literal("setting id "+ id + " node " + arg), false);
       ArgumentInfo info = argumentInfoMap.get(arg);

       if (info.type instanceof  IntegerArgumentType){
           Integer val = ctx.getArgument(info.argumentName, Integer.class);
           ctx.getSource().sendFeedback(()-> Text.literal("to "+ val), false);
           builder.compound.putInt(info.argumentName, val);
       }
       else if(info.type instanceof StringArgumentType) {
           String val = ctx.getArgument(info.argumentName, String.class);
           ctx.getSource().sendFeedback(()-> Text.literal("to "+ val), false);
           builder.compound.putString(info.argumentName, val);
       } else if(info.type instanceof DimensionArgumentType) {
           Identifier val = ctx.getArgument(info.argumentName, Identifier.class);
           ctx.getSource().sendFeedback(()-> Text.literal("to "+ val), false);
           builder.compound.putString(info.argumentName, val.toString());
       } else if(info.type instanceof BlockPosArgumentType) {
           BlockPos val = BlockPosArgumentType.getBlockPos(ctx, "Pos");
           ctx.getSource().sendFeedback(() -> Text.literal("to " + val), false);
           builder.compound.putIntArray(info.argumentName, Arrays.asList(val.getX(), val.getY(), val.getZ()));
       } else if(info.type instanceof RotationArgumentType) {
           PosArgument val = ctx.getArgument(info.argumentName, PosArgument.class);
           ctx.getSource().sendFeedback(()-> Text.literal("to "+ val), false);
           NbtList list = new NbtList();
           Vec2f rotation = val.toAbsoluteRotation(ctx.getSource());
           list.add(0, NbtFloat.of(rotation.x));
           list.add(1, NbtFloat.of(rotation.y));

           builder.compound.put(info.argumentName, list);
       }



       printCurrentState(ctx.getSource(), id);
       return 1;
    }
    private Integer finish(CommandContext<ServerCommandSource> ctx){
        Integer id = ctx.getArgument("interactiveId", Integer.class);
        if (!inProgress.containsKey(id)){
            ctx.getSource().sendFeedback(()-> Text.literal("invalid interactive id"), false);
            return 0;
        }
        CommandBuilder builder = inProgress.get(id);
        if (!builder.requester.equals(ctx.getSource().getPlayer().getUuid()))
        {
            ctx.getSource().sendFeedback(()-> Text.literal("You are not the owner of interactive id"), false);
            return 0;
        }
        inProgress.remove(id);
        return 1;
    }
    public void register(CommandDispatcher<ServerCommandSource> dispatcher){
        if (literals.isEmpty()){
            return;
        }
        if (argumentInfos.isEmpty()){
            return;
        }
        ArgumentBuilder<ServerCommandSource, ?> base = null;
        ArgumentBuilder<ServerCommandSource, ?> tail = null;
        base = argument("interactiveId",IntegerArgumentType.integer());

        for (var argument: argumentInfos) {
            tail = literal(argument.argumentName);
            RequiredArgumentBuilder<ServerCommandSource, ?> arg = argument(argument.argumentName, argument.type);
            if(argument.suggester != null){
                arg.suggests(argument.suggester);
            }
            arg.executes(this::setArgument);
            tail.then(arg);
            base.then(tail);
        }

        tail = base;
        base = literal("set");
        base.then(tail);
        tail = base;
        base = literal(literals.getLast());
        base.then(tail);
        tail = literal("start");
        tail.executes(this::startInteractive);
        base.then(tail);
        tail = literal("finish");
        tail.executes(this::finish);
        tail.then(argument("interactiveId",IntegerArgumentType.integer()));
        base.then(tail);
        for(int i = literals.size() -2; i >= 0; i--) {
            tail = base;
            base = literal(literals.get(i));
            base.then(tail);
        }
        base.requires(ServerCommandSource::isExecutedByPlayer);

        dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>)base);
    }
}
