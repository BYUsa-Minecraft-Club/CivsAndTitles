package edu.byu.minecraft.cat.commands.interactive.parameters;

import org.jetbrains.annotations.Nullable;

public class InteractiveResult<T> {
    boolean success;

    @Nullable
    T value;

    @Nullable
    String error;

    private InteractiveResult(boolean success, @Nullable T value, @Nullable String error) {
        this.success = success;
        this.value = value;
        this.error = error;
    }

    public static <T> InteractiveResult<T> success(T value) {
        return new InteractiveResult<>(true, value, null);
    }

    public static <T> InteractiveResult<T> error(String message) {
        return new InteractiveResult<>(false, null, message);
    }

    public static <T> InteractiveResult<T> error(String message, T partial) {
        return new InteractiveResult<>(false, partial, message);
    }

    @Nullable
    public T getOrPartial() {
        return value;
    }

    public T getOrThrow() {
        if (!success || value == null) {
            throw new RuntimeException("Couldn't extract value from InteractiveResult");
        }
        return value;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isError() {
        return !success;
    }
}
