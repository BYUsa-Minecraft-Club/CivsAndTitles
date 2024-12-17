package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.CivParticipantDAO;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.CivParticipantPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class SqliteCivParticipantDAO extends SqliteDAO<CivParticipantPlayer> implements CivParticipantDAO {
    /**
     * Constructs a new sqlite DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqliteCivParticipantDAO() throws DataAccessException {
    }

    /**
     * @param player
     * @throws DataAccessException
     */
    @Override
    public void insert(CivParticipantPlayer player) throws DataAccessException {
        executeUpdate("INSERT INTO civ_player (civ_id, player_uuid, status) VALUES (?, ?, ?)",
                player.civID(), player.playerUUID(), player.status());
    }

    /**
     * @param player
     * @throws DataAccessException
     */
    @Override
    public void delete(CivParticipantPlayer player) throws DataAccessException {
        executeUpdate("DELETE FROM civ_player WHERE civ_id = ? AND player_uuid = ? AND status = ?",
                player.civID(), player.playerUUID(), player.status());
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<CivParticipantPlayer> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM civ_player", this::parseCollection);
    }

    /**
     * @param uuid
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<CivParticipantPlayer> getAllForPlayer(UUID uuid) throws DataAccessException {
        return executeQuery("SELECT * FROM civ_player WHERE player_uuid = ?", this::parseCollection, uuid);
    }

    /**
     * @param uuid
     * @param status
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<CivParticipantPlayer> getAllForPlayerStatus(UUID uuid, CivParticipantPlayer.Status status)
            throws DataAccessException {
        return executeQuery("SELECT * FROM civ_player WHERE player_uuid = ? AND status = ?",
                this::parseCollection, uuid, status);

    }

    /**
     * @param civID
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<CivParticipantPlayer> getAllForCiv(int civID) throws DataAccessException {
        return executeQuery("SELECT * FROM civ_player WHERE civ_id = ?", this::parseCollection, civID);
    }

    /**
     * @param civID
     * @param status
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<CivParticipantPlayer> getAllForCivStatus(int civID, CivParticipantPlayer.Status status)
            throws DataAccessException {
        return executeQuery("SELECT * FROM civ_player WHERE civ_id = ? AND status = ?",
                this::parseCollection, civID, status);
    }

    /**
     * @param rs result set to retrieve row data from
     * @return
     * @throws SQLException
     */
    @Override
    protected CivParticipantPlayer parse(ResultSet rs) throws SQLException {
        return new CivParticipantPlayer(rs.getInt("civ_id"),
                UUID.fromString(rs.getString("player_uuid")),
                CivParticipantPlayer.Status.valueOf(rs.getString("status")));
    }
}
