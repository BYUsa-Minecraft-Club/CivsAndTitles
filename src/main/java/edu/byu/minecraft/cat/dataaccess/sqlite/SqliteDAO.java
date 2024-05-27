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
    private static final File FOLDER = new File(String.format("config/%s", CivsAndTitles.MOD_ID));

    private static final String FILE_LOCATION = FOLDER.getPath() + "/database.db";

    private static boolean databaseCreated = false;

    protected SqliteDAO() throws DataAccessException {
        configureDatabase();
    }

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

    private synchronized <T> T execute(SQLFunction<T> function) throws DataAccessException {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + FILE_LOCATION)) {
            return function.apply(conn);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

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

    protected String setToString(Set<UUID> set) {
        StringBuilder builder = new StringBuilder();
        for (UUID uuid : set) builder.append(uuid.toString()).append(' ');
        return builder.toString().trim();
    }

    protected Set<UUID> stringToSet(String str) {
        return Arrays.stream(str.split(" ")).map(UUID::fromString).collect(Collectors.toCollection(HashSet::new));
    }

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


    @FunctionalInterface
    protected interface ResultSetParser<T> {
        T parseResultSet(ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    private interface SQLFunction<T> {
        T apply(Connection conn) throws SQLException, DataAccessException;
    }
}
