package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.PlayerDAO;
import edu.byu.minecraft.cat.model.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class SqlitePlayerDAO extends SqliteDAO<Player> implements PlayerDAO {
    /**
     * Constructs a new sqlite DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqlitePlayerDAO() throws DataAccessException {
    }

    /**
     * @param uuid Unique key value to match against
     * @return
     * @throws DataAccessException
     */
    @Override
    public Player get(UUID uuid) throws DataAccessException {
        return executeQuery("SELECT * FROM player WHERE uuid = ?", this::parseSingle, uuid);
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Player> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM player", this::parseCollection);
    }

    /**
     * @param player object to insert
     * @return
     * @throws DataAccessException
     */
    @Override
    public UUID insert(Player player) throws DataAccessException {
        executeUpdate("INSERT INTO player (uuid, username, points, current_title, role, show_rank) " +
                "VALUES (?, ?, ?, ?, ?, ?)", player.uuid(), player.name(), player.points(), player.title(),
                player.role(), player.showRank());
        return player.uuid();
    }

    /**
     * @param uuid unique key
     * @throws DataAccessException
     */
    @Override
    public void delete(UUID uuid) throws DataAccessException {
        executeUpdate("DELETE FROM player WHERE uuid = ?", uuid);
    }

    /**
     * @param player unique key and values to update to
     * @throws DataAccessException
     */
    @Override
    public void update(Player player) throws DataAccessException {
        executeUpdate("UPDATE player SET username = ?, points = ?, current_title = ?, " +
                "role = ?, show_rank = ? WHERE uuid = ?", player.name(), player.points(),
                player.title(), player.role(), player.showRank(), player.uuid());
    }

    /**
     * @param role role to find players
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Player> getForRole(Player.Role role) throws DataAccessException {
        return executeQuery("SELECT * FROM player WHERE role = ?", this::parseCollection, role);
    }

    /**
     * @param minPoints minimum number of points
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Player> getForPoints(int minPoints) throws DataAccessException {
        return executeQuery("SELECT * FROM player WHERE points >= ?", this::parseCollection, minPoints);
    }

    /**
     * @param minPoints lower bound of point range
     * @param maxPoints upper bound of point range
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Player> getForPoints(int minPoints, int maxPoints) throws DataAccessException {
        return executeQuery("SELECT * FROM player WHERE points >= ? AND points <= ?",
                this::parseCollection, minPoints, maxPoints);
    }

    /**
     * @param rs result set to retrieve row data from
     * @return
     * @throws SQLException
     */
    @Override
    protected Player parse(ResultSet rs) throws SQLException {
        return new Player(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("username"),
                rs.getInt("points"),
                rs.getString("current_title"),
                Player.Role.valueOf(rs.getString("role")),
                rs.getBoolean("show_rank")
        );
    }
}
