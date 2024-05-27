package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.Title;

import java.util.Collection;

/**
 * Data Access interface for titles
 */
public interface TitleDAO extends SimpleDAO<Title, String> {

    /**
     * Gets all titles visible to players
     * @return all titles visible to players
     * @throws DataAccessException If database cannot be accessed
     */
    Collection<Title> getVisible() throws DataAccessException;

}
