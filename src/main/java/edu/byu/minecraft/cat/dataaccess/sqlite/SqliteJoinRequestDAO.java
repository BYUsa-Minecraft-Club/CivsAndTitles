package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.JoinRequestDAO;
import edu.byu.minecraft.cat.model.JoinRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SqliteJoinRequestDAO extends SqliteDAO<JoinRequest> implements JoinRequestDAO {
    /**
     * Constructs a new sqlite DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqliteJoinRequestDAO() throws DataAccessException {
    }

    /**
     * @param id Unique key value to match against
     * @return
     * @throws DataAccessException
     */
    @Override
    public JoinRequest get(Integer id) throws DataAccessException {
        return executeQuery("SELECT * FROM join_civ_request WHERE id = ?", this::parseSingle, id);
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<JoinRequest> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM join_civ_request", this::parseCollection);
    }

    /**
     * @param joinRequest object to insert
     * @return
     * @throws DataAccessException
     */
    @Override
    public Integer insert(JoinRequest joinRequest) throws DataAccessException {
        return executeUpdate("INSERT INTO join_civ_request (request_date, requesting_player, civ_id) VALUES (?, ?, ?)",
                joinRequest.requestDate(), joinRequest.requester(), joinRequest.civID());
    }

    /**
     * @param id unique key
     * @throws DataAccessException
     */
    @Override
    public void delete(Integer id) throws DataAccessException {
        executeUpdate("DELETE FROM join_civ_request WHERE id = ?", id);
    }

    /**
     * @param joinRequest unique key and values to update to
     * @throws DataAccessException
     */
    @Override
    public void update(JoinRequest joinRequest) throws DataAccessException {
        executeUpdate("UPDATE join_civ_request SET request_date = ?, requesting_player = ?, civ_id = ? WHERE id = ?",
                joinRequest.requestDate(), joinRequest.requester(), joinRequest.civID(), joinRequest.ID());
    }

    /**
     * @param civID civ ID to get request
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<JoinRequest> getForCiv(int civID) throws DataAccessException {
        return executeQuery("SELECT * FROM join_civ_request WHERE civ_id = ?", this::parseCollection, civID);

    }

    /**
     * @param rs result set to retrieve row data from
     * @return
     * @throws SQLException
     */
    @Override
    protected JoinRequest parse(ResultSet rs) throws SQLException {
        return new JoinRequest(
                rs.getInt("id"),
                rs.getString("request_date"),
                UUID.fromString(rs.getString("requesting_player")),
                rs.getInt("civ_id")
        );
    }
}
