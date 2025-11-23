package edu.byu.minecraft.cat.commands;

import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.UnlockedTitle;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.UUID;

public class PermissionCheckers {
    /**
     * Checks if a player has at least one title.
     */
    public static boolean hasTitle(ServerCommandSource src) {
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) return false;

        UUID pl = player.getUuid();
        try {
            Collection<UnlockedTitle> titles = CivsAndTitles.getDataAccess().getUnlockedTitleDAO().getAll(pl);
            return !titles.isEmpty();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a player is an Admin
     */
    public static boolean isAdmin(ServerCommandSource src) {
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) return false;
        return player.getPermissionLevel() > 2;
    }
}
