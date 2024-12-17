package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.LocationDAO;
import edu.byu.minecraft.cat.model.Build;
import edu.byu.minecraft.cat.model.Builder;
import edu.byu.minecraft.cat.model.Location;
import edu.byu.minecraft.cat.model.Player;
import net.minecraft.world.World;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

class SqliteBuildDAOTest {

    private SqliteBuildDAO dao;

    private int locID;

    private UUID campusSubmitter = UUID.randomUUID();

    private static UUID mtcSubmitter = UUID.randomUUID();

    private Build library;

    private Build marb;

    private Build mtc;


    @BeforeEach
    void setUp() throws DataAccessException {
        dao = new SqliteBuildDAO();
        LocationDAO locationDAO = new SqliteLocationDAO();
        for (Location loc : locationDAO.getAll()) {
            locationDAO.delete(loc.id());
        }

        Location loc = new Location(0, 0, 0, 0, World.OVERWORLD.getValue(), 0f, 0f);
        locID = locationDAO.insert(loc);
        library = new Build(0, "HBLL", "now", locID, 7, campusSubmitter,
                "Super LONG", 166377, 165, Build.JudgeStatus.JUDGED);

        marb = new Build(0, "MARB", "2 seconds ago", locID, library.civID(), campusSubmitter,
                "Super SQUARE", -1, -1, Build.JudgeStatus.ACTIVE);

        mtc = new Build(0, "MTC", "4 minutes ago", locID, library.civID() + 5, mtcSubmitter,
                "Can't verify lookalike", 166376, 135, Build.JudgeStatus.JUDGED);

        Collection<Build> builds = dao.getAll();
        for (Build build : builds) {
            dao.delete(build.ID());
        }
    }

    private Build withId(int id, Build build) {
        return new Build(id, build.name(), build.submittedDate(), build.locationID(), build.civID(), build.submitter(),
                build.comments(), build.points(), build.size(), build.status());
    }

    @Test
    void getAndInsert() throws DataAccessException {
        int id = dao.insert(library);
        Build inserted = withId(id, library);
        Build found = dao.get(id);
        Assertions.assertEquals(inserted, found);

        Assertions.assertNull(dao.get(id + 1));
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

        Build updated = new Build(marbId, "Not MARB", "later", locID, 8, UUID.randomUUID(),
                "IDK", 17, 2, Build.JudgeStatus.PENDING);

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

        SqliteBuilderDAO builderDAO = new SqliteBuilderDAO();
        for(Builder builder : builderDAO.getAll()) {
            builderDAO.delete(builder);
        }
        builderDAO.insert(new Builder(libraryId, campusSubmitter));
        builderDAO.insert(new Builder(marbId, campusSubmitter));
        builderDAO.insert(new Builder(mtcId, campusSubmitter));
        builderDAO.insert(new Builder(mtcId, mtcSubmitter));

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