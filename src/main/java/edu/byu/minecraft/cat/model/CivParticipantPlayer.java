package edu.byu.minecraft.cat.model;

public record CivParticipantPlayer (int civID, String playerUUID, Status status){
    public enum Status {
        FOUNDER,
        OWNER,
        LEADER,
        MEMBER,
        CONTRIBUTOR
    }
}
