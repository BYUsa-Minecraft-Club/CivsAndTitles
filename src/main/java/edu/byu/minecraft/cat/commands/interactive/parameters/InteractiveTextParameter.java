package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public class InteractiveTextParameter extends InteractiveParameter<Text> {

    public InteractiveTextParameter(String name) {
        super(name, Text.class);
    }
    @Override
    public String displayString(Text object) {
        NbtElement test = TextCodecs.CODEC.encodeStart(NbtOps.INSTANCE,object).getOrThrow();
        return test.toString();
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
