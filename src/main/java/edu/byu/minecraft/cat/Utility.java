package edu.byu.minecraft.cat;

import java.time.LocalDateTime;

public class Utility {
    public static long getTime() {
        LocalDateTime dt = LocalDateTime.now();
        return dt.getMinute();
    }
}
