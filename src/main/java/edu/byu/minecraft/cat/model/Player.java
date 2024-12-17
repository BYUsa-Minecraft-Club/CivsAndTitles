package edu.byu.minecraft.cat.model;

import java.util.UUID;

/**
 * Represents a player
 *
 * @param uuid     UUID of player profile
 * @param name     current player name
 * @param points   personal build points
 * @param title    current title
 * @param role     player role
 * @param showRank whether the player wishes their rank to be shown to other players
 */
public record Player(UUID uuid, String name, int points, String title, Role role, boolean showRank) {
    public Player setTitle(String title) {
        return new Player(uuid, name, points, title, role, showRank);
    }

    public Player setRole(Role role) {
        return new Player(uuid, name, points, title, role, showRank);
    }

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
