package edu.byu.minecraft.cat.util;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AsyncUtilities {
    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    /**
     * Runs an action off the main thread, then runs a callback
     * on the server thread with the result of the action. <br>
     * Note: Any DataAccessExceptions must be wrapped
     * in a RuntimeException to be thrown from a lambda
     * @param server The server to run the callback on
     * @param action The action to be run asynchronously
     * @param onFinish The callback to be called on completion of action
     * @param onDatabaseError If a database error is encountered, this function is called
     * @param onMiscError If a different error is encountered, this function is called
     */
    public static <T> void performAsync(
            MinecraftServer server,
            Supplier<T> action,
            Consumer<T> onFinish,
            Consumer<DataAccessException> onDatabaseError,
            Consumer<Throwable> onMiscError) {
        CompletableFuture
                .supplyAsync(action, EXECUTOR)
                .thenAcceptAsync(data -> server.execute(() -> {
                    onFinish.accept(data);
                }))
                .exceptionally(e -> {
                    Throwable cause = e.getCause();
                    server.execute(() -> {
                        if (e instanceof DataAccessException) {
                            onDatabaseError.accept((DataAccessException) e);
                        } else if (cause instanceof DataAccessException) {
                            onDatabaseError.accept((DataAccessException) cause);
                        } else {
                            onMiscError.accept(e);
                        }
                    });
                    return null;
                });
    }

    /**
     * Runs an action off the main thread<br>
     * Note: Any DataAccessExceptions must be wrapped
     * in a RuntimeException to be thrown from a lambda
     * @param server The server to run the callback on
     * @param action The action to be run asynchronously
     * @param onDatabaseError If a database error is encountered, this function is called
     * @param onMiscError If a different error is encountered, this function is called
     */
    public static void performAsync(
            MinecraftServer server,
            Runnable action,
            Consumer<DataAccessException> onDatabaseError,
            Consumer<Throwable> onMiscError) {
        CompletableFuture
                .runAsync(action, EXECUTOR)
                .exceptionally(e -> {
                    Throwable cause = e.getCause();
                    server.execute(() -> {
                        if (e instanceof DataAccessException) {
                            onDatabaseError.accept((DataAccessException) e);
                        } else if (cause instanceof DataAccessException) {
                            onDatabaseError.accept((DataAccessException) cause);
                        } else {
                            onMiscError.accept(e);
                        }
                    });
                    return null;
                });
    }
}
