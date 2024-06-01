package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Build;
import edu.byu.minecraft.cat.model.Location;
import net.minecraft.world.World;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

class SqliteBuildDAOTest {

    private SqliteBuildDAO dao;

    private UUID campusSubmitter = UUID.randomUUID();

    private Location libraryLocation = new Location(-538, 212, 1521, World.OVERWORLD.getValue(), 0f, 0f);

    private Build library = new Build(0, "HBLL", System.currentTimeMillis(), campusSubmitter, libraryLocation,
            7, Set.of(campusSubmitter), "Super LONG", 165, 166377, Build.JudgeStatus.JUDGED);

    private Location marbLocation = new Location(-536, 207, 1879, World.OVERWORLD.getValue(), 0f, 0f);

    private Build marb = new Build(0, "MARB", System.currentTimeMillis() + 2, campusSubmitter, marbLocation,
            library.civID(), Set.of(campusSubmitter), "Super SQUARE", -1, -1, Build.JudgeStatus.ACTIVE);

    private UUID mtcSubmitter = UUID.randomUUID();

    private Location mtcLocation = new Location(103, 251, 271, World.OVERWORLD.getValue(), -180f, 0f);

    private Build mtc = new Build(0, "MTC", System.currentTimeMillis() + 4, mtcSubmitter, mtcLocation,
            library.civID() + 5, Set.of(mtcSubmitter, campusSubmitter), "Can't verify lookalike",
            135, 166376, Build.JudgeStatus.JUDGED);


    @BeforeEach
    void setUp() throws DataAccessException {
        dao = new SqliteBuildDAO();
        Collection<Build> builds = dao.getAll();
        for(Build build : builds) {
            dao.delete(build.ID());
        }
    }

    private Build withId(int id, Build build) {
        return new Build(id, build.name(), build.timestamp(), build.submitter(), build.location(),
                build.civID(), build.builders(), build.comments(), build.points(), build.size(), build.status());
    }

    @Test
    void getAndInsert() throws DataAccessException {
        int id = dao.insert(library);
        Build inserted = withId(id, library);
        Build found = dao.get(id);
        Assertions.assertEquals(inserted, found);
    }

    @Test
    void getAll() throws DataAccessException {
        Collection<Build> builds = dao.getAll();
        Assertions.assertEquals(0, builds.size());

        int libraryId = dao.insert(library);
        Build libraryInserted = withId(libraryId, library);
        builds = dao.getAll();
        Assertions.assertEquals(1, builds.size());
        Assertions.assertTrue(builds.contains(libraryInserted));

        int marbId = dao.insert(marb);
        Build marbInserted = withId(marbId, marb);
        builds = dao.getAll();
        Assertions.assertEquals(2, builds.size());
        Assertions.assertTrue(builds.contains(libraryInserted));
        Assertions.assertTrue(builds.contains(marbInserted));
    }

    @Test
    void delete() throws DataAccessException {
        int libraryId = dao.insert(library);
        Build libraryInserted = withId(libraryId, library);
        int marbId = dao.insert(marb);
        Build marbInserted = withId(marbId, marb);
        Collection<Build> builds = dao.getAll();
        Assertions.assertEquals(2, builds.size());
        Assertions.assertTrue(builds.contains(libraryInserted));
        Assertions.assertTrue(builds.contains(marbInserted));

        dao.delete(libraryId);
        builds = dao.getAll();
        Assertions.assertEquals(1, builds.size());
        Assertions.assertFalse(builds.contains(libraryInserted));
        Assertions.assertTrue(builds.contains(marbInserted));
    }

