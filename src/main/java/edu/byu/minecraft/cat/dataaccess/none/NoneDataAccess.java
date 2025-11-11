package edu.byu.minecraft.cat.dataaccess.none;

import edu.byu.minecraft.cat.dataaccess.*;

public class NoneDataAccess implements DataAccess {
    PlayerDAO playerDAO = new NonePlayerDAO();
    TitleDAO titleDAO = new NoneTitleDAO();
    UnlockedTitleDAO unlockedTitleDAO = new NoneUnlockedTitleDAO();

    public NoneDataAccess() throws DataAccessException {

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
