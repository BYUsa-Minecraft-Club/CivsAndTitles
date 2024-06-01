package edu.byu.minecraft.cat.model;

/**
 * Values of possible player titles
 *
 * @param title       title text
 * @param color       title display color
 * @param description title description, what was required to acquire the title, possible lore
 */
public record Title(String title, String color, String description) {}
