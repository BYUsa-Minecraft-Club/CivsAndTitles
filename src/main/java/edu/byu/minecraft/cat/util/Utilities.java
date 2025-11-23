package edu.byu.minecraft.cat.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilities that are useful in multiple contexts
 */
public class Utilities {
    /***
     * Gets a human-readable timestamp to the current minute
     * @return the created timestamp
     */
    public static String getTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
