package edu.byu.minecraft.cat.commands.interactive;

import edu.byu.minecraft.cat.commands.interactive.parameters.InteractiveParameter;
import joptsimple.ValueConversionException;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class InteractiveDisplayLine<T> implements InteractiveLine<T> {
    private final InteractiveParameter<T> param;
    public InteractiveDisplayLine(InteractiveParameter<T> param){
        this.param = param;
    }

    @Override
    public Component getText(Map<String, Object> parameters, InteractiveCommandBuilder builder) {
        String paramName = param.getName();
        MutableComponent root = Component.literal("");
        MutableComponent argComponent = Component.literal(paramName+": ");
        Component valueComponent;
        Object paramVal = parameters.get(paramName);
        if(paramVal != null) {
            try {
                valueComponent = param.tryDisplayText(paramVal);
            } catch (ClassCastException e) {
                // I have no idea how we messed up so badly to get here
                throw new ValueConversionException("Invalid type in field \"" + paramName + "\"", e);
            }
        }
        else {
            valueComponent = Component.literal("UNSET").setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
        }
        return root.append(argComponent).append(valueComponent);
    }

    @Override
    public Collection<InteractiveParameter<T>> getLineParameters() {
        return Collections.singletonList(param);
    }
}
