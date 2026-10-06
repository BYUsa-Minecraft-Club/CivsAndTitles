package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

public class InteractiveStringParameter extends InteractiveParameter<String> {
    boolean greedy;
    public InteractiveStringParameter(String name, boolean greedy) {
        super(name, String.class);
        this.greedy = greedy;
    }
    public InteractiveStringParameter(String name) {
        this(name, false);
    }
    @Override
    public String displayString(String object) {
        return object;
    }

    @Override
    public String getFromCommandContext(CommandContext<CommandSourceStack> ctx) {
        return ctx.getArgument(getName(), String.class);
    }

    @Override
    public ArgumentType<String> getCommandArgumentType(CommandBuildContext registryAccess) {
        return greedy ? StringArgumentType.greedyString() : StringArgumentType.string();
    }
}
