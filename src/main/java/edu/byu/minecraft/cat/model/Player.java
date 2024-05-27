package edu.byu.minecraft.cat.model;

import java.util.UUID;

/**
 * Represents a player
 *
 * @param uuid   UUID of player profile
 * @param name   current player name
 * @param points personal build points
 * @param title  current title
 * @param role   player role
 */
public record Player(UUID uuid, String name, int points, String title, Role role) {

    /**
     * Roles a player can have
     */
    public enum Role {

        /**
         * Server Administrator/Operator
         */
        ADMIN,

        /**
         * Judges Builds but is not an administrator/operator
         */
        BUILD_JUDGE,

        /**
         * Has permissions to use WorldEdit to count build sizes
         */
        BUILD_SIZER,

        /**
         * A player with no additional permissions
         */
        PLAYER
    }
}
