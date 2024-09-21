package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.CivRequestDAO;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.CivRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class SqliteCivRequestDAO extends SqliteDAO<CivRequest> implements CivRequestDAO {
    /**
     * Constructs a new sqlite DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqliteCivRequestDAO() throws DataAccessException {
    }


    /**
     * @param id Unique key value to match against
     * @return
     * @throws DataAccessException
     */
    @Override
    public CivRequest get(Integer id) throws DataAccessException {
        return executeQuery("SELECT * FROM `NEW_CIV_REQUESTS` WHERE id = ?", this::parseSingle, id);
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<CivRequest> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM `NEW_CIV_REQUESTS`", this::parseCollection);
    }

    /**
     * @param civRequest object to insert
     * @return
     * @throws DataAccessException
     */
    @Override
    public Integer insert(CivRequest civRequest) throws DataAccessException {
        return executeUpdate("INSERT INTO `NEW_CIV_REQUESTS` (timestamp, requester, name, " +
                        "xCoord, yCoord, zCoord, Dimension, tilt, direction) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                civRequest.timestamp(), civRequest.submitter(), civRequest.name(), civRequest.location().x(),
                civRequest.location().y(), civRequest.location().z(), civRequest.location().world(),
                civRequest.location().pitch(), civRequest.location().yaw());
    }

    /**
     * @param id unique key
     * @throws DataAccessException
     */
    @Override
    public void delete(Integer id) throws DataAccessException {
        executeUpdate("DELETE FROM `NEW_CIV_REQUESTS` WHERE id = ?", id);
    }

    /**
     * @param civRequest unique key and values to update to
     * @throws DataAccessException
     */
    @Override
    public void update(CivRequest civRequest) throws DataAccessException {
        executeUpdate("UPDATE `NEW_CIV_REQUESTS` SET timestamp = ?, requester = ?, name = ?, " +
                        "xCoord = ?, yCoord = ?, zCoord = ?, Dimension = ?, tilt = ?, direction = ? " +
                        "WHERE id = ?",
                civRequest.timestamp(), civRequest.submitter(), civRequest.name(), civRequest.location().x(),
                civRequest.location().y(), civRequest.location().z(), civRequest.location().world(),
                civRequest.location().pitch(), civRequest.location().yaw(), civRequest.ID());
    }

    /**
     * @param uuid uuid of requesting player
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<CivRequest> getForPlayer(UUID uuid) throws DataAccessException {
        return executeQuery("SELECT * FROM `NEW_CIV_REQUESTS` WHERE requester = ?", this::parseCollection, uuid);
    }

    /**
     * @param rs result set to retrieve row data from
     * @return
     * @throws SQLException
     */
    @Override
    protected CivRequest parse(ResultSet rs) throws SQLException {
        return new CivRequest(
                rs.getInt("id"),
                rs.getLong("timestamp"),
                UUID.fromString(rs.getString("requester")),
                rs.getString("name"),
                parseLocation(rs)
        );
    }
}
