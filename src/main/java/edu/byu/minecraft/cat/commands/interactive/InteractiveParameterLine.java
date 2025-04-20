package edu.byu.minecraft.cat.commands.interactive;

import edu.byu.minecraft.cat.commands.interactive.parameters.InteractiveParameter;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class InteractiveParameterLine implements InteractiveLine{
    private InteractiveParameter param;
    public InteractiveParameterLine(InteractiveParameter param){
        this.param = param;
    }

    @Override
    public Text getText(Map<String, Object> parameters, InteractiveCommandBuilder builder) {
        String paramName = param.getName();
        MutableText root = Text.literal("");
        MutableText argText = Text.literal(paramName+": ");
        MutableText valueText;
        Object paramVal = parameters.get(paramName);
        if(paramVal != null) {
            valueText = Text.literal(param.displayString(paramVal));
            valueText.setStyle(Style.EMPTY.withColor(Formatting.GREEN));
        }
        else {
            valueText = Text.literal("UNSET");
            valueText.setStyle(Style.EMPTY.withColor(Formatting.RED));
        }
        MutableText clickText = Text.literal(" (SET)");
        clickText.setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent.SuggestCommand(builder.makeSetCommand(paramName)
        )));
        return root.append(argText).append(valueText).append(clickText);
    }

    @Override
    public Collection<InteractiveParameter> getLineParameters() {
        return Collections.singletonList(param);
    }
}
