package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.LocationDAO;
import edu.byu.minecraft.cat.model.Civ;
import edu.byu.minecraft.cat.model.CivParticipantPlayer;
import edu.byu.minecraft.cat.model.Location;
import net.minecraft.world.World;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class SqliteCivDAOTest {

    private SqliteCivDAO dao;

    private static List<Civ> civs;
    private static int locID;

    @BeforeAll
    static void init() throws DataAccessException {
        LocationDAO locationDAO = new SqliteLocationDAO();
        for (Location loc : locationDAO.getAll()) {
            locationDAO.delete(loc.id());
        }
        Location loc = new Location(0, 0, 0, 0, World.OVERWORLD.getValue(), 0f, 0f);
        locID = locationDAO.insert(loc);

        civs = new ArrayList<>();
        civs.add(new Civ(0, "Chickens R Cool", 17, true, true, locID, "now"));
        civs.add(new Civ(0, "Abandonded", 4, false, true, locID, "long ago"));
        civs.add(new Civ(0, "Unincorporated", -1, false, false, locID, "early"));
        civs.add(new Civ(0, "asdf", 21, true, true, locID, "asdf"));
        civs.add(new Civ(0, "jkl;", 108, true, true, locID, "jkl;"));
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        dao = new SqliteCivDAO();
        Collection<Civ> civs = dao.getAll();
        for(Civ civ : civs) {
            dao.delete(civ.ID());
        }
    }

    @Test
    void getAndInsert() throws DataAccessException {
        int id = dao.insert(civs.get(0));
        Civ inserted = withId(id, civs.get(0));
        Civ found = dao.get(id);
        Assertions.assertEquals(inserted, found);

        Assertions.assertNull(dao.get(id + 1));
    }

    @Test
    void getAll() throws DataAccessException {
        Set<Civ> inserted = new HashSet<>();
        for(Civ civ : civs) {
            int id = dao.insert(civ);
            inserted.add(withId(id, civ));
        }

        Assertions.assertEquals(inserted, new HashSet<>(dao.getAll()));
    }

    @Test
    void delete() throws DataAccessException {
        int id1 = dao.insert(civs.get(0));
        Civ civ1ins = withId(id1, civs.get(0));
        int id2 = dao.insert(civs.get(1));
        Civ civ2ins = withId(id2, civs.get(1));
        Collection<Civ> foundCivs = dao.getAll();
        Assertions.assertEquals(2, foundCivs.size());
        Assertions.assertTrue(foundCivs.contains(civ1ins));
        Assertions.assertTrue(foundCivs.contains(civ2ins));

        dao.delete(id1);
        foundCivs = dao.getAll();
        Assertions.assertEquals(1, foundCivs.size());
        Assertions.assertFalse(foundCivs.contains(civ1ins));
        Assertions.assertTrue(foundCivs.contains(civ2ins));
    }

    @Test
    void update() throws DataAccessException {
        int id1 = dao.insert(civs.get(0));
        Civ civ1ins = withId(id1, civs.get(0));
        int id2 = dao.insert(civs.get(1));
        Civ civ2ins = withId(id2, civs.get(1));
        Collection<Civ> civs = dao.getAll();
        Assertions.assertEquals(2, civs.size());
        Assertions.assertTrue(civs.contains(civ1ins));
        Assertions.assertTrue(civs.contains(civ2ins));

        Civ updated = new Civ(id2, "Funky Town", 212121, true, true, locID, "3 days ago");

        dao.update(updated);
        civs = dao.getAll();
        Assertions.assertEquals(2, civs.size());
        Assertions.assertTrue(civs.contains(civ1ins));
        Assertions.assertFalse(civs.contains(civ2ins));
        Assertions.assertTrue(civs.contains(updated));
    }

    @Test
    void getForPlayer() throws DataAccessException {
        SqliteCivParticipantDAO participantDAO = new SqliteCivParticipantDAO();
        for(CivParticipantPlayer player : participantDAO.getAll()) {
            participantDAO.delete(player);
        }

        UUID player = UUID.randomUUID();
        Set<Civ> expected = new HashSet<>();
        for (int i = 0; i < civs.size(); i++) {
            Civ civ = civs.get(i);
            int id = dao.insert(civ);
            civ = withId(id, civ);
            if (i != 1) {
                participantDAO.insert(new CivParticipantPlayer(id, player, CivParticipantPlayer.Status.values()[i]));
                expected.add(civ);
            }
        }

        Assertions.assertEquals(expected, dao.getForPlayer(player));
    }

    @Test
    void getForName() throws DataAccessException {
        int id1 = dao.insert(civs.get(0));
        Civ civ1ins = withId(id1, civs.get(0));
        int id2 = dao.insert(civs.get(1));
        Civ civ2ins = withId(id2, civs.get(1));

        Civ found = dao.getForName(civ1ins.name());
        Assertions.assertEquals(civ1ins, found);
    }

    @Test
    void getForActivity() throws DataAccessException {
        List<Civ> inserted = new ArrayList<>();
        for(Civ civ : civs) {
            int id = dao.insert(civ);
            inserted.add(withId(id, civ));
        }

        Collection<Civ> found = dao.getForActivity(false);
        Assertions.assertFalse(found.contains(inserted.get(0)));
        Assertions.assertTrue(found.contains(inserted.get(1)));
        Assertions.assertTrue(found.contains(inserted.get(2)));
        Assertions.assertFalse(found.contains(inserted.get(3)));
        Assertions.assertFalse(found.contains(inserted.get(4)));
    }

    @Test
    void getForIncorporation() throws DataAccessException {
        List<Civ> inserted = new ArrayList<>();
        for(Civ civ : civs) {
            int id = dao.insert(civ);
            inserted.add(withId(id, civ));
        }

        Collection<Civ> found = dao.getForIncorporation(true);
        Assertions.assertTrue(found.contains(inserted.get(0)));
        Assertions.assertTrue(found.contains(inserted.get(1)));
        Assertions.assertFalse(found.contains(inserted.get(2)));
        Assertions.assertTrue(found.contains(inserted.get(3)));
        Assertions.assertTrue(found.contains(inserted.get(4)));
    }

    @Test
    void getForPointsMin() throws DataAccessException {
        List<Civ> inserted = new ArrayList<>();
        for(Civ civ : civs) {
            int id = dao.insert(civ);
            inserted.add(withId(id, civ));
        }

        Collection<Civ> found = dao.getForPoints(21);
        Assertions.assertFalse(found.contains(inserted.get(0)));
        Assertions.assertFalse(found.contains(inserted.get(1)));
        Assertions.assertFalse(found.contains(inserted.get(2)));
        Assertions.assertTrue(found.contains(inserted.get(3)));
        Assertions.assertTrue(found.contains(inserted.get(4)));
    }

    @Test
    void getForPointsMinMax() throws DataAccessException {
        List<Civ> inserted = new ArrayList<>();
        for(Civ civ : civs) {
            int id = dao.insert(civ);
            inserted.add(withId(id, civ));
        }

        Collection<Civ> found = dao.getForPoints(16, 21);
        Assertions.assertTrue(found.contains(inserted.get(0)));
        Assertions.assertFalse(found.contains(inserted.get(1)));
        Assertions.assertFalse(found.contains(inserted.get(2)));
        Assertions.assertTrue(found.contains(inserted.get(3)));
        Assertions.assertFalse(found.contains(inserted.get(4)));
    }


    private Civ withId(int id, Civ civ) {
        return new Civ(id, civ.name(), civ.numPoints(), civ.isActive(), civ.incorporated(), civ.locationID(), civ.createdDate());
    }
}