    @Test
    void update() throws DataAccessException {
        int libraryId = dao.insert(library);
        Build libraryInserted = withId(libraryId, library);
        int marbId = dao.insert(marb);
        Build marbInserted = withId(marbId, marb);
        Collection<Build> builds = dao.getAll();
        Assertions.assertEquals(2, builds.size());
        Assertions.assertTrue(builds.contains(libraryInserted));
        Assertions.assertTrue(builds.contains(marbInserted));

        Build updated = new Build(marbId, "Not MARB", System.currentTimeMillis(), UUID.randomUUID(), marbLocation,
                8, Set.of(campusSubmitter), "IDK", 17, 2, Build.JudgeStatus.PENDING);

        dao.update(updated);
        builds = dao.getAll();
        Assertions.assertEquals(2, builds.size());
        Assertions.assertTrue(builds.contains(libraryInserted));
        Assertions.assertFalse(builds.contains(marbInserted));
        Assertions.assertTrue(builds.contains(updated));
    }

    @Test
    void getAllForCiv() throws DataAccessException {
        int libraryId = dao.insert(library);
        Build libraryInserted = withId(libraryId, library);
        int marbId = dao.insert(marb);
        Build marbInserted = withId(marbId, marb);
        int mtcId = dao.insert(mtc);
        Build mtcInserted = withId(mtcId, mtc);

        Collection<Build> builds = dao.getAllForCiv(library.civID());
        Assertions.assertEquals(2, builds.size());
        Assertions.assertTrue(builds.contains(libraryInserted));
        Assertions.assertTrue(builds.contains(marbInserted));
        Assertions.assertFalse(builds.contains(mtcInserted));
    }

    @Test
    void getAllForSubmitter() throws DataAccessException {
        int libraryId = dao.insert(library);
        Build libraryInserted = withId(libraryId, library);
        int marbId = dao.insert(marb);
        Build marbInserted = withId(marbId, marb);
        int mtcId = dao.insert(mtc);
        Build mtcInserted = withId(mtcId, mtc);

        Collection<Build> builds = dao.getAllForSubmitter(campusSubmitter);
        Assertions.assertEquals(2, builds.size());
        Assertions.assertTrue(builds.contains(libraryInserted));
        Assertions.assertTrue(builds.contains(marbInserted));
        Assertions.assertFalse(builds.contains(mtcInserted));
    }

    @Test
    void getAllForBuilder() throws DataAccessException {
        int libraryId = dao.insert(library);
        Build libraryInserted = withId(libraryId, library);
        int marbId = dao.insert(marb);
        Build marbInserted = withId(marbId, marb);
        int mtcId = dao.insert(mtc);
        Build mtcInserted = withId(mtcId, mtc);

        Collection<Build> builds = dao.getAllForBuilder(campusSubmitter);
        Assertions.assertEquals(3, builds.size());
        Assertions.assertTrue(builds.contains(libraryInserted));
        Assertions.assertTrue(builds.contains(marbInserted));
        Assertions.assertTrue(builds.contains(mtcInserted));

        builds = dao.getAllForBuilder(mtcSubmitter);
        Assertions.assertEquals(1, builds.size());
        Assertions.assertFalse(builds.contains(libraryInserted));
        Assertions.assertFalse(builds.contains(marbInserted));
        Assertions.assertTrue(builds.contains(mtcInserted));
    }

    @Test
    void getAllForStatus() throws DataAccessException {
        int libraryId = dao.insert(library);
        Build libraryInserted = withId(libraryId, library);
        int marbId = dao.insert(marb);
        Build marbInserted = withId(marbId, marb);
        int mtcId = dao.insert(mtc);
        Build mtcInserted = withId(mtcId, mtc);

        Collection<Build> builds = dao.getAllForStatus(Build.JudgeStatus.JUDGED);
        Assertions.assertEquals(2, builds.size());
        Assertions.assertTrue(builds.contains(libraryInserted));
        Assertions.assertFalse(builds.contains(marbInserted));
        Assertions.assertTrue(builds.contains(mtcInserted));

        builds = dao.getAllForStatus(Build.JudgeStatus.ACTIVE);
        Assertions.assertEquals(1, builds.size());
        Assertions.assertFalse(builds.contains(libraryInserted));
        Assertions.assertTrue(builds.contains(marbInserted));
        Assertions.assertFalse(builds.contains(mtcInserted));
    }
}