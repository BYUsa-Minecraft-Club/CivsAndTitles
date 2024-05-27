package edu.byu.minecraft.cat.model;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a single civ
 *
 * @param ID           unique civ id
 * @param name         name of the civ
 * @param numPoints    total build points this civ has earned
 * @param incorporated if this civ is incorporated (has a border, approved by an admin)
 * @param active       if this civ is currently marked as active
 * @param founder      UUID of founding player
 * @param owner        UUID of current owner player
 * @param leaders      set of UUIDs of civ leader players
 * @param contributors set of UUIDs of players who have contributed to civ builds
 * @param members      set of UUIDS of civ member players
 * @param location     location of civ
 * @param createdDate  date/time of civ creation
 */
public record Civ(int ID, String name, int numPoints, boolean incorporated, boolean active, UUID founder, UUID owner,
                  Set<UUID> leaders, Set<UUID> contributors, Set<UUID> members, Location location, String createdDate) {

    /**
     * Adds points to a civ
     * @param points number of points to add
     * @return a new Civ object with number of points added
     */
    public Civ addPoints(int points) {
        return new Civ(ID, name, numPoints + points, incorporated, active, founder, owner, leaders, contributors,
                members, location, createdDate);
    }
}
