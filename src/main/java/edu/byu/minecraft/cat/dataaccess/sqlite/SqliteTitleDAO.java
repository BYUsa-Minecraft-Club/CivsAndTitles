package edu.byu.minecraft.cat.dataaccess.sqlite;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.TitleDAO;
import edu.byu.minecraft.cat.model.Title;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SqliteTitleDAO extends SqliteDAO<Title> implements TitleDAO {

    /**
     * Constructs a new sqlite DAO. If the database file doesn't already exist it is created
     *
     * @throws DataAccessException if the database file could not be created
     */
    protected SqliteTitleDAO() throws DataAccessException {
    }

    @Override
    public Title get(String s) throws DataAccessException {
        return executeQuery("SELECT * FROM title WHERE name = ?", this::parseSingle, s);
    }

    @Override
    public Collection<Title> getAll() throws DataAccessException {
        return executeQuery("SELECT * FROM title", this::parseCollection);
    }

    @Override
    public String insert(Title title) throws DataAccessException {
        String format = TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE,title.format()).getOrThrow().toString();
        executeUpdate("INSERT INTO title (name, format, description, type, advancement) VALUES (?, ?, ?, ?, ?)",
                title.title(), format, title.description(), title.type().name(), title.advancement().map(Identifier::toString).orElse(null));
        return title.title();
    }

    @Override
    public void delete(String s) throws DataAccessException {
        executeUpdate("DELETE FROM title WHERE name = ?", s);
    }

    @Override
    public void update(Title title) throws DataAccessException {
        String format = TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE,title.format()).getOrThrow().toString();
        executeUpdate("UPDATE title SET format = ?, description = ?, type = ?, advancement = ? WHERE name = ?",
                format, title.description(), title.type().name(), title.advancement().map(Identifier::toString).orElse(null), title.title());
    }

    @Override
    protected Title parse(ResultSet rs) throws SQLException {
        Gson gson = new Gson();
        JsonElement json = new Gson().fromJson(rs.getString("format"), JsonElement.class);
        Text format = TextCodecs.CODEC.parse(JsonOps.INSTANCE, json).getPartialOrThrow();
        String advancement = rs.getString("advancement");
        return new Title(
                rs.getString("name"),
                format,
                rs.getString("description"),
                Title.Type.valueOf(rs.getString("type")),
                advancement == null ? Optional.empty() : Optional.of(Identifier.of(advancement))
        );
    }

    @Override
    public Collection<Title> getAllTitlesByAdvancement(String advancement) throws DataAccessException {
        return executeQuery("SELECT * FROM title WHERE advancement = ?", this::parseCollection, advancement);
    }

    @Override
    public Collection<Title> getAllDefault() throws DataAccessException {
        return executeQuery("SELECT * FROM title WHERE type = ?", this::parseCollection, Title.Type.DEFAULT.name());
    }

    @Override
    public Collection<Title> getAllWorld() throws DataAccessException {
        return executeQuery("SELECT * FROM title WHERE type = ?", this::parseCollection, Title.Type.WORLD.name());
    }
}
