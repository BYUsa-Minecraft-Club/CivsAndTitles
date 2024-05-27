package edu.byu.minecraft.cat.model;

import java.util.UUID;

/**
 * Represents a title a player has unlocked
 *
 * @param uuid   UUID of earning player
 * @param title  title text
 * @param earned when the title was earned
 */
public record UnlockedTitle(UUID uuid, String title, String earned) {}
