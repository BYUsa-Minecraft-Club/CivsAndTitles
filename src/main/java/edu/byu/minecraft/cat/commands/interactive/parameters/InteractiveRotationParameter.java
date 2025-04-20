package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public class InteractiveRotationParameter extends  InteractiveParameter{
    public InteractiveRotationParameter(String name) {
        super(name);
    }

    @Override
    public String displayString(Object object) {
        return object.toString();
    }

    @Override
    protected Object getFromCommandContext(CommandContext<ServerCommandSource> ctx) {
        return ctx.getArgument(getName(), PosArgument.class).getRotation(ctx.getSource());
    }

    @Override
    public ArgumentType<?> getCommandArgumentType() {
        return RotationArgumentType.rotation();
    }
}
