package edu.byu.minecraft.cat.commands.interactive;

import edu.byu.minecraft.cat.commands.interactive.parameters.InteractiveParameter;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Map;

public interface InteractiveLine<T> {
    Text getText(Map<String, Object> parameters, InteractiveCommandBuilder commandBuilder);
    Collection<InteractiveParameter<T>> getLineParameters();
}
