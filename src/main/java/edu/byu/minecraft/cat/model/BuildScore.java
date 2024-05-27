package edu.byu.minecraft.cat.model;

import java.util.UUID;

/**
 * Represents the scores from one judge on one build
 *
 * @param ID            unique ID
 * @param judge         UUID of build judge player
 * @param functionality functionality score zero to ten
 * @param technical     technical score zero to five
 * @param texture       texture score zero to five
 * @param storytelling  storytelling score zero to five
 * @param thematic      thematic score zero to five. May be null if not a part of a civ
 * @param landscaping   landscaping score zero to five
 * @param detailing     detailing score zero to five
 * @param lighting      lighting score zero to five
 * @param layout        layout score zero to five
 * @param discretion    discretion score zero to five
 * @param total         total score zero to fifty if not in a civ, or fifty-five if in a civ
 * @param comments      any comments from the build judge
 */
public record BuildScore(int ID, UUID judge, int functionality, int technical, int texture, int storytelling,
                         Integer thematic, int landscaping, int detailing, int lighting, int layout, int discretion,
                         int total, String comments) {}
