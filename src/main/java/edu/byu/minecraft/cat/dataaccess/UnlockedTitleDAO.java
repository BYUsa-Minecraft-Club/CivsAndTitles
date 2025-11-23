package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.UnlockedTitle;

import java.util.Collection;
import java.util.UUID;

/**
 * Data Access interface for titles unlocked by players
 */
public interface UnlockedTitleDAO {

    /**
     * Gets all titles unlocked by a player
     *
     * @param uuid uuid of player
     * @return all titles unlocked by player
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<UnlockedTitle> getAll(UUID uuid) throws DataAccessException;


    /**
     * Gets all titles matching a title
     *
     * @param title title to match
     * @return all unlocked titles matching title
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<UnlockedTitle> getAll(String title) throws DataAccessException;


    /**
     * Adds a new unlocked title
     *
     * @param title unlocked title to add
     * @throws DataAccessException If database cannot be accessed
     */
    void insert(UnlockedTitle title) throws DataAccessException;


    /**
     * Deletes a particular unlocked title
     *
     * @param uuid  player to remove title from
     * @param title title to remove
     * @throws DataAccessException If database cannot be accessed
     */
    void delete(UUID uuid, String title) throws DataAccessException;


    /**
     * Deletes all unlocked titles matching a title
     *
     * @param title title to match
     * @throws DataAccessException If database cannot be accessed
     */
    void deleteAll(String title) throws DataAccessException;

    /**
     * Gets the unlocked title if it exists<br>
     * Basically only used for checking if a player has a title
     *
     * @param uuid  player to check title for
     * @param title title to match
     * @throws DataAccessException If database cannot be accessed
     */
    UnlockedTitle get(UUID uuid, String title) throws DataAccessException;
}
