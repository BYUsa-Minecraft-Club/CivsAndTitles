package edu.byu.minecraft.cat.dataaccess.none;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.UnlockedTitleDAO;
import edu.byu.minecraft.cat.model.UnlockedTitle;

import java.util.*;

public class NoneUnlockedTitleDAO implements UnlockedTitleDAO {
    HashMap<UUID, HashMap<String, UnlockedTitle>> titles = new HashMap<>();

    @Override
    public Collection<UnlockedTitle> getAll(UUID uuid) throws DataAccessException {
        if (titles.containsKey(uuid)) return titles.get(uuid).values();
        else return List.of();
    }

    @Override
    public Collection<UnlockedTitle> getAll(String title) throws DataAccessException {
        return titles.values().stream().map(map -> map.get(title)).filter(Objects::nonNull).toList();
    }

    @Override
    public void insert(UnlockedTitle title) throws DataAccessException {
        if (!titles.containsKey(title.uuid())) titles.put(title.uuid(), new HashMap<>());
        titles.get(title.uuid()).put(title.title(), title);
    }

    @Override
    public void delete(UUID uuid, String title) throws DataAccessException {
        if (titles.containsKey(uuid)) {
            titles.get(uuid).remove(title);
        }
    }

    @Override
    public void deleteAll(String title) throws DataAccessException {
        titles.values().forEach(map -> map.remove(title));
    }
}
