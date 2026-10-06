package edu.byu.minecraft.cat.model;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * Values of possible player titles
 *
 * @param title       title text
 * @param format       title display
 * @param description title description, what was required to acquire the title, possible lore
 */
public record Title(String title, Component format, String description, Type type, Optional<Identifier> advancement) {
    public enum Type {
        DEFAULT,
        WORLD,
        PERMANENT,
    }
}
