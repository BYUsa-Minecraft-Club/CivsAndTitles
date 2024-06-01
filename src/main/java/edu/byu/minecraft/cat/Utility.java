package edu.byu.minecraft.cat;

import edu.byu.minecraft.cat.model.Location;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.LocalDateTime;

public class Utility {
    /***
     * It calls the current time to the minute.  That's it.  Groundbreaking.
     * @return a longer of the minute.
     */
    public static long getTime() {
        LocalDateTime dt = LocalDateTime.now();
        return dt.getMinute();
    }

    /***
     * Creates a Location object based on a player model
     * @param player the ServerPlayerEntity data for the player
     * @return null if the player is null.  Otherwise, a Location Object.
     */
    public static Location getPlayerLocation(ServerPlayerEntity player) {
        if (player == null) { return null; }
        return new Location(player.getBlockX(), player.getBlockY(), player.getBlockZ(), player.getWorld().getRegistryKey().getRegistry(), player.getHeadYaw(), player.getPitch());
    }
}
