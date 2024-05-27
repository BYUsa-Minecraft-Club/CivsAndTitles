package edu.byu.minecraft.cat.model;

import java.util.UUID;

public record BuildScore(int ID, UUID judge, int functionality, int technical, int texture, int storytelling,
                         Integer thematic, int landscaping, int detailing, int lighting, int layout, int discretion,
                         int total, String comments) {}
