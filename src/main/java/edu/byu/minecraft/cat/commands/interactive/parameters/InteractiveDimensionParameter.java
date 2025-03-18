package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public class InteractiveDimensionParameter extends  InteractiveParameter{
    public InteractiveDimensionParameter(String name) {
        super(name);
    }

    @Override
    public String displayString(Object object) {
        return object.toString();
    }

    @Override
    protected Object getFromCommandContext(CommandContext<ServerCommandSource> ctx) {
        return ctx.getArgument(getName(), Identifier.class);
    }

    @Override
    public ArgumentType<?> getCommandArgumentType() {
        return DimensionArgumentType.dimension();
    }
}
