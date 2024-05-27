package edu.byu.minecraft.cat.model;

import java.util.UUID;

public record JoinRequest(int ID, long timestamp, UUID requester, int civID) {}
