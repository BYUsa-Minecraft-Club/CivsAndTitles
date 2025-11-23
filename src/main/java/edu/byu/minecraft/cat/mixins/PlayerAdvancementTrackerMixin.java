package edu.byu.minecraft.cat.mixins;

import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.DataAccess;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.model.Title;
import edu.byu.minecraft.cat.model.UnlockedTitle;
import edu.byu.minecraft.cat.util.AsyncUtilities;
import edu.byu.minecraft.cat.util.Utilities;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
    @Final
    @Shadow
    private Map<AdvancementEntry, AdvancementProgress> progress;
    @Shadow
    private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at= @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementRewards;apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void grantTitles(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (CivsAndTitles.advancementsEnabled() && progress.get(advancement).isDone()) {
            AsyncUtilities.performAsync(owner.getEntityWorld().getServer(),() -> {
                DataAccess access = CivsAndTitles.getDataAccess();
                try {
                    for (Title title : access.getTitleDAO().getAllTitlesByAdvancement(advancement.toString())) {
                        access.getUnlockedTitleDAO().insert(new UnlockedTitle(owner.getUuid(), title.title(), Utilities.getTime()));
                        CivsAndTitles.LOGGER.info("Awarded title {} to player {} for earning advancement {}", title.title(), owner.getName().getString(), advancement);
                    }
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
            }, error -> {
                CivsAndTitles.LOGGER.error("Database error automatically awarding titles to {}: ", owner.getName().getString(), error);
            }, error -> {
                CivsAndTitles.LOGGER.error("Unknown error automatically awarding titles to {}: ", owner.getName().getString(), error);
            });
        }
    }
}
