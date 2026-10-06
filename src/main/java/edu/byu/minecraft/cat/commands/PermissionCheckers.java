package edu.byu.minecraft.cat.commands;

import edu.byu.minecraft.cat.CivsAndTitles;
import net.fabricmc.fabric.api.permission.v1.PermissionPredicates;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.PermissionLevel;

import java.util.function.Predicate;

public class PermissionCheckers {

    private static final Identifier ADMIN_PERMISSION_NODE = CivsAndTitles.id("admin");
    private static final Identifier MODIFY_PERMISSION_NODE = CivsAndTitles.id("admin.modify");
    private static final Identifier AWARD_PERMISSION_NODE = CivsAndTitles.id("admin.award");
    private static final Identifier CLEAR_WORLD_TITLES_PERMISSION_NODE = CivsAndTitles.id("admin.clear_world");
    private static final Identifier APPLY_SELF_PERMISSION_NODE = CivsAndTitles.id("apply");

    public static final Predicate<CommandSourceStack> ADMIN_PERMISSION = PermissionPredicates.require(ADMIN_PERMISSION_NODE, PermissionLevel.byId(2));
    public static final Predicate<CommandSourceStack> MODIFY_PERMISSION = PermissionPredicates.require(MODIFY_PERMISSION_NODE, PermissionLevel.byId(2));
    public static final Predicate<CommandSourceStack> AWARD_PERMISSION = PermissionPredicates.require(AWARD_PERMISSION_NODE, PermissionLevel.byId(2));
    public static final Predicate<CommandSourceStack> APPLY_PERMISSION = PermissionPredicates.require(APPLY_SELF_PERMISSION_NODE, true);
    public static final Predicate<CommandSourceStack> CLEAR_WORLD_TITLES_PERMISSION = PermissionPredicates.require(CLEAR_WORLD_TITLES_PERMISSION_NODE, PermissionLevel.byId(2));
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
