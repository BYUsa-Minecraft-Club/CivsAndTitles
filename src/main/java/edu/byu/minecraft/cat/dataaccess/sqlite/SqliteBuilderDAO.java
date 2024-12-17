package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.BuilderDAO;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Builder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class SqliteBuilderDAO extends SqliteDAO<Builder> implements BuilderDAO {
    /**
     * Constructs a new sqlite DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqliteBuilderDAO() throws DataAccessException {
    }

    /**
     * @param builder
     * @throws DataAccessException
     */
    @Override
    public void insert(Builder builder) throws DataAccessException {
        executeUpdate("INSERT INTO `builder` (build_id, player_uuid) VALUES (?, ?)",
                builder.buildID(), builder.playerUUID());
    }

    /**
     * @param builder
     * @throws DataAccessException
     */
    @Override
    public void delete(Builder builder) throws DataAccessException {
        executeUpdate("DELETE FROM `builder` WHERE build_id = ? AND player_uuid = ?",
                builder.buildID(), builder.playerUUID());
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Builder> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM `builder`", this::parseCollection);
    }

    /**
     * @param uuid
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Builder> getAllForPlayer(UUID uuid) throws DataAccessException {
        return executeQuery("SELECT * FROM `builder` WHERE player_uuid = ?", this::parseCollection, uuid);
    }

    /**
     * @param buildID
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Builder> getAllForBuild(int buildID) throws DataAccessException {
        return executeQuery("SELECT * FROM `builder` WHERE build_id = ?", this::parseCollection, buildID);
    }

    /**
     * @param rs result set to retrieve row data from
     * @return
     * @throws SQLException
     */
    @Override
    protected Builder parse(ResultSet rs) throws SQLException {
        return new Builder(rs.getInt("build_id"), UUID.fromString(rs.getString("player_uuid")));
    }
}
