package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.Builder;

import java.util.Collection;
import java.util.UUID;

public interface BuilderDAO {
    void insert(Builder builder) throws DataAccessException;

    void delete(Builder builder) throws DataAccessException;

    Collection<Builder> getAll() throws DataAccessException;

    Collection<Builder> getAllForPlayer(UUID uuid) throws DataAccessException;

    Collection<Builder> getAllForBuild(int buildID) throws DataAccessException;
}
