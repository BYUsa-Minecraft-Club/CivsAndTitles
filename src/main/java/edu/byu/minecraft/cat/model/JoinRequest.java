package edu.byu.minecraft.cat.model;

import java.util.UUID;

/**
 * Represents a request of one player to join a civ
 *
 * @param ID        unique request ID
 * @param timestamp when the request was made
 * @param requester UUID of requesting player
 * @param civID     civ player is requesting to join
 */
public record JoinRequest(int ID, long timestamp, UUID requester, int civID) {}
