package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.Player;

import java.util.UUID;

/**
 * Data Access interface for players
 */
public interface PlayerDAO extends SimpleDAO<Player, UUID> {
    /**
     *  Gets a player's UUID using their username
     * @param username the players username to search
     * @return Players UUID
     * @throws DataAccessException Database error
     */
    UUID getPlayerUUID (String username) throws DataAccessException;

    /**
     *  Removes all instances of a title from players who are using it
     * @param title the title to search for
     * @throws DataAccessException Database error
     */
    void removeAllTitles(String title) throws DataAccessException;

    /**
     *  Gets a player by their username
     * @param username the players username to search
     * @return The player with that username
     * @throws DataAccessException Database error
     */
    default Player get(String username) throws DataAccessException {
        UUID id = getPlayerUUID(username);
        if (id != null) {
            return get(id);
        }
        return null;
    }
}
