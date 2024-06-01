package edu.byu.minecraft.cat;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Civ;
import edu.byu.minecraft.cat.model.Player;

import edu.byu.minecraft.cat.model.Location;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.LocalDateTime;
import java.util.UUID;

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

    /**
     * Returns if a player is in a civilization
     * @param uuid
     * @param civName
     * @return
     * @throws DataAccessException
     */
    public static boolean isPlayerInCiv(UUID uuid, String civName) throws DataAccessException {
        Civ civ= CivsAndTitles.getDataAccess().getCivDAO().getForName(civName);
        return civ.members().contains(uuid);
    }

    /**
     * Returns if a player is in a civilization
     * @param uuid
     * @param civId
     * @return
     * @throws DataAccessException
     */
    public static boolean isPlayerInCiv(UUID uuid, Integer civId) throws DataAccessException {
        Civ civ= CivsAndTitles.getDataAccess().getCivDAO().get(civId);
        return civ.members().contains(uuid);
    }

    /**
     * Returns if a player is a leader of a civ
     * @param uuid
     * @param civName
     * @return
     * @throws DataAccessException
     */
    public static boolean isPlayerCivLeader(UUID uuid, String civName) throws DataAccessException {
        Civ civ= CivsAndTitles.getDataAccess().getCivDAO().getForName(civName);
        return civ.leaders().contains(uuid);
    }

    /**
     * Returns if a player is a leader of a civ
     * @param uuid
     * @param civId
     * @return
     * @throws DataAccessException
     */
    public static boolean isPlayerCivLeader(UUID uuid, Integer civId) throws DataAccessException {
        Civ civ= CivsAndTitles.getDataAccess().getCivDAO().get(civId);
        return civ.leaders().contains(uuid);
    }
}
