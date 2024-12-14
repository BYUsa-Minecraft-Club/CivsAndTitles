package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.BuildDAO;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Build;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class SqliteBuildDAO extends SqliteDAO<Build> implements BuildDAO {

    /**
     * Constructs a new sqlite build DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqliteBuildDAO() throws DataAccessException {
    }

    /**
     * @param integer Unique key value to match against
     * @return
     * @throws DataAccessException
     */
    @Override
    public Build get(Integer integer) throws DataAccessException {
        return executeQuery("SELECT * FROM `build` WHERE id = ?", this::parseSingle, integer);
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Build> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM `build`", this::parseCollection);
    }

    /**
     * @param build object to insert
     * @return
     * @throws DataAccessException
     */
    @Override
    public Integer insert(Build build) throws DataAccessException {
        return executeUpdate("INSERT INTO `build` (name, civ_id, submitted_date, comments, size, " +
                        "points, status, location_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                build.name(), build.civID(), build.submittedDate(), build.comments(), build.size(), build.points(),
                build.status(), build.locationID());
    }

    /**
     * @param integer unique key
     * @throws DataAccessException
     */
    @Override
    public void delete(Integer integer) throws DataAccessException {
        executeUpdate("DELETE FROM `build` WHERE id = ?", integer);
    }

    /**
     * @param build unique key and values to update to
     * @throws DataAccessException
     */
    @Override
    public void update(Build build) throws DataAccessException {
        executeUpdate("UPDATE `build` SET name = ?, civ_id = ?, submitted_date = ?, comments = ?, " +
                        "size = ?, points = ?, status = ?, location_id = ? WHERE id = ?",
                build.name(), build.civID(), build.submittedDate(), build.comments(), build.size(), build.points(),
                build.status(), build.locationID(), build.ID());
    }

    /**
     * @param civID civ ID to find build of
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Build> getAllForCiv(int civID) throws DataAccessException {
        return executeQuery("SELECT * FROM `build` WHERE civ_id = ?", this::parseCollection, civID);
    }

    /**
     * @param uuid uuid of player
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Build> getAllForSubmitter(UUID uuid) throws DataAccessException {
        return executeQuery("SELECT * FROM `build` WHERE submitter = ?", this::parseCollection, uuid);
    }

    /**
     * @param uuid uuid of player
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Build> getAllForBuilder(UUID uuid) throws DataAccessException {
        return executeQuery("SELECT * FROM `build` WHERE builders LIKE '%" + uuid + "%'", this::parseCollection);
    }

    /**
     * @param status status to find build matching
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Build> getAllForStatus(Build.JudgeStatus status) throws DataAccessException {
        return executeQuery("SELECT * FROM `build` WHERE status = ?", this::parseCollection, status);
    }

    protected Build parse(ResultSet rs) throws SQLException {
        return new Build(rs.getInt("id"),
                rs.getString("name"),
                rs.getString("submitted_date"),
                rs.getInt("location_id"),
                rs.getInt("civ_id"),
                rs.getString("comments"),
                rs.getInt("points"),
                rs.getInt("size"),
                Build.JudgeStatus.valueOf(rs.getString("status")));
    }
}
