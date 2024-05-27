package edu.byu.minecraft.cat.model;

import java.util.UUID;

public record CivRequest (int ID, long timestamp, UUID submitter, String name, Location location) {}
