package edu.byu.minecraft.cat.commands.interactive;

import edu.byu.minecraft.cat.commands.interactive.parameters.InteractiveParameter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class InteractiveFinishLine implements InteractiveLine<Void> {

    @Override
    public Component getText(Map<String, Object> parameters, InteractiveCommandBuilder commandBuilder, Map<String, InteractiveParameter<?>> parameterInfoMap) {
        boolean ready = true;
        for (String key : parameters.keySet()) {
            if (!parameterInfoMap.get(key).isOptional() && parameters.get(key) == null) {
                ready = false;
                break;
            }
        }

        if(ready) {
            return Component.literal("(SUBMIT)").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withClickEvent(new ClickEvent.RunCommand(commandBuilder.makeFinishCommand())));
        }
        else {
            return Component.literal("INCOMPLETE").setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
        }
    }

    @Override
    public Component getText(Map<String, Object> parameters, InteractiveCommandBuilder commandBuilder) {
        boolean ready = true;
        for (Object object: parameters.values()) {
            if(object == null){
                ready = false;
                break;
            }
        }

        if(ready) {
            return Component.literal("(SUBMIT)").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withClickEvent(new ClickEvent.RunCommand(commandBuilder.makeFinishCommand())));
        }
        else {
            return Component.literal("INCOMPLETE").setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
        }
    }

    @Override
    public Collection<InteractiveParameter<Void>> getLineParameters() {
        return Collections.emptyList();
    }
}
