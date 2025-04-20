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

public class InteractiveFinishLine implements InteractiveLine {
    @Override
    public Text getText(Map<String, Object> parameters, InteractiveCommandBuilder commandBuilder) {
        boolean ready = true;
        for (Object object: parameters.values()) {
            if(object == null){
                ready = false;
                break;
            }
        }
        MutableText completeText;
        if(ready) {
            completeText = Text.literal("(SUBMIT)").setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent.RunCommand(commandBuilder.makeFinishCommand())));
        }
        else {
            completeText = Text.literal("INCOMPLETE").setStyle(Style.EMPTY.withColor(Formatting.RED));
        }
        return completeText;
    }

    @Override
    public Collection<InteractiveParameter> getLineParameters() {
        return Collections.emptyList();
    }
}
