package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class InteractiveParameter<T> {
    final Class<T> type;
    String name;
    SuggestionProvider<ServerCommandSource> suggestionProvider;

    @Nullable
    T defaultVal;
    Function<CommandContext<ServerCommandSource>, T> defaultValProvider;

    Predicate<T> validator;

    boolean optional = false;

    public InteractiveParameter(String name, Class<T> type){
        this.name = name;
        this.type = type;
    }
    public String getName() {
        return  name;
    }
    public abstract String displayString(T object);

    public Text displayText(T object) {
        return Text.of(displayString(object));
    }

    private T tryCast(Object object) throws ClassCastException {
        return type.cast(object);
    }

    public Text tryDisplayText(Object object) throws ClassCastException {
        return displayText(tryCast(object));
    }

    public String tryDisplayString(Object object) throws ClassCastException {
        return displayString(tryCast(object));
    }

    /**
     * returns value of parameter using the command
     * @param ctx command context setting parameter
     * @return if parameter was valid
     */
    public T loadFromCommandContext(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        T val = getFromCommandContext(ctx);
        if(validator != null && !validator.test(val))
        {
            return null;
        }
        return val;
    }

    protected abstract T getFromCommandContext(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException;


    /**
     * Gets default value for the parameter based on the context
     * @param ctx current context
     * @return parameter or null if there is no default
     */
    public T getDefaultVal(CommandContext<ServerCommandSource> ctx){
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


    public InteractiveParameter<T> setDefaultVal(T object){
        defaultVal = object;
        return this;
    }

    public InteractiveParameter<T> setDefaultProvider(Function<CommandContext<ServerCommandSource>, T> provider) {
        defaultValProvider = provider;
        return this;
    }

    public InteractiveParameter<T> setValidator(Predicate<T> validator){
        this.validator = validator;
        return this;
    }

    public abstract ArgumentType<T> getCommandArgumentType(CommandRegistryAccess registryAccess);

    public InteractiveParameter<T> setSuggestionProvider(SuggestionProvider<ServerCommandSource> provider){
        suggestionProvider = provider;
        return this;
    }
    public SuggestionProvider<ServerCommandSource> getSuggestionProvider(){
        return suggestionProvider;
    }

    public InteractiveParameter<T> setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public boolean isOptional() {
        return optional;
    }
}
