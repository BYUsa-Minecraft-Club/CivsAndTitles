package edu.byu.minecraft.cat.model;

import java.util.Set;
import java.util.UUID;

public record Civ(int ID, String name, int numPoints, boolean incorporated, boolean active, UUID founder, UUID owner,
                  Set<UUID> leaders, Set<UUID> contributors, Set<UUID> members, Location location,
                  String createdDate) {
    public Civ addPoints(int points) {
        return new Civ(ID, name, numPoints + points, incorporated, active, founder, owner, leaders,
                contributors, members, location, createdDate);
    }
}
