package edu.byu.minecraft.cat.commands.interactive;

import edu.byu.minecraft.cat.commands.interactive.parameters.InteractiveParameter;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class InteractiveTextLine implements InteractiveLine<Component> {
    Component text;
    public InteractiveTextLine(Component text){
        this.text = text;
    }
    @Override
    public Component getText(Map<String, Object> parameters, InteractiveCommandBuilder builder) {
        return text;
    }

    @Override
    public Collection<InteractiveParameter<Component>> getLineParameters() {
        return new ArrayList<>();
    }
}
