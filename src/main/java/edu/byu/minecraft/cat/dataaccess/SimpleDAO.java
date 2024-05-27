package edu.byu.minecraft.cat.dataaccess;

import java.util.Collection;

public interface SimpleDAO<T, U> {
    T get(U u) throws DataAccessException;
    Collection<T> getAll() throws DataAccessException;
    U insert(T t) throws DataAccessException;
    void delete(U u) throws DataAccessException;
    void update(T t) throws DataAccessException;
}
