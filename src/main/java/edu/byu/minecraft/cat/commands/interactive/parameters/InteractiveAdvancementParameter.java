package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;

public class InteractiveAdvancementParameter extends InteractiveParameter<AdvancementEntry> {
    public InteractiveAdvancementParameter(String name) {
        super(name, AdvancementEntry.class);
    }

    @Override
    public String displayString(AdvancementEntry object) {
        return object.toString();
    }

    @Override
    protected AdvancementEntry getFromCommandContext(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return RegistryKeyArgumentType.getAdvancementEntry(ctx, getName());
    }

    @Override
    public ArgumentType<AdvancementEntry> getCommandArgumentType(CommandRegistryAccess registryAccess) {
        // Absolutely nothing to see here, look away
        return (ArgumentType<AdvancementEntry>) (ArgumentType) RegistryKeyArgumentType.registryKey(RegistryKeys.ADVANCEMENT);
    }
}
