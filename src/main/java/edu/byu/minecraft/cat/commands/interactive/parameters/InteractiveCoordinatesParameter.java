package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

public class InteractiveCoordinatesParameter extends InteractiveParameter{
    public InteractiveCoordinatesParameter(String name) {
        super(name);
    }

    @Override
    public String displayString(Object object) {
        return ((BlockPos)object).toString();
    }

    @Override
    protected Object getFromCommandContext(CommandContext<ServerCommandSource> ctx) {
        return BlockPosArgumentType.getBlockPos(ctx, getName());
    }

    @Override
    public ArgumentType<?> getCommandArgumentType() {
        return BlockPosArgumentType.blockPos();
    }
}
