package edu.byu.minecraft.cat.util;

import edu.byu.minecraft.cat.model.Location;
import net.minecraft.server.network.ServerPlayerEntity;

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

    /***
     * Creates a Location object based on a player model
     * @param player the ServerPlayerEntity data for the player
     * @return null if the player is null.  Otherwise, a Location Object.
     */
    public static Location getPlayerLocation(ServerPlayerEntity player) {
        if (player == null) { return null; }
        return new Location(0, player.getBlockX(), player.getBlockY(), player.getBlockZ(),
                player.getWorld().getRegistryKey().getRegistry(), player.getHeadYaw(), player.getPitch());
    }

}
