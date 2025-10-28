package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.Player;

import java.util.Collection;
import java.util.UUID;

/**
 * Data Access interface for players
 */
public interface PlayerDAO extends SimpleDAO<Player, UUID> {

    /**
     * Gets all players with a particular role
     *
     * @param role role to find players
     * @return all players with role
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Player> getForRole(Player.Role role) throws DataAccessException;

    /**
     *  Gets a player's UUID using their username
     * @param username the players username to search
     * @return Players UUID
     * @throws DataAccessException
     */
    UUID getPlayerUUID (String username) throws DataAccessException;

}
