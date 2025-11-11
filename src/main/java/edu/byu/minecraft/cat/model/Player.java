package edu.byu.minecraft.cat.model;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a player
 *
 * @param uuid     UUID of player profile
 * @param name     current player name
 * @param title    current title
 */
public record Player(UUID uuid, String name, @Nullable String title) {
    public Player setTitle(String title) {
        return new Player(uuid, name, title);
    }
}
