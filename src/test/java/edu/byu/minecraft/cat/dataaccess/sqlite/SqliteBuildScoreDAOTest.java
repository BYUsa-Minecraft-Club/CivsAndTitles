package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Build;
import edu.byu.minecraft.cat.model.BuildScore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SqliteBuildScoreDAOTest {

    private SqliteBuildScoreDAO dao;

    private UUID judge1 = UUID.randomUUID();

    private UUID judge2 = UUID.randomUUID();

    private BuildScore score1 = new BuildScore(0, 5, judge1, System.currentTimeMillis() * 3 / 4,1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 45, "Comments");

    private BuildScore score2 = new BuildScore(0, 8, judge1, System.currentTimeMillis() * 8 / 9, 3, 6, 9, 2, 5, 8, 1, 4, 7, 0, 35, "Meh");

    private BuildScore score3 = new BuildScore(0, score1.buildID(), judge2, System.currentTimeMillis(), 9, 7, 5, 3, 1, 8, 6, 4, 2, 0, 1234, "");


    @BeforeEach
    void setUp() throws DataAccessException {
        dao = new SqliteBuildScoreDAO();
        Collection<BuildScore> scores = dao.getAll();
        for(BuildScore score : scores) {
            dao.delete(score.ID());
        }
    }

    @Test
    void getAndInsert() throws DataAccessException {
        int id = dao.insert(score1);
        BuildScore inserted = withId(id, score1);
        BuildScore found = dao.get(id);
        Assertions.assertEquals(inserted, found);

        Assertions.assertNull(dao.get(id + 1));
    }

    @Test
    void getAll() throws DataAccessException {
        Collection<BuildScore> scores = dao.getAll();
        Assertions.assertEquals(0, scores.size());

        int id1 = dao.insert(score1);
        BuildScore score1inserted = withId(id1, score1);
        scores = dao.getAll();
        Assertions.assertEquals(1, scores.size());
        Assertions.assertTrue(scores.contains(score1inserted));

        int id2 = dao.insert(score2);
        BuildScore score2inserted = withId(id2, score2);
        scores = dao.getAll();
        Assertions.assertEquals(2, scores.size());
        Assertions.assertTrue(scores.contains(score1inserted));
        Assertions.assertTrue(scores.contains(score2inserted));
        Assertions.assertFalse(scores.contains(score3));
    }

    @Test
    void delete() throws DataAccessException {
        int id1 = dao.insert(score1);
        BuildScore score1ins = withId(id1, score1);
        int id2 = dao.insert(score2);
        BuildScore score2ins = withId(id2, score2);
        Collection<BuildScore> scores = dao.getAll();
        Assertions.assertEquals(2, scores.size());
        Assertions.assertTrue(scores.contains(score1ins));
        Assertions.assertTrue(scores.contains(score2ins));

        dao.delete(id1);
        scores = dao.getAll();
        Assertions.assertEquals(1, scores.size());
        Assertions.assertFalse(scores.contains(score1ins));
        Assertions.assertTrue(scores.contains(score2ins));
    }

    @Test
    void update() throws DataAccessException {
        int id1 = dao.insert(score1);
        BuildScore score1ins = withId(id1, score1);
        int id2 = dao.insert(score2);
        BuildScore score2ins = withId(id2, score2);
        Collection<BuildScore> scores = dao.getAll();
        Assertions.assertEquals(2, scores.size());
        Assertions.assertTrue(scores.contains(score1ins));
        Assertions.assertTrue(scores.contains(score2ins));

        BuildScore updated = new BuildScore(id2, 19, judge2, System.currentTimeMillis() * 5 / 6,0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, "Really Bad");

        dao.update(updated);
        scores = dao.getAll();
        Assertions.assertEquals(2, scores.size());
        Assertions.assertTrue(scores.contains(score1ins));
        Assertions.assertFalse(scores.contains(score2ins));
        Assertions.assertTrue(scores.contains(updated));
    }

    @Test
    void getForBuild() throws DataAccessException {
        int id1 = dao.insert(score1);
        BuildScore score1ins = withId(id1, score1);
        int id2 = dao.insert(score2);
        BuildScore score2ins = withId(id2, score2);
        int id3 = dao.insert(score3);
        BuildScore score3ins = withId(id3, score3);

        Collection<BuildScore> scores = dao.getForBuild(score1.buildID());
        Assertions.assertEquals(2, scores.size());
        Assertions.assertTrue(scores.contains(score1ins));
        Assertions.assertTrue(scores.contains(score3ins));
        Assertions.assertFalse(scores.contains(score2ins));
    }

    @Test
    void getForJudge() throws DataAccessException {
        int id1 = dao.insert(score1);
        BuildScore score1ins = withId(id1, score1);
        int id2 = dao.insert(score2);
        BuildScore score2ins = withId(id2, score2);
        int id3 = dao.insert(score3);
        BuildScore score3ins = withId(id3, score3);

        Collection<BuildScore> scores = dao.getForJudge(score1.judge());
        Assertions.assertEquals(2, scores.size());
        Assertions.assertTrue(scores.contains(score1ins));
        Assertions.assertTrue(scores.contains(score2ins));
        Assertions.assertFalse(scores.contains(score3ins));
    }

    private BuildScore withId(int id, BuildScore score) {
        return new BuildScore(id, score.buildID(), score.judge(), score.timestamp(), score.functionality(),
                score.technical(), score.texture(), score.storytelling(), score.thematic(), score.landscaping(),
                score.detailing(), score.lighting(), score.layout(), score.discretion(), score.total(), score.comments());
    }
}