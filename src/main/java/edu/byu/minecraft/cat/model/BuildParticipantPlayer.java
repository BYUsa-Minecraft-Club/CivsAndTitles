package edu.byu.minecraft.cat.model;

public record BuildParticipantPlayer (int buildID, String playerUUID, Status status) {
    public enum Status {
        SUBMITTER,
        BUILDER
    }
}
