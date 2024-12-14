package edu.byu.minecraft.cat.model;

import net.minecraft.util.Identifier;

/**
 * Represents a location in minecraft a player can teleport to
 *
 * @param id    id of location
 * @param x     x block coordinate (+East/-West)
 * @param y     y block coordinate (+Up/-Down)
 * @param z     z block coordinate (+South/-North)
 * @param world identifier of world location is in
 * @param yaw   direction (horizontal) a player is facing
 * @param pitch direction (vertical) a player is facing
 */
public record Location(int id, int x, int y, int z, Identifier world, float yaw, float pitch) {}
