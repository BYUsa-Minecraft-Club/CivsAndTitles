package edu.byu.minecraft.cat.mixins;

import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.DataAccess;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Title;
import edu.byu.minecraft.cat.model.UnlockedTitle;
import edu.byu.minecraft.cat.util.AsyncUtilities;
import edu.byu.minecraft.cat.util.Utilities;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementsMixin {
    @Final
    @Shadow
    private Map<AdvancementHolder, AdvancementProgress> progress;
    @Shadow
    private ServerPlayer player;

    @Inject(method = "award", at= @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void grantTitles(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (CivsAndTitles.advancementsEnabled() && progress.get(advancement).isDone()) {
            AsyncUtilities.performAsync(player.level().getServer(),() -> {
                DataAccess access = CivsAndTitles.getDataAccess();
                try {
                    for (Title title : access.getTitleDAO().getAllTitlesByAdvancement(advancement.toString())) {
                        access.getUnlockedTitleDAO().insert(new UnlockedTitle(player.getUUID(), title.title(), Utilities.getTime()));
                        CivsAndTitles.LOGGER.info("Awarded title {} to player {} for earning advancement {}", title.title(), player.getName().getString(), advancement);
                    }
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
            }, error -> {
                CivsAndTitles.LOGGER.error("Database error automatically awarding titles to {}: ", player.getName().getString(), error);
            }, error -> {
                CivsAndTitles.LOGGER.error("Unknown error automatically awarding titles to {}: ", player.getName().getString(), error);
            });
        }
    }
}
