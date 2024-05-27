package edu.byu.minecraft.cat.model;

import net.minecraft.util.Identifier;

public record Location(int x, int y, int z, Identifier world, float yaw, float pitch) {
}
