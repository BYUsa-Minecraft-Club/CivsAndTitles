package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public class InteractiveTextParameter extends InteractiveParameter<Component> {

    public InteractiveTextParameter(String name) {
        super(name, Component.class);
    }
    @Override
    public String displayString(Component object) {
        Tag test = ComponentSerialization.CODEC.encodeStart(NbtOps.INSTANCE,object).getOrThrow();
        return test.toString();
    }

    @Override
    public Component displayText(Component object) {
        return object;
    }

    @Override
    public Component getFromCommandContext(CommandContext<CommandSourceStack> ctx) {
        return ComponentArgument.getRawComponent(ctx, getName());
    }

    @Override
    public ArgumentType<Component> getCommandArgumentType(CommandBuildContext registryAccess) {
        return ComponentArgument.textComponent(registryAccess);
    }
}
