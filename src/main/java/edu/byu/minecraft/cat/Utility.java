package edu.byu.minecraft.cat;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Civ;
import edu.byu.minecraft.cat.model.Player;

import java.time.LocalDateTime;
import java.util.UUID;

public class Utility {
    public static long getTime() {
        LocalDateTime dt = LocalDateTime.now();
        return dt.getMinute();
    }

    public static boolean isPlayerInCiv(UUID uuid, String civName) throws DataAccessException {
        Civ civ= CivsAndTitles.getDataAccess().getCivDAO().getForName(civName);
        return civ.members().contains(uuid);
    }
    public static boolean isPlayerInCiv(UUID uuid, Integer civId) throws DataAccessException {
        Civ civ= CivsAndTitles.getDataAccess().getCivDAO().get(civId);
        return civ.members().contains(uuid);
    }

    public static boolean isPlayerCivLeader(UUID uuid, String civName) throws DataAccessException {
        Civ civ= CivsAndTitles.getDataAccess().getCivDAO().getForName(civName);
        return civ.leaders().contains(uuid);
    }

    public static boolean isPlayerCivLeader(UUID uuid, Integer civId) throws DataAccessException {
        Civ civ= CivsAndTitles.getDataAccess().getCivDAO().get(civId);
        return civ.leaders().contains(uuid);
    }
}
