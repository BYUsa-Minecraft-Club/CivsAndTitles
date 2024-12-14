package edu.byu.minecraft.cat.model;

/**
 * Represents a single civ
 *
 * @param ID           unique civ id
 * @param name         name of the civ
 * @param numPoints    total build points this civ has earned
 * @param isActive     if this civ is currently marked as active
 * @param incorporated if this civ is incorporated (has a border, approved by an admin)
 * @param locationID   ID of location of civ
 * @param createdDate  date/time of civ creation
 */
public record Civ(int ID, String name, int numPoints, boolean isActive, boolean incorporated, int locationID,
                  String createdDate) {

    /**
     * Adds points to a civ
     *
     * @param points number of points to add
     * @return a new Civ object with number of points added
     */
    public Civ addPoints(int points) {
        return new Civ(ID, name, numPoints + points, isActive, incorporated, locationID, createdDate);
    }
}
