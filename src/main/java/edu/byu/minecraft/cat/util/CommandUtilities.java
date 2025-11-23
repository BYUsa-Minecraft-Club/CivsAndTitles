package edu.byu.minecraft.cat.util;

import com.mojang.brigadier.context.CommandContext;
import edu.byu.minecraft.cat.CivsAndTitles;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.function.Supplier;

/**
 * Utilities specific for parsing and responding to commands
 */
public class CommandUtilities {
    /**
     * Runs a command process off the main thread then sends feedback to the caller
     *
     * @param ctx The command context to receive feedback
     * @param action A command action. Returns true if an error was encountered
     * @param onSuccess A Text supplier to be sent as feedback on success
     * @param onError A Text supplier to be sent as feedback on error
     */
    public static int perform(
            CommandContext<ServerCommandSource> ctx,
            Supplier<Boolean> action,
            Supplier<Text> onSuccess,
            Supplier<Text> onError) {
        AsyncUtilities.performAsync(ctx.getSource().getServer(),
                action,
                failed -> {
                    if (failed) ctx.getSource().sendError(onError.get());
                    else ctx.getSource().sendFeedback(onSuccess, false);
                },
                error -> {
                    ctx.getSource().sendError(Text.literal("An error occurred accessing the database")
                            .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Text.of(error.toString())))));
                    CivsAndTitles.LOGGER.error("An error occurred accessing the database: ",error);
                },
                error -> {
                    ctx.getSource().sendError(Text.literal("An unknown error occurred running that command")
                            .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Text.of(error.toString())))));
                    CivsAndTitles.LOGGER.error("An unknown error occurred: ",error);
                });
        return 0;
    }
}
