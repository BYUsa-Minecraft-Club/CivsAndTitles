package edu.byu.minecraft.cat.commands.interactive;

import edu.byu.minecraft.cat.commands.interactive.parameters.InteractiveParameter;
import joptsimple.ValueConversionException;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class InteractiveParameterLine<T> implements InteractiveLine<T> {
    private final InteractiveParameter<T> param;
    public InteractiveParameterLine(InteractiveParameter<T> param){
        this.param = param;
    }

    @Override
    public Text getText(Map<String, Object> parameters, InteractiveCommandBuilder builder) {
        String paramName = param.getName();
        MutableText root = Text.literal("");
        MutableText argText = Text.literal(paramName+": ");
        Text valueText;
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
            valueText = Text.literal("UNSET").setStyle(Style.EMPTY.withColor(Formatting.RED));
        }
        MutableText clickText = Text.literal(" (SET)");
        clickText.setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent.SuggestCommand(builder.makeSetCommand(paramName)
        )));
        return root.append(argText).append(valueText).append(clickText);
    }

    @Override
    public Collection<InteractiveParameter<T>> getLineParameters() {
        return Collections.singletonList(param);
    }
}
