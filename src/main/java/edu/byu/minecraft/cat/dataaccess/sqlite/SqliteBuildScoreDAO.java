package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.BuildScoreDAO;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Build;
import edu.byu.minecraft.cat.model.BuildScore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class SqliteBuildScoreDAO extends SqliteDAO<BuildScore> implements BuildScoreDAO {

    /**
     * Constructs a new sqlite DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqliteBuildScoreDAO() throws DataAccessException {
    }

    /**
     * @param integer Unique key value to match against
     * @return
     * @throws DataAccessException
     */
    @Override
    public BuildScore get(Integer integer) throws DataAccessException {
        return executeQuery("SELECT * FROM `SCORES` WHERE id = ?", this::parseSingle, integer);
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<BuildScore> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM `SCORES`", this::parseCollection);
    }

    /**
     * @param buildScore object to insert
     * @return
     * @throws DataAccessException
     */
    @Override
    public Integer insert(BuildScore buildScore) throws DataAccessException {
        return executeUpdate("INSERT INTO `SCORES` (buildID, judge, timestamp, functionality, technical, texture, " +
                "storytelling, thematic, terraforming, detailing, lighting, layout, judgeDiscretion, pointTotal, " +
                        "comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                buildScore.buildID(), buildScore.judge(), buildScore.judgedDate(), buildScore.functionality(),
                buildScore.technical(), buildScore.texture(), buildScore.storytelling(), buildScore.thematic(),
                buildScore.landscaping(), buildScore.detailing(), buildScore.lighting(), buildScore.layout(),
                buildScore.discretion(), buildScore.total(), buildScore.comments());
    }

    /**
     * @param integer unique key
     * @throws DataAccessException
     */
    @Override
    public void delete(Integer integer) throws DataAccessException {
        executeUpdate("DELETE FROM `SCORES` WHERE id = ?", integer);
    }

    /**
     * @param buildScore unique key and values to update to
     * @throws DataAccessException
     */
    @Override
    public void update(BuildScore buildScore) throws DataAccessException {
        executeUpdate("UPDATE `SCORES` SET buildID = ?, judge = ?, functionality = ?, timestamp = ?, " +
                        "technical = ?, texture = ?, storytelling = ?, thematic = ?, terraforming = ?, detailing = ?, " +
                        "lighting = ?, layout = ?, judgeDiscretion = ?, pointTotal = ?, comments = ? WHERE id = ?",
                buildScore.buildID(), buildScore.judge(), buildScore.functionality(), buildScore.judgedDate(),
                buildScore.technical(), buildScore.texture(), buildScore.storytelling(), buildScore.thematic(),
                buildScore.landscaping(), buildScore.detailing(), buildScore.lighting(), buildScore.layout(),
                buildScore.discretion(), buildScore.total(), buildScore.comments(), buildScore.ID());
    }

    /**
     * @param buildID build ID to find build scores for
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<BuildScore> getForBuild(int buildID) throws DataAccessException {
        return executeQuery("SELECT * FROM `SCORES` WHERE buildID = ?", this::parseCollection, buildID);
    }

    /**
     * @param uuid uuid of judge
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<BuildScore> getForJudge(UUID uuid) throws DataAccessException {
        return executeQuery("SELECT * FROM `SCORES` WHERE judge = ?", this::parseCollection, uuid);
    }

    protected BuildScore parse(ResultSet rs) throws SQLException {
        return new BuildScore(rs.getInt("id"),
                rs.getInt("buildID"),
                UUID.fromString(rs.getString("judge")),
                rs.getString("timestamp"),
                rs.getInt("functionality"),
                rs.getInt("technical"),
                rs.getInt("texture"),
                rs.getInt("storytelling"),
                rs.getInt("thematic"),
                rs.getInt("terraforming"),
                rs.getInt("detailing"),
                rs.getInt("lighting"),
                rs.getInt("layout"),
                rs.getInt("judgeDiscretion"),
                rs.getInt("pointTotal"),
                rs.getString("comments")
        );
    }
}
