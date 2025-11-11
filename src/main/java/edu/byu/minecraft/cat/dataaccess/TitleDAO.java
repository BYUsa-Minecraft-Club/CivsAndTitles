package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.Title;

import java.util.Collection;
import java.util.List;

/**
 * Data Access interface for titles
 */
public interface TitleDAO extends SimpleDAO<Title, String> {
    Collection<Title> getAllTitlesByAdvancement(String advancement) throws DataAccessException;
}
