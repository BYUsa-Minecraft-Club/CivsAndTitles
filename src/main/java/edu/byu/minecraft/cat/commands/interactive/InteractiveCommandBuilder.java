package edu.byu.minecraft.cat.commands.interactive;

public interface InteractiveCommandBuilder {
    String makeSetCommand(String paramName);
    String makeSetCommandWithArg(String param, String val);
    String makeDisplayCommand();

    String makeFinishCommand();
}
