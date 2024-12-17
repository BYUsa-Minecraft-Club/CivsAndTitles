package edu.byu.minecraft.cat.util;

import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Civ;
import edu.byu.minecraft.cat.model.CivParticipantPlayer;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * Utilites specifically for dealing with civs
 */
public class CivUtilities {
//    /**
//     * Returns if a player is in a civilization
//     * @param uuid
//     * @param civName
//     * @return
//     * @throws DataAccessException
//     */
//    public static boolean isPlayerInCiv(UUID uuid, String civName) throws DataAccessException {
//        Civ civ= CivsAndTitles.getDataAccess().getCivDAO().getForName(civName);
//        return civ.members().contains(uuid);
//    }
//
//    /**
//     * Returns if a player is in a civilization
//     * @param uuid
//     * @param civId
//     * @return
//     * @throws DataAccessException
//     */
//    public static boolean isPlayerInCiv(UUID uuid, Integer civId) throws DataAccessException {
//        Civ civ= CivsAndTitles.getDataAccess().getCivDAO().get(civId);
//        return civ.members().contains(uuid);
//    }
//
//    /**
//     * Returns if a player is a leader of a civ
//     * @param uuid
//     * @param civName
//     * @return
//     * @throws DataAccessException
//     */
//    public static boolean isPlayerCivLeader(UUID uuid, String civName) throws DataAccessException {
//        Civ civ= CivsAndTitles.getDataAccess().getCivDAO().getForName(civName);
//        return civ.leaders().contains(uuid);
//    }

    /**
     * Returns if a player is a leader of a civ
     * @param uuid
     * @param civId
     * @return
     * @throws DataAccessException
     */
    public static boolean isPlayerCivLeader(UUID uuid, Integer civId) throws DataAccessException {
        var civPlayers = CivsAndTitles.getDataAccess().getCivParticipantDAO().getAllForCiv(civId);
        return civPlayers.stream().anyMatch(cpp -> cpp.playerUUID().equals(uuid) &&
                cpp.status() == CivParticipantPlayer.Status.OWNER || cpp.status() == CivParticipantPlayer.Status.LEADER);
    }
}
