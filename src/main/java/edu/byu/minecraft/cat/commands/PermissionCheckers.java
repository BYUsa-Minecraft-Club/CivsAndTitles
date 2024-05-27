package edu.byu.minecraft.cat.commands;

import net.minecraft.server.command.ServerCommandSource;

import java.util.UUID;

public class PermissionCheckers {

    /***
     * Checks if a player is in the civ
     * @param src
     * @return
     */
    public static boolean isInCiv(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        //TODO
        return true;
    }

    public static boolean isBuildJudge(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        //TODO
        return true;
    }

    /**
     * Checks if a player has at least one title.
     * @param src
     * @return
     */
    public static boolean hasTitle(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        //TODO
        return true;
    }


    /**
     * Checks if a player is a civ leader
     * @param src
     * @return
     */
    public static boolean isCivLeader(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        //TODO
        return true;
    }


    /**
     * Checks if a player is a civ owner
     * @param src
     * @return
     */
    public static boolean isCivOwner(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        //TODO
        return true;
    }

    /**
     * Checks if a player is an Admin
     * @param src
     * @return
     */
    public static boolean isAdmin(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        //TODO
        return true;
    }
}
