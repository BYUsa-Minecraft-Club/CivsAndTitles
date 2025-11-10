package edu.byu.minecraft.cat.dataaccess.sqlite;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.TitleDAO;
import edu.byu.minecraft.cat.model.Title;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class SqliteTitleDAO extends SqliteDAO<Title> implements TitleDAO {

    /**
     * Constructs a new sqlite DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqliteTitleDAO() throws DataAccessException {
    }

    /**
     * @param s Unique key value to match against
     * @return
     * @throws DataAccessException
     */
    @Override
    public Title get(String s) throws DataAccessException {
        return executeQuery("SELECT * FROM title WHERE name = ?", this::parseSingle, s);
    }

    /**
     * @return
     * @throws DataAccessException
     */
    @Override
    public Collection<Title> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM title", this::parseCollection);
    }

    /**
     * @param title object to insert
     * @return
     * @throws DataAccessException
     */
    @Override
    public String insert(Title title) throws DataAccessException {
        String format = TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE,title.format()).getOrThrow().toString();
        executeUpdate("INSERT INTO title (name, format, description, type) VALUES (?, ?, ?, ?)",
                title.title(), format, title.description(), title.type().name());
        return title.title();
    }

    /**
     * @param s unique key
     * @throws DataAccessException
     */
    @Override
    public void delete(String s) throws DataAccessException {
        executeUpdate("DELETE FROM title WHERE name = ?", s);
    }

    /**
     * @param title unique key and values to update to
     * @throws DataAccessException
     */
    @Override
    public void update(Title title) throws DataAccessException {
        String format = TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE,title.format()).getOrThrow().toString();
        executeUpdate("UPDATE title SET format = ?, description = ?, type = ? WHERE name = ?",
                format, title.description(), title.type().name(), title.title());
    }

    /**
     * @param rs result set to retrieve row data from
     * @return
     * @throws SQLException
     */
    @Override
    protected Title parse(ResultSet rs) throws SQLException {
        Gson gson = new Gson();
        JsonElement json = new Gson().fromJson(rs.getString("format"), JsonElement.class);
        Text format = TextCodecs.CODEC.parse(JsonOps.INSTANCE, json).getPartialOrThrow();
        return new Title(
                rs.getString("name"),
                format,
                rs.getString("description"),
                Title.Type.valueOf(rs.getString("type"))
        );
    }
}
