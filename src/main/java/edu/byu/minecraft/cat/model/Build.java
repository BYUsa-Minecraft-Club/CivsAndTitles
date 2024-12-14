package edu.byu.minecraft.cat.model;

/**
 * Represents a single build
 *
 * @param ID         id of the build
 * @param name       name of the build
 * @param timestamp  when the build was submitted
 * @param locationID ID of location of build
 * @param civID      ID of civ containing build, may be null
 * @param comments   any comments on the build, if applicable
 * @param points     number of points build has been awarded, may be null if build has not yet been judged
 * @param size       number of blocks contained in build
 * @param status     status of build judging
 */
public record Build(int ID, String name, long timestamp, int locationID, Integer civID, String comments, int points,
                    int size, JudgeStatus status) {

    /**
     * Build judging status
     */
    public enum JudgeStatus {
        /**
         * The build has been fully judged
         */
        JUDGED,

        /**
         * The build has not yet been fully judged but has been marked as ready to be judged by an admin
         */
        ACTIVE,

        /**
         * The build has not yet been fully judged and has not yet been marked as ready to be judged by an admin
         */
        PENDING
    }
}
