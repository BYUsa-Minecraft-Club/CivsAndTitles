package edu.byu.minecraft.cat.model;

import java.util.UUID;

public record CivParticipantPlayer (int civID, UUID playerUUID, Status status){
    public enum Status {
        FOUNDER,
        OWNER,
        LEADER,
        MEMBER,
        CONTRIBUTOR
    }
}
