package edu.byu.minecraft.cat.commands.interactive;

import edu.byu.minecraft.cat.commands.interactive.parameters.InteractiveParameter;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class InteractiveTextLine implements InteractiveLine {
    Text text;
    public InteractiveTextLine(Text text){
        this.text = text;
    }
    @Override
    public Text getText(Map<String, Object> parameters, InteractiveCommandBuilder builder) {
        return text;
    }

    @Override
    public Collection<InteractiveParameter> getLineParameters() {
        return new ArrayList<>();
    }
}
