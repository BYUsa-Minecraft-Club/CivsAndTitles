package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.*;

public class SqliteDataAccess implements DataAccess {
    private final CivDAO civDAO;;
    private final CivParticipantDAO civParticipantDAO;;
    private final BuildDAO buildDAO;;
    private final BuildScoreDAO buildScoreDAO;;
    private final BuilderDAO builderDAO;;
    private final PlayerDAO playerDAO;;
    private final CivRequestDAO civRequestDAO;;
    private final JoinRequestDAO joinRequestDAO;;
    private final TitleDAO titleDAO;;
    private final UnlockedTitleDAO unlockedTitleDAO;;
    private final LocationDAO locationDAO;;
    
    public SqliteDataAccess() throws DataAccessException {
        civDAO = new SqliteCivDAO();
        civParticipantDAO = new SqliteCivParticipantDAO();
        buildDAO = new SqliteBuildDAO();
        buildScoreDAO = new SqliteBuildScoreDAO();
        builderDAO = new SqliteBuilderDAO();
        playerDAO = new SqlitePlayerDAO();
        civRequestDAO = new SqliteCivRequestDAO();
        joinRequestDAO = new SqliteJoinRequestDAO();
        titleDAO = new SqliteTitleDAO();
        unlockedTitleDAO = new SqliteUnlockedTitleDAO();
        locationDAO = new SqliteLocationDAO();
    }
    
    /**
     * @return 
     * @throws DataAccessException
     */
    @Override
    public CivDAO getCivDAO() throws DataAccessException {
        return civDAO;
    }

    /**
     * @return 
     * @throws DataAccessException
     */
    @Override
    public CivParticipantDAO getCivParticipantDAO() throws DataAccessException {
        return civParticipantDAO;
    }

    /**
     * @return 
     * @throws DataAccessException
     */
    @Override
    public BuildDAO getBuildDAO() throws DataAccessException {
        return buildDAO;
    }

    /**
     * @return 
     * @throws DataAccessException
     */
    @Override
    public BuildScoreDAO getBuildScoreDAO() throws DataAccessException {
        return buildScoreDAO;
    }

    /**
     * @return 
     * @throws DataAccessException
     */
    @Override
    public BuilderDAO getBuilderDAO() throws DataAccessException {
        return builderDAO;
    }

    /**
     * @return 
     * @throws DataAccessException
     */
    @Override
    public PlayerDAO getPlayerDAO() throws DataAccessException {
        return playerDAO;
    }

    /**
     * @return 
     * @throws DataAccessException
     */
    @Override
    public CivRequestDAO getCivRequestDAO() throws DataAccessException {
        return civRequestDAO;
    }

    /**
     * @return 
     * @throws DataAccessException
     */
    @Override
    public JoinRequestDAO getJoinRequestDAO() throws DataAccessException {
        return joinRequestDAO;
    }

    /**
     * @return 
     * @throws DataAccessException
     */
    @Override
    public TitleDAO getTitleDAO() throws DataAccessException {
        return titleDAO;
    }

    /**
     * @return 
     * @throws DataAccessException
     */
    @Override
    public UnlockedTitleDAO getUnlockedTitleDAO() throws DataAccessException {
        return unlockedTitleDAO;
    }

    /**
     * @return 
     * @throws DataAccessException
     */
    @Override
    public LocationDAO getLocationDAO() throws DataAccessException {
        return locationDAO;
    }
}
