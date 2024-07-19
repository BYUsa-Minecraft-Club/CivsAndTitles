package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Civ;
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

    private static List<UUID> players;

    private static List<Civ> civs;

    @BeforeAll
    static void init() {
        players = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            players.add(UUID.randomUUID());
        }
        civs = new ArrayList<>();
        civs.add(new Civ(0, "Chickens R Cool", 17, true, true, players.get(0), players.get(0),
                Set.of(players.get(1)), new HashSet<>(players), new HashSet<>(players),
                new Location(213, 70, -2291, World.OVERWORLD.getValue(), 0, 0), "May 3"));
        civs.add(new Civ(0, "Abandonded", 4, true, false, players.get(0), players.get(1),
                Set.of(players.get(2)), Set.of(players.get(3)), Set.of(players.get(4)),
                new Location(-1070, 200, 246, World.NETHER.getValue(), 90, 0), "June 8"));
        civs.add(new Civ(0, "Unincorporated", -1, false, false, players.get(1), players.get(1),
                Set.of(players.get(0)), Set.of(players.get(3)), Set.of(),
                new Location(0, -50, 10000, World.END.getValue(), 0, 25), "July 19"));
        civs.add(new Civ(0, "asdf", 21, true, true, players.get(7), players.get(7),
                Set.of(), Set.of(players.get(0)), Set.of(players.get(2)),
                new Location(19, -28, -3829, World.OVERWORLD.getValue(), 18, -20), "March 1"));
        civs.add(new Civ(0, "jkl;", 108, true, true, players.get(9), players.get(9),
                Set.of(players.get(4)), Set.of(), Set.of(players.get(0)),
                new Location(824, 16, -234, World.OVERWORLD.getValue(), 0, -25), "August 7"));
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

        Civ updated = new Civ(id2, "Funky Town", 212121, true, true,
                civ2ins.founder(), civ2ins.owner(), civ2ins.leaders(), civ2ins.contributors(), civ2ins.members(),
                civ2ins.location(), "3 days ago");

        dao.update(updated);
        civs = dao.getAll();
        Assertions.assertEquals(2, civs.size());
        Assertions.assertTrue(civs.contains(civ1ins));
        Assertions.assertFalse(civs.contains(civ2ins));
        Assertions.assertTrue(civs.contains(updated));
    }

    @Test
    void getForPlayer() throws DataAccessException {
        Set<Civ> inserted = new HashSet<>();
        for(Civ civ : civs) {
            int id = dao.insert(civ);
            inserted.add(withId(id, civ));
        }

        Set<Civ> expected = new HashSet<>(inserted);
        expected.removeIf((civ) -> civ.name().equals(civs.get(1).name()));
        Assertions.assertEquals(expected, dao.getForPlayer(players.get(0)));
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
        return new Civ(id, civ.name(), civ.numPoints(), civ.incorporated(), civ.active(), civ.founder(), civ.owner(),
                civ.leaders(), civ.contributors(), civ.members(), civ.location(), civ.createdDate());
    }
}