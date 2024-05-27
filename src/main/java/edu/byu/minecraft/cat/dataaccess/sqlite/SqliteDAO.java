package edu.byu.minecraft.cat.dataaccess.sqlite;

import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import net.minecraft.util.Identifier;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SqliteDAO {
    private static final String SEPARATOR = " ";

    private static final File FOLDER = new File(String.format("config/%s", CivsAndTitles.MOD_ID));

    private static final String FILE_LOCATION = FOLDER.getPath() + "/database.db";

    private static boolean databaseCreated = false;


    /**
     * Constructs a new sqlite DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqliteDAO() throws DataAccessException {
        configureDatabase();
    }


    /**
     * Executes a SQL update
     *
     * @param statement SQL statement to execute containing an update statement without output. Contains '?'s equal to
     *                  the length of params
     * @param params    Array of objects as parameters to the sql statement
     * @return Generated sql auto increment value, if applicable. 0 otherwise.
     * @throws DataAccessException if something goes wrong with sql execution
     */
    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        return execute(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                addParams(ps, params);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                return rs.next() ? rs.getInt(1) : 0;
            }
        });
    }


    /**
     * Performs a SQL query.
     *
     * @param statement SQL statement to execute containing a query with output. Contains '?'s equal to the length of
     *                  params
     * @param parser    What to do with the result set when obtained
     * @param params    Array of objects as parameters to the sql statement
     * @param <T>       return type
     * @return the return value of parser
     * @throws DataAccessException if something goes wrong with sql execution
     */
    protected <T> T executeQuery(String statement, ResultSetParser<T> parser, Object... params)
            throws DataAccessException {
        return execute(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                addParams(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    return parser.parseResultSet(rs);
                }
            }
        });
    }


    /**
     * Executes code for a connection. Synchronized to ensure only one connection exists
     *
     * @param function code to execute on the connection
     * @param <T>      return type of function
     * @return the return value of function
     * @throws DataAccessException if something goes wrong with sql execution
     */
    private synchronized <T> T execute(SQLFunction<T> function) throws DataAccessException {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + FILE_LOCATION)) {
            return function.apply(conn);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }


    /**
     * Adds parameters to a prepared statement
     *
     * @param ps     SQL PreparedStatement containing executable sql with '?'s equal to the length of params
     * @param params Parameters to fill ps with
     * @throws SQLException        if ps is closed or there are more params than '?'s in ps
     * @throws DataAccessException if params contains an unexpected data type
     */
    private void addParams(PreparedStatement ps, Object[] params) throws SQLException, DataAccessException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            switch (param) {
                case String p -> ps.setString(i + 1, p);
                case Integer p -> ps.setInt(i + 1, p);
                case Float p -> ps.setFloat(i + 1, p);
                case Long p -> ps.setLong(i + 1, p);
                case UUID p -> ps.setString(i + 1, p.toString());
                case Enum<?> p -> ps.setInt(i + 1, p.ordinal());
                case Identifier p -> ps.setString(i + 1, p.toString());
                case null -> ps.setNull(i + 1, Types.NULL);
                default -> throw new DataAccessException("Unexpected data type: " + param.getClass());
            }
        }
    }


    /**
     * Turns a set of UUID's into a string for use in sql
     *
     * @param set a set of UUID's to turn into a string
     * @return a string containing a SEPARATOR separated list of the UUID's from set
     */
    protected String setToString(Set<UUID> set) {
        StringBuilder builder = new StringBuilder();
        for (UUID uuid : set) builder.append(uuid.toString()).append(SEPARATOR);
        return builder.toString().trim();
    }


    /**
     * Turns a string generated from setToString back into a set of UUID's
     *
     * @param str a string containing a SEPARATOR separated list of UUID's
     * @return a set of UUID's
     */
    protected Set<UUID> stringToSet(String str) {
        return Arrays.stream(str.split(SEPARATOR)).map(UUID::fromString).collect(Collectors.toCollection(HashSet::new));
    }


    /**
     * Creates a new database file if it doesn't already exist
     *
     * @throws DataAccessException if file creation goes wrong
     */
    protected void configureDatabase() throws DataAccessException {
        if (!databaseCreated) {
            try {
                if (!FOLDER.exists() && !FOLDER.mkdirs()) {
                    throw new DataAccessException("Couldn't create folder: " + FOLDER.getAbsolutePath());
                }
                if (!new File(FILE_LOCATION).exists()) {
                    Files.copy(Objects.requireNonNull(getClass().getResourceAsStream("database.db")),
                            Path.of(FILE_LOCATION));
                }
                databaseCreated = true;
            } catch (Exception e) {
                throw new DataAccessException(e);
            }
        }
    }


    /**
     * Parses a result set and returns the result
     *
     * @param <T> return type
     */
    @FunctionalInterface
    protected interface ResultSetParser<T> {
        /**
         * Parses a result set
         *
         * @param rs ResultSet to parse
         * @return Contents of ResultSet as java object(s)
         * @throws SQLException if a database access error occurs, the result set is closed, or something goes wrong
         *                      with SQL execution
         */
        T parseResultSet(ResultSet rs) throws SQLException;
    }


    /**
     * Executes sql on a provided connection
     *
     * @param <T> return type
     */
    @FunctionalInterface
    private interface SQLFunction<T> {
        /**
         * Executes sql code on a connection
         *
         * @param conn the connection to use
         * @return return value of sql code
         * @throws SQLException        If SQL syntax is incorrect or other errors in sql execution
         * @throws DataAccessException If non-sql errors arise
         */
        T apply(Connection conn) throws SQLException, DataAccessException;
    }
}
