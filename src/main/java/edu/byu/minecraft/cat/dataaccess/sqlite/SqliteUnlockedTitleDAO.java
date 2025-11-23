package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.UnlockedTitleDAO;
import edu.byu.minecraft.cat.model.UnlockedTitle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class SqliteUnlockedTitleDAO extends SqliteDAO<UnlockedTitle> implements UnlockedTitleDAO {
    /**
     * Constructs a new sqlite DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqliteUnlockedTitleDAO() throws DataAccessException {
    }

    @Override
    public Collection<UnlockedTitle> getAll(UUID uuid) throws DataAccessException {
        return executeQuery("SELECT * FROM player_title WHERE player_uuid = ?", this::parseCollection, uuid);
    }

    @Override
    public Collection<UnlockedTitle> getAll(String title) throws DataAccessException {
        return executeQuery("SELECT * FROM player_title WHERE title = ?", this::parseCollection, title);
    }

    @Override
    public void insert(UnlockedTitle title) throws DataAccessException {
        executeUpdate("INSERT OR IGNORE INTO player_title (player_uuid, title, date_earned) VALUES (?, ?, ?)",
                title.uuid(), title.title(), title.earned());
    }

    @Override
    public void delete(UUID uuid, String title) throws DataAccessException {
        executeUpdate("DELETE FROM player_title WHERE player_uuid = ? AND title = ?", uuid, title);
    }

    @Override
    public void deleteAll(String title) throws DataAccessException {
        executeUpdate("DELETE FROM player_title WHERE title = ?", title);

    }

    @Override
    public UnlockedTitle get(UUID uuid, String title) throws DataAccessException {
        return executeQuery("SELECT * FROM player_title WHERE player_uuid = ? AND title = ?", this::parseSingle, uuid, title);
    }

    @Override
    protected UnlockedTitle parse(ResultSet rs) throws SQLException {
        return new UnlockedTitle(
                UUID.fromString(rs.getString("player_uuid")),
                rs.getString("title"),
                rs.getString("date_earned")
        );
    }
}
