package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;

public class InteractiveStringParameter extends InteractiveParameter {
    boolean greedy;
    public InteractiveStringParameter(String name, boolean greedy) {
        super(name);
        this.greedy = greedy;
    }
    public InteractiveStringParameter(String name) {
        this(name, false);
    }
    @Override
    public String displayString(Object object) {
        return (String) object;
    }

    @Override
    public Object getFromCommandContext(CommandContext<ServerCommandSource> ctx) {
        return ctx.getArgument(getName(), String.class);
    }

    @Override
    public ArgumentType<?> getCommandArgumentType() {
        return greedy ? StringArgumentType.greedyString() : StringArgumentType.string();
    }
}
