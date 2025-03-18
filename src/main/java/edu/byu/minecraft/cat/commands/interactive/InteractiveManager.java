package edu.byu.minecraft.cat.commands.interactive;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import edu.byu.minecraft.cat.commands.interactive.parameters.InteractiveParameter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

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
            return makeBaseCommand() + "set " + sessionId + " " + paramName;
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
    private List<InteractiveLine> lines;
    private int currentId;

    private final Map<String, InteractiveParameter> parameterInfoMap;

    private BiFunction<CommandContext<ServerCommandSource>, Map<String, Object>, Integer> finishConsumer;

    public InteractiveManager(List <String> basePath){
        this.basePath = basePath;
        this.activeSessions = new HashMap<>();
        this.currentId = 0;
        this.parameterInfoMap = new HashMap<>();
        this.lines = new ArrayList<>();
    }

    public InteractiveManager addLine(InteractiveLine line){
        lines.add(line);
        for (var param: line.getLineParameters()){
            parameterInfoMap.put(param.getName(), param);
        }
        return this;
    }
    public InteractiveManager setDataHandler(BiFunction<CommandContext<ServerCommandSource>, Map<String, Object>, Integer> consumer){
        finishConsumer = consumer;
        return this;
    }
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        ArgumentBuilder<ServerCommandSource, ?> base = null;
        ArgumentBuilder<ServerCommandSource, ?> tail = null;
        ArgumentBuilder<ServerCommandSource, ?> arg = null;
        base = argument("sessionId", IntegerArgumentType.integer());
        //handle parameters
        for (var line: lines) {
            for (var parameter: line.getLineParameters()) {
                String name = parameter.getName();
                tail = literal(name);
                RequiredArgumentBuilder<ServerCommandSource, ?> arg2 = argument(name, parameter.getCommandArgumentType());
                SuggestionProvider<ServerCommandSource> suggester = parameter.getSuggestionProvider();
                if (suggester != null) {
                    arg2.suggests(suggester);
                }
                arg2.executes(this::setParameter);
                tail.then(arg2);
                base.then(tail);
            }
        }

        tail = base;
        base = literal("set");
        base.then(tail);
        tail = base;
        base = literal(basePath.getLast());
        base.then(tail);
        tail = literal("start");
        tail.executes(this::startInteractive);
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

        dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>)base);
    }


    //function handlers
    private void displayInteractive(ServerCommandSource source, int sessionId){
        CommandBuilder builder = new CommandBuilder(sessionId);
        for(var line : lines){
            source.sendFeedback(()-> line.getText(activeSessions.get(sessionId).parameters, builder), false);
        }
    }

    private Integer startInteractive (CommandContext<ServerCommandSource> ctx){
        UUID player = ctx.getSource().getPlayer().getUuid();
        Map<String, Object> defaults = new HashMap<>();
        for(InteractiveParameter info: parameterInfoMap.values()){
            defaults.put(info.getName(), info.getDefaultVal(ctx));
        }
        activeSessions.put(currentId, new InteractiveManager.SessionInfo(player, defaults));

        displayInteractive(ctx.getSource(), currentId);
        currentId += 1;
        return 1;
    }

    private boolean checkSession(CommandContext<ServerCommandSource> ctx, int id){
        if (!activeSessions.containsKey(id)){
            ctx.getSource().sendFeedback(()-> Text.literal("invalid session id"), false);
            return false;
        }
        UUID player = activeSessions.get(id).player();
        if (!player.equals(ctx.getSource().getPlayer().getUuid()))
        {
            ctx.getSource().sendFeedback(()-> Text.literal("You are not the owner of session"), false);
            return false;
        }
        return true;
    }

    private Integer setParameter (CommandContext<ServerCommandSource> ctx){
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
