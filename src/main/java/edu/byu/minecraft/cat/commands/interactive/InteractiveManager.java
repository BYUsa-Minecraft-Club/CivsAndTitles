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
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.BiFunction;

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
    private List<InteractiveLine<?>> lines;

    private InteractiveParameter<?> startArg;

    private int currentId;

    private final Map<String, InteractiveParameter<?>> parameterInfoMap;

    private BiFunction<CommandContext<ServerCommandSource>, Map<String, Object>, Integer> finishConsumer;

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

    public InteractiveManager setDataHandler(BiFunction<CommandContext<ServerCommandSource>, Map<String, Object>, Integer> consumer){
        finishConsumer = consumer;
        return this;
    }
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        ArgumentBuilder<ServerCommandSource, ?> top;
        LiteralArgumentBuilder<ServerCommandSource> base;
        ArgumentBuilder<ServerCommandSource, ?> tail;
        ArgumentBuilder<ServerCommandSource, ?> arg;
        top = argument("sessionId", IntegerArgumentType.integer());
        //handle parameters
        for (var line: lines) {
            for (InteractiveParameter<?> parameter: line.getLineParameters()) {
                String name = parameter.getName();
                tail = literal(name);
                RequiredArgumentBuilder<ServerCommandSource, ?> arg2 = argument(name, parameter.getCommandArgumentType(registryAccess));
                SuggestionProvider<ServerCommandSource> suggester = parameter.getSuggestionProvider();
                if (suggester != null) {
                    arg2.suggests(suggester);
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
            RequiredArgumentBuilder<ServerCommandSource, ?> arg2 = argument(startArg.getName(), startArg.getCommandArgumentType(registryAccess));
            SuggestionProvider<ServerCommandSource> suggester = startArg.getSuggestionProvider();
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
        base.requires(ServerCommandSource::isExecutedByPlayer);

        dispatcher.register(base);
    }


    //function handlers
    private void displayInteractive(ServerCommandSource source, int sessionId){
        CommandBuilder builder = new CommandBuilder(sessionId);
        for(var line : lines){
            source.sendFeedback(()-> line.getText(activeSessions.get(sessionId).parameters, builder, parameterInfoMap), false);
        }
    }

    private Integer startInteractive (CommandContext<ServerCommandSource> ctx){
        ServerPlayerEntity playerEntity = ctx.getSource().getPlayer();
        UUID player;
        if (playerEntity != null) {
            player = playerEntity.getUuid();
        } else {
            ctx.getSource().sendFeedback(()-> Text.literal("You cannot create a session"), false);
            return 0;
        }
        Map<String, Object> defaults = new HashMap<>();
        for(InteractiveParameter<?> info: parameterInfoMap.values()){
            defaults.put(info.getName(), info.getDefaultVal(ctx));
        }

        activeSessions.put(currentId, new InteractiveManager.SessionInfo(player, defaults));

        displayInteractive(ctx.getSource(), currentId);
        currentId += 1;
        return 1;
    }

    private boolean checkSession(CommandContext<ServerCommandSource> ctx, int id){
        if (!activeSessions.containsKey(id)){
            ctx.getSource().sendFeedback(()-> Text.literal("invalid session id " + id + "valid ids are"), false);
            for(var session : activeSessions.keySet()){
                ctx.getSource().sendFeedback(()-> Text.literal("session id " + session), false);
            }
            return false;
        }
        UUID player = activeSessions.get(id).player();
        ServerPlayerEntity playerEntity = ctx.getSource().getPlayer();
        if (playerEntity == null || !player.equals(playerEntity.getUuid()))
        {
            ctx.getSource().sendFeedback(()-> Text.literal("You are not the owner of session"), false);
            return false;
        }
        return true;
    }

    private Integer setParameter (CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Integer id = ctx.getArgument("sessionId", Integer.class);
        if(!checkSession(ctx, id)){
            return 0;
        }
        List<ParsedCommandNode<ServerCommandSource>> nodes = ctx.getNodes();
        String paramName =  nodes.get(nodes.size()-2).getNode().getName();
        //read value and set value
        Object paramVal = parameterInfoMap.get(paramName).loadFromCommandContext(ctx);
        if (paramVal == null)
        {
            ctx.getSource().sendFeedback(()-> Text.literal("Invalid value for " + paramName + ": " + paramVal), false);
            return 0;
        }
        activeSessions.get(id).parameters.put(paramName, paramVal);

        displayInteractive(ctx.getSource(), id);
        return 1;
    }

    private Integer finishInteractive(CommandContext<ServerCommandSource> ctx){
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

    private Integer showInteractive(CommandContext<ServerCommandSource> ctx){
        Integer id = ctx.getArgument("sessionId", Integer.class);
        if(!checkSession(ctx, id)){
            return 0;
        }
        displayInteractive(ctx.getSource(), id);
        return 1;
    }



}
