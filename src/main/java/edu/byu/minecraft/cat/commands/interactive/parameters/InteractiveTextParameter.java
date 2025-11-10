package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class InteractiveTextParameter extends InteractiveParameter<Text> {

    public InteractiveTextParameter(String name) {
        super(name, Text.class);
    }
    @Override
    public String displayString(Text object) {
        return object.getString();
    }

    @Override
    public Text displayText(Text object) {
        return object;
    }

    @Override
    public Text getFromCommandContext(CommandContext<ServerCommandSource> ctx) {
        return TextArgumentType.getTextArgument(ctx, getName());
    }

    @Override
    public ArgumentType<Text> getCommandArgumentType(CommandRegistryAccess registryAccess) {
        return TextArgumentType.text(registryAccess);
    }
}
