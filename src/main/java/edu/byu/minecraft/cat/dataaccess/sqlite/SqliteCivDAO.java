package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.CivDAO;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Civ;
import edu.byu.minecraft.cat.model.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SqliteCivDAO extends SqliteDAO<Civ> implements CivDAO {

    /**
     * Constructs a new sqlite DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqliteCivDAO() throws DataAccessException {
    }

    /**
     * @param integer Unique key value to match against
     * @return
     * @throws DataAccessException
     */
    @Override
    public Civ get(Integer integer) throws DataAccessException {
        return executeQuery("SELECT * FROM `civ` WHERE id = ?", this::parseSingle, integer);
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Civ> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM `civ`", this::parseCollection);
    }

    /**
     * @param civ object to insert
     * @return
     * @throws DataAccessException
     */
    @Override
    public Integer insert(Civ civ) throws DataAccessException {
        return executeUpdate("INSERT INTO `civ` (name, points, is_active, incorporated, " +
                        "location_id, founded_date) VALUES (?, ?, ?, ?, ?, ?)",
                civ.name(), civ.numPoints(), civ.isActive(), civ.incorporated(), 
                civ.locationID(), civ.createdDate());
    }

    /**
     * @param id unique key
     * @throws DataAccessException
     */
    @Override
    public void delete(Integer id) throws DataAccessException {
        executeUpdate("DELETE FROM `civ` WHERE id = ?", id);
    }

    /**
     * @param civ unique key and values to update to
     * @throws DataAccessException
     */
    @Override
    public void update(Civ civ) throws DataAccessException {
        executeUpdate("UPDATE `civ` SET name = ?, points = ?, is_active = ?, " +
                        "incorporated = ?, location_id = ?, founded_date = ? WHERE id = ?",
                civ.name(), civ.numPoints(), civ.isActive(), civ.incorporated(),
                civ.locationID(), civ.createdDate(), civ.ID());
    }

    /**
     * @param uuid uuid of player
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Civ> getForPlayer(UUID uuid) throws DataAccessException {
        return executeQuery("""
                SELECT * FROM civ
                JOIN civ_player ON civ.id = civ_player.civ_id
                WHERE civ_player.player_uuid = ?
                """, this::parseCollection, uuid);
    }

    /**
     * @param name civ name to retrieve matching civ
     * @return
     * @throws DataAccessException
     */
    @Override
    public Civ getForName(String name) throws DataAccessException {
        return executeQuery("SELECT * FROM `civ` WHERE name = ?", this::parseSingle, name);
    }

    /**
     * @param active activity status to match
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Civ> getForActivity(boolean active) throws DataAccessException {
        return executeQuery("SELECT * FROM `civ` WHERE is_active = ?", this::parseCollection, active);
    }

    /**
     * @param incorporated incorporation status to match
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Civ> getForIncorporation(boolean incorporated) throws DataAccessException {
        return executeQuery("SELECT * FROM `civ` WHERE incorporated = ?", this::parseCollection, incorporated);
    }

    /**
     * @param minPoints minimum number of points
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Civ> getForPoints(int minPoints) throws DataAccessException {
        return executeQuery("SELECT * FROM `civ` WHERE points >= ?", this::parseCollection, minPoints);
    }

    /**
     * @param minPoints lower bound of point range
     * @param maxPoints upper bound of point range
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Civ> getForPoints(int minPoints, int maxPoints) throws DataAccessException {
        return executeQuery("SELECT * FROM `civ` WHERE points >= ? AND points <= ?",
                this::parseCollection, minPoints, maxPoints);
    }

    /**
     * @param rs result set to retrieve row data from
     * @return
     * @throws SQLException
     */
    @Override
    protected Civ parse(ResultSet rs) throws SQLException {
        return new Civ(rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("points"),
                rs.getBoolean("is_active"),
                rs.getBoolean("incorporated"),
                rs.getInt("location_id"),
                rs.getString("founded_date"));
    }
}
