package edu.byu.minecraft.cat.model;

import java.util.UUID;

/**
 * Represents a request of one player to join a civ
 *
 * @param ID          unique request ID
 * @param requestDate when the request was made
 * @param requester   UUID of requesting player
 * @param civID       civ player is requesting to join
 */
public record JoinRequest(int ID, String requestDate, UUID requester, int civID) {}
