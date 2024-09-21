package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.dataaccess.CivRequestDAO;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.CivRequest;
import edu.byu.minecraft.cat.model.Location;
import net.minecraft.world.World;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.UUID;

class SqliteCivRequestDAOTest {

    CivRequestDAO dao;

    CivRequest r1 = new CivRequest(5, System.currentTimeMillis() - 1000, UUID.randomUUID(), "Aloha",
            new Location(782, 63, -1495, World.OVERWORLD.getValue(), 0, 0));

    CivRequest r2 = new CivRequest(17, System.currentTimeMillis() - 321, r1.submitter(), "Slotted Aloha",
            new Location(-287, 251, 3281, World.NETHER.getValue(), -10.23f, 20));

    CivRequest r3 = new CivRequest(78, System.currentTimeMillis(), UUID.randomUUID(), "CSMA/CD",
            new Location(0, -2, 0, World.END.getValue(), 89, -89.5f));

    @BeforeEach
    void setUp() throws DataAccessException {
        dao = new SqliteCivRequestDAO();
        Collection<CivRequest> requests = dao.getAll();
        for(CivRequest request : requests) {
            dao.delete(request.ID());
        }
    }

    @Test
    void getAndInsert() throws DataAccessException {
        int id = dao.insert(r2);
        CivRequest inserted = withId(id, r2);
        CivRequest found = dao.get(id);
        Assertions.assertEquals(inserted, found);

        Assertions.assertNull(dao.get(id + 1));
    }

    @Test
    void getAll() throws DataAccessException {
        Collection<CivRequest> requests = dao.getAll();
        Assertions.assertEquals(0, requests.size());

        int r1id = dao.insert(r1);
        CivRequest r1inserted = withId(r1id, r1);
        requests = dao.getAll();
        Assertions.assertEquals(1, requests.size());
        Assertions.assertTrue(requests.contains(r1inserted));

        int r2id = dao.insert(r2);
        CivRequest r2inserted = withId(r2id, r2);
        requests = dao.getAll();
        Assertions.assertEquals(2, requests.size());
        Assertions.assertTrue(requests.contains(r1inserted));
        Assertions.assertTrue(requests.contains(r2inserted));
        
    }

    @Test
    void delete() throws DataAccessException {
        int r1id = dao.insert(r1);
        CivRequest r1inserted = withId(r1id, r1);
        int r2id = dao.insert(r2);
        CivRequest r2inserted = withId(r2id, r2);
        Collection<CivRequest> CivRequests = dao.getAll();
        Assertions.assertEquals(2, CivRequests.size());
        Assertions.assertTrue(CivRequests.contains(r1inserted));
        Assertions.assertTrue(CivRequests.contains(r2inserted));

        dao.delete(r1id);
        CivRequests = dao.getAll();
        Assertions.assertEquals(1, CivRequests.size());
        Assertions.assertFalse(CivRequests.contains(r1inserted));
        Assertions.assertTrue(CivRequests.contains(r2inserted));
    }

    @Test
    void update() throws DataAccessException {
        int r1Id = dao.insert(r1);
        CivRequest r1Inserted = withId(r1Id, r1);
        int r2Id = dao.insert(r2);
        CivRequest r2Inserted = withId(r2Id, r2);
        Collection<CivRequest> requests = dao.getAll();
        Assertions.assertEquals(2, requests.size());
        Assertions.assertTrue(requests.contains(r1Inserted));
        Assertions.assertTrue(requests.contains(r2Inserted));

        CivRequest updated = new CivRequest(r2Id, System.currentTimeMillis() + 10, UUID.randomUUID(),
                "Chicken Colony", r3.location());

        dao.update(updated);
        requests = dao.getAll();
        Assertions.assertEquals(2, requests.size());
        Assertions.assertTrue(requests.contains(r1Inserted));
        Assertions.assertFalse(requests.contains(r2Inserted));
        Assertions.assertTrue(requests.contains(updated));
    }

    @Test
    void getForPlayer() throws DataAccessException {
        int r1Id = dao.insert(r1);
        CivRequest r1Inserted = withId(r1Id, r1);
        int r2Id = dao.insert(r2);
        CivRequest r2Inserted = withId(r2Id, r2);
        int r3Id = dao.insert(r3);
        CivRequest r3Inserted = withId(r3Id, r3);

        Collection<CivRequest> requests = dao.getForPlayer(r1.submitter());
        Assertions.assertEquals(2, requests.size());
        Assertions.assertTrue(requests.contains(r1Inserted));
        Assertions.assertTrue(requests.contains(r2Inserted));
        Assertions.assertFalse(requests.contains(r3Inserted));
    }

    private CivRequest withId(int id, CivRequest request) {
        return new CivRequest(id, request.timestamp(), request.submitter(), request.name(), request.location());
    }
}