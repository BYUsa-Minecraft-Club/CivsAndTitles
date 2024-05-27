package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.Player;

import java.util.Collection;

/**
 * Data Access interface for players
 */
public interface PlayerDAO extends SimpleDAO<Player, String> {

    /**
     * Gets all players with a particular role
     *
     * @param role role to find players
     * @return all players with role
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Player> getForRole(Player.Role role) throws DataAccessException;


    /**
     * Gets all players with more than a minimum number of points
     *
     * @param minPoints minimum number of points
     * @return all players with at least minPoints points
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Player> getForPoints(int minPoints) throws DataAccessException;


    /**
     * Gets all players with points between two values
     *
     * @param minPoints lower bound of point range
     * @param maxPoints upper bound of point range
     * @return all players with points in point range
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Player> getForPoints(int minPoints, int maxPoints) throws DataAccessException;

}
