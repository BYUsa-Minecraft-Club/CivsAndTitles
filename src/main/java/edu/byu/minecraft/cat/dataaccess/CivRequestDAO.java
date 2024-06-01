package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.CivRequest;

import java.util.Collection;
import java.util.UUID;

/**
 * Data Access interface for civ creation requests
 */
public interface CivRequestDAO extends SimpleDAO<CivRequest, Integer> {

    /**
     * Gets all civ requests a player has made
     * @param uuid uuid of requesting player
     * @return all active civ requests made by player
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<CivRequest> getForPlayer(UUID uuid) throws DataAccessException;
}
