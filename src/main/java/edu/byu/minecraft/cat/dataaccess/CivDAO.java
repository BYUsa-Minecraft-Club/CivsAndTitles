package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.Civ;

import java.util.Collection;
import java.util.UUID;

/**
 * Data Access interface for Civs
 */
public interface CivDAO extends SimpleDAO<Civ, Integer> {

    /**
     * Gets all civs a player owns, is a leader of, is a member of, or has contributed to
     *
     * @param uuid uuid of player
     * @return civs related to player
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Civ> getForPlayer(UUID uuid) throws DataAccessException;


    /**
     * Gets a civ by the name
     * @param name civ name to retrieve matching civ
     * @return a civ with name name or null if it doesn't exist
     * @throws DataAccessException If database cannot be accessed
     */
    Civ getForName(String name) throws DataAccessException;

    /**
     * Gets civs with a certain activity status
     *
     * @param active activity status to match
     * @return all civs matching activity status
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Civ> getForActivity(boolean active) throws DataAccessException;


    /**
     * Gets civs with a certain incorporation status
     *
     * @param incorporated incorporation status to match
     * @return all civs matching incorporation status
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Civ> getForIncorporation(boolean incorporated) throws DataAccessException;


    /**
     * Gets all civs with more than a minimum number of points
     *
     * @param minPoints minimum number of points
     * @return all civs with at least minPoints points
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Civ> getForPoints(int minPoints) throws DataAccessException;


    /**
     * Gets all civs with points between two values
     *
     * @param minPoints lower bound of point range
     * @param maxPoints upper bound of point range
     * @return all civs with points in point range
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Civ> getForPoints(int minPoints, int maxPoints) throws DataAccessException;

}
