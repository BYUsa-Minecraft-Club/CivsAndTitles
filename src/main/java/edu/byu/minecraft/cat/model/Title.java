package edu.byu.minecraft.cat.model;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Values of possible player titles
 *
 * @param title       title text
 * @param format       title display
 * @param description title description, what was required to acquire the title, possible lore
 */
public record Title(String title, Text format, String description, Type type, Identifier advancement) {
   public enum Type {
        DEFAULT,
        WORLD,
        PERMANENT,
    }
}
