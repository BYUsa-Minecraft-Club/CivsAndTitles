package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.Title;

import java.util.Collection;
import java.util.List;

/**
 * Data Access interface for titles
 */
public interface TitleDAO extends SimpleDAO<Title, String> {
    /**
     * Gets every title awarded from an advancement
     *
     * @param advancement The registry key of the advancement to search for
     * @return A collection of titles that should be awarded
     * @throws DataAccessException Database error
     */
    Collection<Title> getAllTitlesByAdvancement(String advancement) throws DataAccessException;
    /**
     * Gets every title that can be used by default
     *
     * @return A collection of default titles
     * @throws DataAccessException Database error
     */
    Collection<Title> getAllDefault() throws DataAccessException;
    /**
     * Gets every title that players should not carry over between worlds (World titles)
     *
     * @return A collection of world titles
     * @throws DataAccessException Database error
     */
    Collection<Title> getAllWorld() throws DataAccessException;
}
