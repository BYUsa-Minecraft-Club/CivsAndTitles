package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

public class InteractiveColorParameter extends InteractiveParameter {

    public InteractiveColorParameter(String name) {
        super(name);
    }
    @Override
    public String displayString(Object object) {
        return ((Formatting) object).getName();
    }

    @Override
    public Object getFromCommandContext(CommandContext<ServerCommandSource> ctx) {
        return ColorArgumentType.getColor(ctx, getName());
    }

    @Override
    public ArgumentType<?> getCommandArgumentType() {
        return ColorArgumentType.color();
    }
}
