package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.LocationDAO;
import edu.byu.minecraft.cat.model.Location;
import net.minecraft.util.Identifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class SqliteLocationDAO extends SqliteDAO<Location> implements LocationDAO {
    /**
     * Constructs a new sqlite DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqliteLocationDAO() throws DataAccessException {
    }

    /**
     * @param id Unique key value to match against
     * @return
     * @throws DataAccessException
     */
    @Override
    public Location get(Integer id) throws DataAccessException {
        return executeQuery("SELECT * FROM location WHERE id = ?", this::parseSingle, id);
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Location> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM location", this::parseCollection);
    }

    /**
     * @param location object to insert
     * @return
     * @throws DataAccessException
     */
    @Override
    public Integer insert(Location location) throws DataAccessException {
        return executeUpdate("INSERT INTO location (x_coordinate, y_coordinate, z_coordinate, " +
                "dimension, yaw, pitch ) VALUES (?, ?, ?, ?, ?, ?)",
                location.x(), location.y(), location.z(), location.world(), location.yaw(), location.pitch());
    }

    /**
     * @param id unique key
     * @throws DataAccessException
     */
    @Override
    public void delete(Integer id) throws DataAccessException {
        executeUpdate("DELETE FROM location WHERE id = ?", id);
    }

    /**
     * @param location unique key and values to update to
     * @throws DataAccessException
     */
    @Override
    public void update(Location location) throws DataAccessException {
        executeUpdate("UPDATE location SET x_coordinate = ?, y_coordinate = ?, z_coordinate = ?, " +
                        "dimension = ?, yaw = ?, pitch = ? WHERE id = ?",
                location.x(), location.y(), location.z(), location.world(), location.yaw(), location.pitch(),
                location.id());
    }

    /**
     * @param rs result set to retrieve row data from
     * @return
     * @throws SQLException
     */
    @Override
    protected Location parse(ResultSet rs) throws SQLException {
        return new Location(rs.getInt("id"),
                rs.getInt("x_coordinate"),
                rs.getInt("y_coordinate"),
                rs.getInt("z_coordinate"),
                Identifier.tryParse(rs.getString("dimension")),
                rs.getFloat("yaw"),
                rs.getFloat("pitch"));
    }
}
