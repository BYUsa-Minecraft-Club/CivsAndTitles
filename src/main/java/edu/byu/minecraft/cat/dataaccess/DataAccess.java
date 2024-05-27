package edu.byu.minecraft.cat.dataaccess;

/**
 * Abstract factory interface for DAO's of a particular storage system
 */
public interface DataAccess {

    /**
     * Gets/Makes a civ DAO for the current storage system
     * @return a civ DAO
     * @throws DataAccessException If database cannot be accessed
     */
    CivDAO getCivDAO() throws DataAccessException;


    /**
     * Gets/Makes a build DAO for the current storage system
     * @return a build DAO
     * @throws DataAccessException If database cannot be accessed
     */
    BuildDAO getBuildDAO() throws DataAccessException;


    /**
     * Gets/Makes a build score DAO for the current storage system
     * @return a build score DAO
     * @throws DataAccessException If database cannot be accessed
     */
    BuildScoreDAO getBuildScoreDAO() throws DataAccessException;


    /**
     * Gets/Makes a player DAO for the current storage system
     * @return a player DAO
     * @throws DataAccessException If database cannot be accessed
     */
    PlayerDAO getPlayerDAO() throws DataAccessException;


    /**
     * Gets/Makes a civ request DAO for the current storage system
     * @return a civ request DAO
     * @throws DataAccessException If database cannot be accessed
     */
    CivRequestDAO getCivRequestDAO() throws DataAccessException;


    /**
     * Gets/Makes a join request DAO for the current storage system
     * @return a join request DAO
     * @throws DataAccessException If database cannot be accessed
     */
    JoinRequestDAO getJoinRequestDAO() throws DataAccessException;


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
