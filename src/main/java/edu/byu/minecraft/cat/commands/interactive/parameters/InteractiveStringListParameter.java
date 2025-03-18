package edu.byu.minecraft.cat.commands.interactive.parameters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Arrays;
import java.util.List;

public class InteractiveStringListParameter extends InteractiveParameter{
    public InteractiveStringListParameter(String name) {
        super(name);
    }

    @Override
    public String displayString(Object object) {
        List<String> stringList = (List<String>) object;
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for(String string : stringList){
            builder.append(string).append(",");
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    protected Object getFromCommandContext(CommandContext<ServerCommandSource> ctx) {
        String string = ctx.getArgument(getName(), String.class);
        return Arrays.asList(string.split(" "));
    }

    @Override
    public ArgumentType<?> getCommandArgumentType() {
        return StringArgumentType.greedyString();
    }
}
