package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.JoinRequest;

import java.util.Collection;

/**
 * Data Access interface for civ join requests
 */
public interface JoinRequestDAO extends SimpleDAO<JoinRequest, Integer> {

    /**
     * Gets all join requests for a particular civ
     *
     * @param civID civ ID to get request
     * @return all join requests for civ
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<JoinRequest> getForCiv(int civID) throws DataAccessException;

}
