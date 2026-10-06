package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.fabricmc.fabric.impl.biome.modification.BuiltInResourceKeys;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

public class InteractiveAdvancementParameter extends InteractiveParameter<AdvancementHolder> {
    public InteractiveAdvancementParameter(String name) {
        super(name, AdvancementHolder.class);
    }

    @Override
    public String displayString(AdvancementHolder object) {
        return object.toString();
    }

    @Override
    protected AdvancementHolder getFromCommandContext(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return ResourceKeyArgument.getAdvancement(ctx, getName());
    }

    @Override
    public ArgumentType<AdvancementHolder> getCommandArgumentType(CommandBuildContext registryAccess) {
        // Absolutely nothing to see here, look away
        return (ArgumentType<AdvancementHolder>) (ArgumentType) ResourceKeyArgument.key(Registries.ADVANCEMENT);
    }
}
