package edu.byu.minecraft.cat.model;

import java.util.UUID;

public record Player(UUID uuid, String name, int points, String title, Role role, String rank) {
    public enum Role {
        ADMIN, BUILD_JUDGE, BUILD_SIZER, PLAYER
    }
}
