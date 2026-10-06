package edu.byu.minecraft.cat.commands.interactive;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import edu.byu.minecraft.cat.commands.interactive.parameters.InteractiveParameter;
import edu.byu.minecraft.cat.commands.interactive.parameters.InteractiveResult;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class InteractiveManager {

    private record SessionInfo (UUID player, Map<String, Object> parameters) {}

    private class CommandBuilder implements InteractiveCommandBuilder {
        int sessionId;
        CommandBuilder(int sessionId){
            this.sessionId = sessionId;
        }
        private String makeBaseCommand(){
            StringBuilder builder = new StringBuilder();
            builder.append("/");
            for (String literal : basePath){
                builder.append(literal);
                builder.append(" ");
            }
            return builder.toString();
        }

        @Override
        public String makeSetCommand(String paramName) {
            Object param = activeSessions.get(sessionId).parameters.get(paramName);
            if(param != null)
            {
                return makeBaseCommand() + "set " + sessionId + " " + paramName + " "
                        + parameterInfoMap.get(paramName).tryDisplayString(param);
            }
            return makeBaseCommand() + "set " + sessionId + " " + paramName + " ";
        }

        @Override
        public String makeSetCommandWithArg(String param, String val) {
            return makeSetCommand(param) + " " + val;
        }

        @Override
        public String makeDisplayCommand() {
            return makeBaseCommand() + "display " + sessionId;
        }

        @Override
        public String makeFinishCommand() {
            return makeBaseCommand() + "finish " + sessionId;
        }
    }

    private final List<String> basePath;

    private final Map<Integer, SessionInfo> activeSessions;
    private final List<InteractiveLine<?>> lines;

    private InteractiveParameter<?> startArg;

    private int currentId;

    private final Map<String, InteractiveParameter<?>> parameterInfoMap;

    private BiFunction<CommandContext<CommandSourceStack>, Map<String, Object>, Integer> finishConsumer;

    public InteractiveManager(List <String> basePath){
        this.basePath = basePath;
        this.activeSessions = new HashMap<>();
        this.currentId = 0;
        this.parameterInfoMap = new HashMap<>();
        this.lines = new ArrayList<>();
    }

    public InteractiveManager addLine(InteractiveLine<?> line){
        lines.add(line);
        for (var param: line.getLineParameters()){
            parameterInfoMap.put(param.getName(), param);
        }
        return this;
    }

    public InteractiveManager setStartArg(InteractiveParameter<?> param)
    {
        parameterInfoMap.put(param.getName(), param);
        startArg = param;
        return this;
    }

    public InteractiveManager setDataHandler(BiFunction<CommandContext<CommandSourceStack>, Map<String, Object>, Integer> consumer){
        finishConsumer = consumer;
        return this;
    }
    public void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext registryAccess,
            @Nullable Predicate<CommandSourceStack> permission
    ) {
        ArgumentBuilder<CommandSourceStack, ?> top;
        LiteralArgumentBuilder<CommandSourceStack> base;
        ArgumentBuilder<CommandSourceStack, ?> tail;
        ArgumentBuilder<CommandSourceStack, ?> arg;
        top = argument("sessionId", IntegerArgumentType.integer());
        //handle parameters
        for (var line: lines) {
            for (InteractiveParameter<?> parameter: line.getLineParameters()) {
                String name = parameter.getName();
                tail = literal(name);
                RequiredArgumentBuilder<CommandSourceStack, ?> arg2 = argument(name, parameter.getCommandArgumentType(registryAccess));
                SuggestionProvider<CommandSourceStack> suggester = parameter.getSuggestionProvider();
                if (suggester != null) {
                    arg2.suggests(suggester);
                }
                if (parameter.isOptional()) {
                    tail.executes(this::clearParameter);
                }
                arg2.executes(this::setParameter);
                tail.then(arg2);
                top.then(tail);
            }
        }

        tail = top;
        base = literal("set");
        base.then(tail);
        tail = base;
        base = literal(basePath.getLast());
        base.then(tail);

        tail = literal("start");
        if(startArg != null)
        {
            RequiredArgumentBuilder<CommandSourceStack, ?> arg2 = argument(startArg.getName(), startArg.getCommandArgumentType(registryAccess));
            SuggestionProvider<CommandSourceStack> suggester = startArg.getSuggestionProvider();
            if (suggester != null) {
                arg2.suggests(suggester);
            }
            arg2.executes(this::startInteractive);
            tail.then(arg2);
        }
        else {
            tail.executes(this::startInteractive);
        }

        base.then(tail);
        tail = literal("finish");
        arg = argument("sessionId",IntegerArgumentType.integer());
        arg.executes(this::finishInteractive);
        tail.then(arg);
        base.then(tail);
        tail = literal("display");
        arg = argument("sessionId",IntegerArgumentType.integer());
        arg.executes(this::showInteractive);
        tail.then(arg);
        base.then(tail);
        for(int i = basePath.size() -2; i >= 0; i--) {
            tail = base;
            base = literal(basePath.get(i));
            base.then(tail);
        }
        base.requires(CommandSourceStack::isPlayer);
        if (permission != null) base.requires(permission);

        dispatcher.register(base);
    }


    //function handlers
    private void displayInteractive(CommandSourceStack source, int sessionId){
        CommandBuilder builder = new CommandBuilder(sessionId);
        for(var line : lines){
            source.sendSuccess(()-> line.getText(activeSessions.get(sessionId).parameters, builder, parameterInfoMap), false);
        }
    }

    private Integer startInteractive (CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer playerEntity = ctx.getSource().getPlayer();
        UUID player;
        if (playerEntity != null) {
            player = playerEntity.getUUID();
        } else {
            ctx.getSource().sendSuccess(()-> Component.literal("You cannot create a session"), false);
            return 0;
        }
        Map<String, Object> defaults = new HashMap<>();
        for(InteractiveParameter<?> info: parameterInfoMap.values()){
            defaults.put(info.getName(), info.getDefaultVal(ctx));
        }
        if (startArg != null) {
            defaults.put(startArg.getName(), startArg.loadFromCommandContext(ctx).getOrPartial());
        }

        activeSessions.put(currentId, new InteractiveManager.SessionInfo(player, defaults));

        displayInteractive(ctx.getSource(), currentId);
        currentId += 1;
        return 1;
    }

    private boolean checkSession(CommandContext<CommandSourceStack> ctx, int id){
        if (!activeSessions.containsKey(id)){
            ctx.getSource().sendSuccess(()-> Component.literal("invalid session id " + id + "valid ids are"), false);
            for(var session : activeSessions.keySet()){
                ctx.getSource().sendSuccess(()-> Component.literal("session id " + session), false);
            }
            return false;
        }
        UUID player = activeSessions.get(id).player();
        ServerPlayer playerEntity = ctx.getSource().getPlayer();
        if (playerEntity == null || !player.equals(playerEntity.getUUID()))
        {
            ctx.getSource().sendSuccess(()-> Component.literal("You are not the owner of session"), false);
            return false;
        }
        return true;
    }

    private Integer setParameter (CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Integer id = ctx.getArgument("sessionId", Integer.class);
        if(!checkSession(ctx, id)){
            return 0;
        }
        List<ParsedCommandNode<CommandSourceStack>> nodes = ctx.getNodes();
        String paramName = nodes.get(nodes.size()-2).getNode().getName();
        //read value and set value
        InteractiveResult<?> paramVal = parameterInfoMap.get(paramName).loadFromCommandContext(ctx);
        if (paramVal.isError())
        {
            ctx.getSource().sendSuccess(()-> Component.literal("Invalid value for " + paramName + ": " + paramVal.getOrPartial()), false);
            return 0;
        }
        activeSessions.get(id).parameters.put(paramName, paramVal.getOrThrow());

        displayInteractive(ctx.getSource(), id);
        return 1;
    }

    private Integer clearParameter (CommandContext<CommandSourceStack> ctx) {
        Integer id = ctx.getArgument("sessionId", Integer.class);
        if(!checkSession(ctx, id)){
            return 0;
        }
        List<ParsedCommandNode<CommandSourceStack>> nodes = ctx.getNodes();
        String paramName = nodes.getLast().getNode().getName();

        activeSessions.get(id).parameters.put(paramName, null);

        displayInteractive(ctx.getSource(), id);
        return 1;
    }

    private Integer finishInteractive(CommandContext<CommandSourceStack> ctx){
        Integer id = ctx.getArgument("sessionId", Integer.class);
        if(!checkSession(ctx, id)){
            return 0;
        }
        if(finishConsumer != null){
            finishConsumer.apply(ctx, activeSessions.get(id).parameters());
        }
        activeSessions.remove(id);
        return 1;
    }

    private Integer showInteractive(CommandContext<CommandSourceStack> ctx){
        Integer id = ctx.getArgument("sessionId", Integer.class);
        if(!checkSession(ctx, id)){
            return 0;
        }
        displayInteractive(ctx.getSource(), id);
        return 1;
    }



}
