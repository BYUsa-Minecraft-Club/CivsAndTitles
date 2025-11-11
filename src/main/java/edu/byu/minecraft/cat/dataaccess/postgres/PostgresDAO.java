package edu.byu.minecraft.cat.dataaccess.postgres;

import java.sql.*;
import java.util.*;

public abstract class PostgresDAO<S> {
    PostgresDataAccess database;

    /**
     * Constructs a new postgresql DAO
     */
    protected PostgresDAO(PostgresDataAccess database) {
        this.database = database;
    }

    /**
     * Parses a single row of a result set into a model object
     *
     * @param rs result set to retrieve row data from
     * @return An appropriate model object
     * @throws SQLException if column names are retrieved incorrectly or are not present
     */
    protected abstract S parse(ResultSet rs) throws SQLException;

    /**
     * Parses an entire result set as a single object. Should qualify as a ResultSetParser
     *
     * @param rs result set to retrieve data from
     * @return An appropriate model object or null if the result set is empty
     * @throws SQLException if thrown from parse
     */
    protected S parseSingle(ResultSet rs) throws SQLException {
        if(!rs.next()) return null;
        return parse(rs);
    }

    /**
     * Parses an entire result set as a collection. Should qualify as a ResultSetParser
     *
     * @param rs result set to retrieve data from
     * @return A collection of model objects. Can be empty if the result set is empty
     * @throws SQLException if thrown from parse
     */
    protected Collection<S> parseCollection(ResultSet rs) throws SQLException {
        Collection<S> coll = new HashSet<>();
        while (rs.next()) {
            coll.add(parse(rs));
        }
        return coll;
    }



}
