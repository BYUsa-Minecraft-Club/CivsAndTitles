package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.UnlockedTitle;

import java.util.Collection;
import java.util.UUID;

public interface UnlockedTitleDAO {
    Collection<UnlockedTitle> getAll(UUID uuid) throws DataAccessException;
    Collection<UnlockedTitle> getAll(String title) throws DataAccessException;
    int insert(UnlockedTitle title) throws DataAccessException;
    void delete(UUID uuid, String title) throws DataAccessException;
    void deleteAll(String title) throws DataAccessException;
    void update(UnlockedTitle title) throws DataAccessException;
}
