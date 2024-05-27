package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.BuildScore;

import java.util.Collection;
import java.util.UUID;

public interface BuildScoreDAO extends SimpleDAO<BuildScore, Integer> {
    Collection<BuildScore> getForBuild(int buildID) throws DataAccessException;
    Collection<BuildScore> getForJudge(UUID uuid) throws DataAccessException;
}
