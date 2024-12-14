package edu.byu.minecraft.cat.model;

import java.util.UUID;

/**
 * Represents a request to make a new Civ
 *
 * @param ID          unique request id
 * @param requestDate when the request was made
 * @param submitter   uuid of player submitting request
 * @param name        requested civ name
 * @param location    location of requested civ
 */
public record CivRequest(int ID, String requestDate, UUID submitter, String name, Location location) {}
