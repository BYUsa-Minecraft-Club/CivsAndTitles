package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.BuildScore;

import java.util.Collection;
import java.util.UUID;

/**
 * Data Access interface for BuildScores
 */
public interface BuildScoreDAO extends SimpleDAO<BuildScore, Integer> {

    /**
     * Gets all scores for a build
     *
     * @param buildID build ID to find build scores for
     * @return all scores for the build
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<BuildScore> getForBuild(int buildID) throws DataAccessException;


    /**
     * Gets all scores for a judge
     *
     * @param uuid uuid of judge
     * @return all scores judged by judge
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<BuildScore> getForJudge(UUID uuid) throws DataAccessException;
}
