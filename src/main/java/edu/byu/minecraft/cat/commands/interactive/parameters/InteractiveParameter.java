package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class InteractiveParameter {
    String name;
    SuggestionProvider<ServerCommandSource> suggestionProvider;
    Object defaultVal;
    Function<CommandContext<ServerCommandSource>, Object> defaultValProvider;

    Predicate<Object> validater;
    public InteractiveParameter(String name){
        this.name = name;
    }
    public String getName() {
        return  name;
    }
    public abstract String displayString(Object object);

    /**
     * returns value of parameter using the command
     * @param ctx command context setting parameter
     * @return if parameter was valid
     */
    public Object loadFromCommandContext(CommandContext<ServerCommandSource> ctx) {
        Object val = getFromCommandContext(ctx);
        if(validater != null && !validater.test(val))
        {
            return null;
        }
        return val;
    }
    protected abstract Object getFromCommandContext(CommandContext<ServerCommandSource> ctx);


    /**
     * Gets default value for the parameter based on the context
     * @param ctx current context
     * @return parameter or null if there is no default
     */
    public Object getDefaultVal(CommandContext<ServerCommandSource> ctx){
        if(defaultVal != null)
        {
            return defaultVal;
        }
        if(defaultValProvider != null)
        {
            return defaultValProvider.apply(ctx);
        }
        return null;
    }


    public InteractiveParameter setDefaultVal(Object object){
        defaultVal = object;
        return this;
    }

    public InteractiveParameter setDefaultProvider(Function<CommandContext<ServerCommandSource>, Object> provider) {
        defaultValProvider = provider;
        return this;
    }

    public InteractiveParameter setValidater(Predicate<Object> validater){
        this.validater = validater;
        return this;
    }

    public abstract ArgumentType<?> getCommandArgumentType();

    public InteractiveParameter setSuggestionProvider(SuggestionProvider<ServerCommandSource> provider){
        suggestionProvider = provider;
        return this;
    }
    public SuggestionProvider<ServerCommandSource> getSuggestionProvider(){
        return suggestionProvider;
    }
}
