package edu.byu.minecraft.cat.dataaccess.postgres;

import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.*;
import edu.byu.minecraft.cat.dataaccess.sqlite.SqliteDataAccess;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.sql.*;
import java.util.UUID;

public class PostgresDataAccess implements DataAccess {
    PostgresConfig config;
    Connection database;
    SqliteDataAccess fallback = null;

    PostgresPlayerDAO playerDAO;
    PostgresTitleDAO titleDAO;
    PostgresUnlockedTitleDAO unlockedTitleDAO;

    public PostgresDataAccess() throws DataAccessException {
        config = PostgresConfig.loadOrCreate();
        try {
            database = DriverManager.getConnection(config.toJdbcUrl(), config.username(), config.password());
            configureDatabase();

            playerDAO = new PostgresPlayerDAO(this);
            titleDAO = new PostgresTitleDAO(this);
            unlockedTitleDAO = new PostgresUnlockedTitleDAO(this);
        } catch (SQLException e) {
            CivsAndTitles.LOGGER.error("Error connecting to Postgresql:", e);
            CivsAndTitles.LOGGER.warn("Falling back to SqLite");
            fallback = new SqliteDataAccess();
        }
    }

    @Override
    public PlayerDAO getPlayerDAO() throws DataAccessException {
        if (fallback != null) return fallback.getPlayerDAO();
        return playerDAO;
    }

    @Override
    public TitleDAO getTitleDAO() throws DataAccessException {
        if (fallback != null) return fallback.getTitleDAO();
        return titleDAO;
    }

    @Override
    public UnlockedTitleDAO getUnlockedTitleDAO() throws DataAccessException {
        if (fallback != null) return fallback.getUnlockedTitleDAO();
        return unlockedTitleDAO;
    }

    /**
     * Creates a new database file if it doesn't already exist
     *
     * @throws DataAccessException if file creation goes wrong
     */
    protected void configureDatabase() throws DataAccessException {
        // I'm too lazy to check if the tables already exist so I'm just going to run the create commands on every start
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("create_sqlite_tables.sql")) {
            if(is == null) {
                throw new DataAccessException("Could not find required database creation resource file");
            }
            String contents = new String(is.readAllBytes());
            String[] split = contents.split(";");
            for (String createStatement : split) {
                createStatement = createStatement.trim();
                if(!createStatement.isBlank()) {
                    executeUpdate(createStatement + ";");
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
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
    protected void executeUpdate(String statement, Object... params) throws DataAccessException {
        execute(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                addParams(ps, params);
                ps.executeUpdate();
            }
            return null;
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
    protected <T> T executeQuery(String statement, PostgresDataAccess.ResultSetParser<T> parser, Object... params)
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
    private <T> T execute(PostgresDataAccess.SQLFunction<T> function) throws DataAccessException {
        synchronized (PostgresDataAccess.class) {
            try {
                return function.apply(database);
            } catch (SQLException e) {
                throw new DataAccessException(e);
            }
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
                case Boolean p -> ps.setBoolean(i + 1, p);
                case UUID p -> ps.setString(i + 1, p.toString());
                case Enum<?> p -> ps.setString(i + 1, p.name());
                case Identifier p -> ps.setString(i + 1, p.toString());
                case null -> ps.setNull(i + 1, Types.NULL);
                default -> throw new DataAccessException("Unexpected data type: " + param.getClass());
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
