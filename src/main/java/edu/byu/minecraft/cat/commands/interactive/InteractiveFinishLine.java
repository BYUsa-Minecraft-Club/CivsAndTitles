package edu.byu.minecraft.cat.commands.interactive;

import edu.byu.minecraft.cat.commands.interactive.parameters.InteractiveParameter;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class InteractiveFinishLine implements InteractiveLine<Void> {

    @Override
    public Text getText(Map<String, Object> parameters, InteractiveCommandBuilder commandBuilder, Map<String, InteractiveParameter<?>> parameterInfoMap) {
        boolean ready = true;
        for (String key : parameters.keySet()) {
            if (!parameterInfoMap.get(key).isOptional() && parameters.get(key) == null) {
                ready = false;
                break;
            }
        }

        if(ready) {
            return Text.literal("(SUBMIT)").setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent.RunCommand(commandBuilder.makeFinishCommand())));
        }
        else {
            return Text.literal("INCOMPLETE").setStyle(Style.EMPTY.withColor(Formatting.RED));
        }
    }

    @Override
    public Text getText(Map<String, Object> parameters, InteractiveCommandBuilder commandBuilder) {
        boolean ready = true;
        for (Object object: parameters.values()) {
            if(object == null){
                ready = false;
                break;
            }
        }

        if(ready) {
            return Text.literal("(SUBMIT)").setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withClickEvent(new ClickEvent.RunCommand(commandBuilder.makeFinishCommand())));
        }
        else {
            return Text.literal("INCOMPLETE").setStyle(Style.EMPTY.withColor(Formatting.RED));
        }
    }

    @Override
    public Collection<InteractiveParameter<Void>> getLineParameters() {
        return Collections.emptyList();
    }
}
