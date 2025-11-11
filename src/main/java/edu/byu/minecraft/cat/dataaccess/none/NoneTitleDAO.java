package edu.byu.minecraft.cat.dataaccess.none;

import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.TitleDAO;
import edu.byu.minecraft.cat.model.Title;

import java.util.Collection;
import java.util.HashMap;

public class NoneTitleDAO implements TitleDAO {
    HashMap<String, Title> titles = new HashMap<>();

    @Override
    public Collection<Title> getAllTitlesByAdvancement(String advancement) throws DataAccessException {
        return titles.values().stream().filter(title -> title.advancement().isPresent() && title.advancement().get().toString().equals(advancement)).toList();
    }

    @Override
    public Title get(String s) throws DataAccessException {
        return titles.get(s);
    }

    @Override
    public Collection<Title> getAll() throws DataAccessException {
        return titles.values();
    }

    @Override
    public String insert(Title title) throws DataAccessException {
        titles.put(title.title(), title);
        return title.title();
    }

    @Override
    public void delete(String s) throws DataAccessException {
        titles.remove(s);
    }

    @Override
    public void update(Title title) throws DataAccessException {
        titles.put(title.title(),title);
    }
}
