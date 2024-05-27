package edu.byu.minecraft.cat.model;

import java.util.Set;
import java.util.UUID;

public record Build(int ID, String name, long timestamp, UUID submitter, Location location, int civID,
                    Set<UUID> builders, String comments, int points, int size, JudgeStatus status) {
    public enum JudgeStatus {
        JUDGED, ACTIVE, PENDING
    }
}
