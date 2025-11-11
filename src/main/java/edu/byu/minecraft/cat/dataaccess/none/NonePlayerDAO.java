package edu.byu.minecraft.cat.dataaccess.none;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.PlayerDAO;
import edu.byu.minecraft.cat.model.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class NonePlayerDAO implements PlayerDAO {
    HashMap<UUID, Player> players = new HashMap<>();

    @Override
    public UUID getPlayerUUID(String username) throws DataAccessException {
        for (Player player : players.values()) {
            if (player.name().equals(username)) {
                return player.uuid();
            }
        }
        return null;
    }

    @Override
    public Player get(UUID uuid) throws DataAccessException {
        return players.get(uuid);
    }

    @Override
    public Collection<Player> getAll() throws DataAccessException {
        return players.values();
    }

    @Override
    public UUID insert(Player player) throws DataAccessException {
        players.put(player.uuid(), player);
        return player.uuid();
    }

    @Override
    public void delete(UUID uuid) throws DataAccessException {
        players.remove(uuid);
    }

    @Override
    public void update(Player player) throws DataAccessException {
        players.put(player.uuid(), player);
    }
}
