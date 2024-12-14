package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.LocationDAO;
import edu.byu.minecraft.cat.model.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SqliteLocationDAO extends SqliteDAO<Location> implements LocationDAO {
    /**
     * Constructs a new sqlite DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqliteLocationDAO() throws DataAccessException {
    }

    /**
     * @param integer Unique key value to match against
     * @return
     * @throws DataAccessException
     */
    @Override
    public Location get(Integer integer) throws DataAccessException {
        return null;
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Location> getAll() throws DataAccessException {
        return List.of();
    }

    /**
     * @param location object to insert
     * @return
     * @throws DataAccessException
     */
    @Override
    public Integer insert(Location location) throws DataAccessException {
        return 0;
    }

    /**
     * @param integer unique key
     * @throws DataAccessException
     */
    @Override
    public void delete(Integer integer) throws DataAccessException {

    }

    /**
     * @param location unique key and values to update to
     * @throws DataAccessException
     */
    @Override
    public void update(Location location) throws DataAccessException {

    }

    /**
     * @param rs result set to retrieve row data from
     * @return
     * @throws SQLException
     */
    @Override
    protected Location parse(ResultSet rs) throws SQLException {
        return null;
    }
}
