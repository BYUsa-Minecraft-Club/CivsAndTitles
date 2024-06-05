package edu.byu.minecraft.cat.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * Utilities specific for parsing and responding to commands
 */
public class CommandUtilities {
    /**
     * A simple wrapper function that prints a message to a command executor
     * @param ctx
     * @param message
     * @param broadCastToOps
     */
    public static void printFeedback(CommandContext<ServerCommandSource> ctx, String message, boolean broadCastToOps) {
        ctx.getSource().sendFeedback(()-> Text.literal(message), broadCastToOps);
    }

    /**
     * A simple wrapper function that prints a message to a command executor
     * @param ctx
     * @param message
     */
    public static void printFeedback(CommandContext<ServerCommandSource> ctx, String message) {
        printFeedback(ctx, message, false);
    }
}
