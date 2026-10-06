package edu.byu.minecraft.cat.commands.interactive;

import edu.byu.minecraft.cat.commands.interactive.parameters.InteractiveParameter;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Map;

public interface InteractiveLine<T> {
    Component getText(Map<String, Object> parameters, InteractiveCommandBuilder commandBuilder);
    Collection<InteractiveParameter<T>> getLineParameters();

    default Component getText(Map<String, Object> parameters, InteractiveCommandBuilder commandBuilder, Map<String, InteractiveParameter<?>> parameterInfoMap) {
        return getText(parameters, commandBuilder);
    }
}
