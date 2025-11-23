package edu.byu.minecraft.cat.dataaccess.postgres;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.PlayerDAO;
import edu.byu.minecraft.cat.model.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class PostgresPlayerDAO extends PostgresDAO<Player> implements PlayerDAO {

    /**
     * Constructs a new postgresql DAO
     *
     * @param database Access to the postgresql database
     */
    protected PostgresPlayerDAO(PostgresDataAccess database) {
        super(database);
    }

    @Override
    public Player get(UUID uuid) throws DataAccessException {
        return database.executeQuery("SELECT * FROM player WHERE uuid = ?", this::parseSingle, uuid);
    }

    @Override
    public Collection<Player> getAll() throws DataAccessException {
        return database.executeQuery("SELECT * FROM player", this::parseCollection);
    }

    @Override
    public UUID insert(Player player) throws DataAccessException {
        database.executeUpdate("INSERT INTO player (uuid, username, current_title) " +
                "VALUES (?, ?, ?)", player.uuid(), player.name(), player.title());
        return player.uuid();
    }

    @Override
    public void delete(UUID uuid) throws DataAccessException {
        database.executeUpdate("DELETE FROM player WHERE uuid = ?", uuid);
    }

    @Override
    public void update(Player player) throws DataAccessException {
        database.executeUpdate("UPDATE player SET username = ?, current_title = ? WHERE uuid = ?",
                player.name(), player.title(), player.uuid());
    }

    @Override
    public UUID getPlayerUUID(String username) throws DataAccessException {
        Player player = database.executeQuery("SELECT * FROM player WHERE username = ?", this::parseSingle, username);
        return player.uuid();
    }

    @Override
    protected Player parse(ResultSet rs) throws SQLException {
        return new Player(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("username"),
                rs.getString("current_title")
        );
    }

    @Override
    public void removeAllTitles(String title) throws DataAccessException {
        database.executeUpdate("UPDATE player SET current_title = null WHERE current_title = ?", title);
    }
}
