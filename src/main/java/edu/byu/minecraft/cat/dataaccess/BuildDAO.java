package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.Build;

import java.util.Collection;
import java.util.UUID;

/**
 * Data Access interface of Builds
 */
public interface BuildDAO extends SimpleDAO<Build, Integer> {

    /**
     * Gets all builds for a single Civ ID
     *
     * @param civID civ ID to find builds of
     * @return all builds for civ
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Build> getAllForCiv(int civID) throws DataAccessException;


    /**
     * Gets all builds submitted by one player
     *
     * @param uuid uuid of player
     * @return all builds submitted by player
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Build> getAllForSubmitter(UUID uuid) throws DataAccessException;


    /**
     * Gets all builds a player contributed to
     *
     * @param uuid uuid of player
     * @return all builds player contributed to
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Build> getAllForBuilder(UUID uuid) throws DataAccessException;


    /**
     * Gets all builds of a particular status
     *
     * @param status status to find builds matching
     * @return all builds matching status
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Build> getAllForStatus(Build.JudgeStatus status) throws DataAccessException;

}
