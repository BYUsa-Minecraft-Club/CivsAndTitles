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
        return executeQuery("SELECT * FROM `new_civ_request` WHERE id = ?", this::parseSingle, id);
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<CivRequest> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM `new_civ_request`", this::parseCollection);
    }

    /**
     * @param civRequest object to insert
     * @return
     * @throws DataAccessException
     */
    @Override
    public Integer insert(CivRequest civRequest) throws DataAccessException {
        return executeUpdate("INSERT INTO `new_civ_request` (request_date, requesting_player, name, location_id) " +
                        "VALUES (?, ?, ?, ?)",
                civRequest.requestDate(), civRequest.submitter(), civRequest.name(), civRequest.locationID());
    }

    /**
     * @param id unique key
     * @throws DataAccessException
     */
    @Override
    public void delete(Integer id) throws DataAccessException {
        executeUpdate("DELETE FROM `new_civ_request` WHERE id = ?", id);
    }

    /**
     * @param civRequest unique key and values to update to
     * @throws DataAccessException
     */
    @Override
    public void update(CivRequest civRequest) throws DataAccessException {
        executeUpdate("UPDATE `new_civ_request` SET request_date = ?, requesting_player = ?, name = ?, " +
                        "location_id = ? WHERE id = ?",
                civRequest.requestDate(), civRequest.submitter(), civRequest.name(), civRequest.locationID(), civRequest.ID());
    }

    /**
     * @param uuid uuid of requesting player
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<CivRequest> getForPlayer(UUID uuid) throws DataAccessException {
        return executeQuery("SELECT * FROM `new_civ_request` WHERE requesting_player = ?", this::parseCollection, uuid);
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
                rs.getString("request_date"),
                UUID.fromString(rs.getString("requesting_player")),
                rs.getString("name"),
                rs.getInt("location_id")
        );
    }
}
