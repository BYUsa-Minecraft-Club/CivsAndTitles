package edu.byu.minecraft.cat.dataaccess.postgres;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.UnlockedTitleDAO;
import edu.byu.minecraft.cat.dataaccess.sqlite.SqliteDAO;
import edu.byu.minecraft.cat.model.UnlockedTitle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class PostgresUnlockedTitleDAO extends PostgresDAO<UnlockedTitle> implements UnlockedTitleDAO {

    /**
     * Constructs a new postgresql DAO
     *
     * @param database access to the postgres database
     */
    protected PostgresUnlockedTitleDAO(PostgresDataAccess database) {
        super(database);
    }

    @Override
    public Collection<UnlockedTitle> getAll(UUID uuid) throws DataAccessException {
        return database.executeQuery("SELECT * FROM player_title WHERE player_uuid = ?", this::parseCollection, uuid);
    }

    @Override
    public Collection<UnlockedTitle> getAll(String title) throws DataAccessException {
        return database.executeQuery("SELECT * FROM player_title WHERE title = ?", this::parseCollection, title);
    }

    @Override
    public void insert(UnlockedTitle title) throws DataAccessException {
        database.executeUpdate("INSERT INTO player_title (player_uuid, title, date_earned) VALUES (?, ?, ?) ON CONFLICT DO NOTHING",
                title.uuid(), title.title(), title.earned());
    }

    @Override
    public void delete(UUID uuid, String title) throws DataAccessException {
        database.executeUpdate("DELETE FROM player_title WHERE player_uuid = ? AND title = ?", uuid, title);
    }

    @Override
    public void deleteAll(String title) throws DataAccessException {
        database.executeUpdate("DELETE FROM player_title WHERE title = ?", title);

    }

    @Override
    protected UnlockedTitle parse(ResultSet rs) throws SQLException {
        return new UnlockedTitle(
                UUID.fromString(rs.getString("player_uuid")),
                rs.getString("title"),
                rs.getString("date_earned")
        );
    }

    @Override
    public UnlockedTitle get(UUID uuid, String title) throws DataAccessException {
        return database.executeQuery("SELECT * FROM player_title WHERE player_uuid = ? AND title = ?", this::parseSingle, uuid, title);
    }
}
