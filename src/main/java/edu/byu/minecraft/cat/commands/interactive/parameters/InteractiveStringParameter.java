package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;

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
    public String getFromCommandContext(CommandContext<ServerCommandSource> ctx) {
        return ctx.getArgument(getName(), String.class);
    }

    @Override
    public ArgumentType<String> getCommandArgumentType(CommandRegistryAccess registryAccess) {
        return greedy ? StringArgumentType.greedyString() : StringArgumentType.string();
    }
}
