package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.BuildDAO;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Build;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SqliteBuildDAO extends SqliteDAO implements BuildDAO {

    ResultSetParser<Collection<Build>> collectionParser = new ResultSetParser<Collection<Build>>() {
        @Override
        public Collection<Build> parseResultSet(ResultSet rs) throws SQLException {
            Collection<Build> builds = new HashSet<>();
            while (rs.next()) {
                builds.add(parseBuild(rs));
            }
            return builds;
        }
    };

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
        return executeQuery("SELECT * FROM `BUILDS` WHERE id = ?", (rs) -> {
            if(!rs.next()) return null;
            return parseBuild(rs);
        }, integer);
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Build> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM `BUILDS`", collectionParser);
    }

    private Build parseBuild(ResultSet rs) throws SQLException {
        return new Build(rs.getInt("id"),
                rs.getString("name"),
                rs.getLong("timestamp"),
                UUID.fromString(rs.getString("submitter")),
                parseLocation(rs),
                rs.getInt("civID"),
                stringToSet(rs.getString("builders")),
                rs.getString("comments"),
                rs.getInt("points"),
                rs.getInt("size"),
                Build.JudgeStatus.values()[rs.getInt("status")]
                );
    }

    /**
     * @param build object to insert
     * @return
     * @throws DataAccessException
     */
    @Override
    public Integer insert(Build build) throws DataAccessException {
        return executeUpdate("INSERT INTO `BUILDS` (name, timestamp, submitter, xCoord, yCoord, zCoord, " +
                "dimension, tilt, direction, civID, builders, comments, points, size, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", build.name(), build.timestamp(),
                build.submitter(), build.location().x(), build.location().y(), build.location().z(),
                build.location().world(), build.location().pitch(), build.location().yaw(), build.civID(),
                setToString(build.builders()), build.comments(), build.points(), build.size(), build.status());
    }

    /**
     * @param integer unique key
     * @throws DataAccessException
     */
    @Override
    public void delete(Integer integer) throws DataAccessException {
        executeUpdate("DELETE FROM `BUILDS` WHERE id = ?", integer);
    }

    /**
     * @param build unique key and values to update to
     * @throws DataAccessException
     */
    @Override
    public void update(Build build) throws DataAccessException {
        executeUpdate("UPDATE `BUILDS` SET name = ?, timestamp = ?, submitter = ?, " +
                "xCoord = ?, yCoord = ?, zCoord = ?, dimension = ?, tilt = ?, direction = ?, " +
                "civID = ?, builders = ?, comments = ?, points = ?, size = ?, status  = ? " +
                "WHERE id = ?", build.name(), build.timestamp(), build.submitter(),
                build.location().x(), build.location().y(), build.location().z(), build.location().world(),
                build.location().pitch(), build.location().yaw(), build.civID(), setToString(build.builders()),
                build.comments(), build.points(), build.size(), build.status(), build.ID());
    }

    /**
     * @param civID civ ID to find builds of
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Build> getAllForCiv(int civID) throws DataAccessException {
        return executeQuery("SELECT * FROM `BUILDS` WHERE civID = ?", collectionParser, civID);
    }

    /**
     * @param uuid uuid of player
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Build> getAllForSubmitter(UUID uuid) throws DataAccessException {
        return executeQuery("SELECT * FROM `BUILDS` WHERE submitter = ?", collectionParser, uuid);
    }

    /**
     * @param uuid uuid of player
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Build> getAllForBuilder(UUID uuid) throws DataAccessException {
        return executeQuery("SELECT * FROM `BUILDS` WHERE builders LIKE '%" + uuid + "%'", collectionParser);
    }

    /**
     * @param status status to find builds matching
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Build> getAllForStatus(Build.JudgeStatus status) throws DataAccessException {
        return executeQuery("SELECT * FROM `BUILDS` WHERE status = ?", collectionParser, status);
    }
}
