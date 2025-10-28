package edu.byu.minecraft.cat.dataaccess;

/**
 * Abstract factory interface for DAO's of a particular storage system
 */
public interface DataAccess {
    /**
     * Gets/Makes a player DAO for the current storage system
     * @return a player DAO
     * @throws DataAccessException If database cannot be accessed
     */
    PlayerDAO getPlayerDAO() throws DataAccessException;

    /**
     * Gets/Makes a title DAO for the current storage system
     * @return a title DAO
     * @throws DataAccessException If database cannot be accessed
     */
    TitleDAO getTitleDAO() throws DataAccessException;

    /**
     * Gets/Makes an unlocked title DAO for the current storage system
     * @return an unlocked title DAO
     * @throws DataAccessException If database cannot be accessed
     */
    UnlockedTitleDAO getUnlockedTitleDAO() throws DataAccessException;
}
