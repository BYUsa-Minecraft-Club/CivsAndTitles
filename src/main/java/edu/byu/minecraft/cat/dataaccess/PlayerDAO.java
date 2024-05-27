package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.Player;

import java.util.Collection;

public interface PlayerDAO extends SimpleDAO<Player, String> {
    Collection<Player> getForRole(Player.Role role) throws DataAccessException;
    Collection<Player> getForRank(String rank) throws DataAccessException;
    Collection<Player> getForPoints(int minPoints) throws DataAccessException;
    Collection<Player> getForPoints(int minPoints, int maxPoints) throws DataAccessException;
}
