package edu.byu.minecraft.cat.commands;

import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Civ;
import edu.byu.minecraft.cat.model.Player;
import edu.byu.minecraft.cat.model.UnlockedTitle;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Collection;
import java.util.UUID;

public class PermissionCheckers {

    /***
     * Checks if a player is in the civ
     * @param src
     * @return
     */
    public static boolean isInCiv(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        try {
            Collection<Civ> civs = CivsAndTitles.getDataAccess().getCivDAO().getForPlayer(pl);
            return !civs.isEmpty();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isBuildJudge(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        try {
            Player player = CivsAndTitles.getDataAccess().getPlayerDAO().get(pl.toString());
            if(player == null) {
                return false;
            }
            return player.role() == Player.Role.BUILD_JUDGE;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a player has at least one title.
     * @param src
     * @return
     */
    public static boolean hasTitle(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        try {
            Collection<UnlockedTitle> titles = CivsAndTitles.getDataAccess().getUnlockedTitleDAO().getAll(pl);
            return !titles.isEmpty();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Checks if a player is a civ leader
     * @param src
     * @return
     */
    public static boolean isCivLeader(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        try {
            Collection<Civ> civs = CivsAndTitles.getDataAccess().getCivDAO().getForPlayer(pl);
            for(Civ civ: civs)
            {
                if(civ.leaders().contains(pl) || civ.owner().equals(pl))
                {
                    return true;
                }
            }
            return false;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Checks if a player is a civ owner
     * @param src
     * @return
     */
    public static boolean isCivOwner(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        try {
            Collection<Civ> civs = CivsAndTitles.getDataAccess().getCivDAO().getForPlayer(pl);
            for(Civ civ: civs)
            {
                if(civ.owner().equals(pl))
                {
                    return true;
                }
            }
            return false;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a player is an Admin
     * @param src
     * @return
     */
    public static boolean isAdmin(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        try {
            Player player = CivsAndTitles.getDataAccess().getPlayerDAO().get(pl.toString());
            if(player == null) {
                return false;
            }
            return player.role() == Player.Role.ADMIN;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
