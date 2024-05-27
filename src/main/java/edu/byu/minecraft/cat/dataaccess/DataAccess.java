package edu.byu.minecraft.cat.dataaccess;

public interface DataAccess {
    CivDAO getCivDAO() throws DataAccessException;
    BuildDAO getBuildDAO() throws DataAccessException;
    BuildScoreDAO getBuildScoreDAO() throws DataAccessException;
    PlayerDAO getPlayerDAO() throws DataAccessException;
    CivRequestDAO getCivRequestDAO() throws DataAccessException;
    JoinRequestDAO getJoinRequestDAO() throws DataAccessException;
    TitleDAO getTitleDAO() throws DataAccessException;
    UnlockedTitleDAO getUnlockedTitleDAO() throws DataAccessException;
}
