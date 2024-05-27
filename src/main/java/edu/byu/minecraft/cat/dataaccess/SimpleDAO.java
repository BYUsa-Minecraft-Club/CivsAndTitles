package edu.byu.minecraft.cat.dataaccess;

import java.util.Collection;

/**
 * Provides methods most DAO's will need
 *
 * @param <T> Model object type
 * @param <U> Type of table unique key
 */
public interface SimpleDAO<T, U> {

    /**
     * Gets a single object
     *
     * @param u Unique key value to match against
     * @return a single model object or null if it cannot be found
     * @throws DataAccessException If database cannot be accessed
     */
    T get(U u) throws DataAccessException;


    /**
     * Finds all applicable objects
     *
     * @return a collection of all applicable objects
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<T> getAll() throws DataAccessException;


    /**
     * Inserts an object into the database
     *
     * @param t object to insert
     * @return unique key, which may be created by the implementing method
     * @throws DataAccessException If database cannot be accessed
     */
    U insert(T t) throws DataAccessException;


    /**
     * Deletes one item from the database
     *
     * @param u unique key
     * @throws DataAccessException If database cannot be accessed
     */
    void delete(U u) throws DataAccessException;


    /**
     * Updates one item in the database with matching unique key
     *
     * @param t unique key and values to update to
     * @throws DataAccessException If database cannot be accessed
     */
    void update(T t) throws DataAccessException;

}
