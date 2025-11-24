package edu.byu.minecraft.cat.commands;

import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.UnlockedTitle;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

public class PermissionCheckers {
    private static final String ADMIN_PERMISSION_NODE = "titles.admin";
    private static final String MODIFY_PERMISSION_NODE = "titles.admin.modify";
    private static final String AWARD_PERMISSION_NODE = "titles.admin.award";
    private static final String CLEAR_WORLD_TITLES_PERMISSION_NODE = "titles.admin.clear_world";
    private static final String APPLY_SELF_PERMISSION_NODE = "titles.apply";

    public static final Predicate<ServerCommandSource> ADMIN_PERMISSION = Permissions.require(ADMIN_PERMISSION_NODE, 2);
    public static final Predicate<ServerCommandSource> MODIFY_PERMISSION = Permissions.require(MODIFY_PERMISSION_NODE, 2);
    public static final Predicate<ServerCommandSource> AWARD_PERMISSION = Permissions.require(AWARD_PERMISSION_NODE, 2);
    public static final Predicate<ServerCommandSource> APPLY_PERMISSION = Permissions.require(APPLY_SELF_PERMISSION_NODE, true);
    public static final Predicate<ServerCommandSource> CLEAR_WORLD_TITLES_PERMISSION = Permissions.require(CLEAR_WORLD_TITLES_PERMISSION_NODE, 2);
//
//    /**
//     * Checks if a player has at least one title.
//     */
//    public static boolean hasTitle(ServerCommandSource src) {
//        ServerPlayerEntity player = src.getPlayer();
//        if (player == null) return false;
//
//        UUID pl = player.getUuid();
//        try {
//            Collection<UnlockedTitle> titles = CivsAndTitles.getDataAccess().getUnlockedTitleDAO().getAll(pl);
//            return !titles.isEmpty();
//        } catch (DataAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * Checks if a player is an Admin
//     */
//    public static boolean isAdmin(ServerCommandSource src) {
//        ServerPlayerEntity player = src.getPlayer();
//        if (player == null) return false;
//        return player.getPermissionLevel() > 2;
//    }
}
