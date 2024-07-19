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
        return executeQuery("SELECT * FROM `CIVS` WHERE id = ?", this::parseSingle, integer);
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Civ> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM `CIVS`", this::parseCollection);
    }

    /**
     * @param civ object to insert
     * @return
     * @throws DataAccessException
     */
    @Override
    public Integer insert(Civ civ) throws DataAccessException {
        return executeUpdate("INSERT INTO `CIVS` (name, points, hasBorder, status, founder, owner, " +
                "leaders, contributers, members, xCoord, yCoord, zCoord, dimension, tilt, direction, foundedDate) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                civ.name(), civ.numPoints(), civ.incorporated(), civ.active(), civ.founder(), civ.owner(),
                setToString(civ.leaders()), setToString(civ.contributors()), setToString(civ.members()),
                civ.location().x(), civ.location().y(), civ.location().z(), civ.location().world(),
                civ.location().pitch(), civ.location().yaw(), civ.createdDate());
    }

    /**
     * @param id unique key
     * @throws DataAccessException
     */
    @Override
    public void delete(Integer id) throws DataAccessException {
        executeUpdate("DELETE FROM `CIVS` WHERE id = ?", id);
    }

    /**
     * @param civ unique key and values to update to
     * @throws DataAccessException
     */
    @Override
    public void update(Civ civ) throws DataAccessException {
        executeUpdate("UPDATE `CIVS` SET name = ?, points = ?, hasBorder = ?, status = ?, founder = ?, " +
                        "owner = ?, leaders = ?, contributers = ?, members = ?, xCoord = ?, yCoord = ?, zCoord = ?, " +
                        "dimension = ?, tilt = ?, direction = ?, foundedDate = ? WHERE id = ?",
                civ.name(), civ.numPoints(), civ.incorporated(), civ.active(), civ.founder(), civ.owner(),
                setToString(civ.leaders()), setToString(civ.contributors()), setToString(civ.members()),
                civ.location().x(), civ.location().y(), civ.location().z(), civ.location().world(),
                civ.location().pitch(), civ.location().yaw(), civ.createdDate(), civ.ID());
    }

    /**
     * @param uuid uuid of player
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Civ> getForPlayer(UUID uuid) throws DataAccessException {
        String query = String.format("SELECT * FROM `CIVS` WHERE owner = ? OR leaders LIKE '%%%1$s%%' " +
                "OR members LIKE '%%%1$s%%' OR contributers LIKE '%%%1$s%%'", uuid);
        return executeQuery(query, this::parseCollection, uuid);
    }

    /**
     * @param name civ name to retrieve matching civ
     * @return
     * @throws DataAccessException
     */
    @Override
    public Civ getForName(String name) throws DataAccessException {
        return executeQuery("SELECT * FROM `CIVS` WHERE name = ?", this::parseSingle, name);
    }

    /**
     * @param active activity status to match
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Civ> getForActivity(boolean active) throws DataAccessException {
        return executeQuery("SELECT * FROM `CIVS` WHERE status = ?", this::parseCollection, active);
    }

    /**
     * @param incorporated incorporation status to match
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Civ> getForIncorporation(boolean incorporated) throws DataAccessException {
        return executeQuery("SELECT * FROM `CIVS` WHERE hasBorder = ?", this::parseCollection, incorporated);
    }

    /**
     * @param minPoints minimum number of points
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Civ> getForPoints(int minPoints) throws DataAccessException {
        return executeQuery("SELECT * FROM `CIVS` WHERE points >= ?", this::parseCollection, minPoints);
    }

    /**
     * @param minPoints lower bound of point range
     * @param maxPoints upper bound of point range
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Civ> getForPoints(int minPoints, int maxPoints) throws DataAccessException {
        return executeQuery("SELECT * FROM `CIVS` WHERE points >= ? AND points <= ?",
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
                rs.getBoolean("hasBorder"),
                rs.getBoolean("status"),
                UUID.fromString(rs.getString("founder")),
                UUID.fromString(rs.getString("owner")),
                stringToSet(rs.getString("leaders")),
                stringToSet(rs.getString("contributers")),
                stringToSet(rs.getString("members")),
                parseLocation(rs),
                rs.getString("foundedDate"));
    }
}
