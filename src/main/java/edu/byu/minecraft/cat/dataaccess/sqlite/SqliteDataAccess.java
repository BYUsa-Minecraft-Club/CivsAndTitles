package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.*;

public class SqliteDataAccess implements DataAccess {
    private final PlayerDAO playerDAO;
    private final TitleDAO titleDAO;
    private final UnlockedTitleDAO unlockedTitleDAO;
    
    public SqliteDataAccess() throws DataAccessException {
        playerDAO = new SqlitePlayerDAO();
        titleDAO = new SqliteTitleDAO();
        unlockedTitleDAO = new SqliteUnlockedTitleDAO();
    }

    @Override
    public PlayerDAO getPlayerDAO() throws DataAccessException {
        return playerDAO;
    }

    @Override
    public TitleDAO getTitleDAO() throws DataAccessException {
        return titleDAO;
    }

    @Override
    public UnlockedTitleDAO getUnlockedTitleDAO() throws DataAccessException {
        return unlockedTitleDAO;
    }
}
