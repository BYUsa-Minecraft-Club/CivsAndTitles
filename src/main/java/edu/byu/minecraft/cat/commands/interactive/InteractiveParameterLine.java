package edu.byu.minecraft.cat.commands.interactive;

import edu.byu.minecraft.cat.commands.interactive.parameters.InteractiveParameter;
import joptsimple.ValueConversionException;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class InteractiveParameterLine<T> implements InteractiveLine<T> {
    private final InteractiveParameter<T> param;
    public InteractiveParameterLine(InteractiveParameter<T> param){
        this.param = param;
    }

    @Override
    public Component getText(Map<String, Object> parameters, InteractiveCommandBuilder builder) {
        String paramName = param.getName();
        MutableComponent root = Component.literal("");
        MutableComponent argText = Component.literal(paramName+": ");
        Component valueText;
        Object paramVal = parameters.get(paramName);
        if(paramVal != null) {
            try {
                valueText = param.tryDisplayText(paramVal);
            } catch (ClassCastException e) {
                // Someone messed up
                throw new ValueConversionException("Invalid type in field \"" + paramName + "\"", e);
            }
        }
        else {
            valueText = Component.literal("UNSET").setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
        }
        MutableComponent clickText = Component.literal(" (SET)");
        clickText.setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withClickEvent(new ClickEvent.SuggestCommand(builder.makeSetCommand(paramName)
        )));
        return root.append(argText).append(valueText).append(clickText);
    }

    @Override
    public Collection<InteractiveParameter<T>> getLineParameters() {
        return Collections.singletonList(param);
    }
}
