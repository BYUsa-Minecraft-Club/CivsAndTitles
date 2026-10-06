package edu.byu.minecraft.cat.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.model.Title;
import edu.byu.minecraft.cat.util.TitleUtilities;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @ModifyReturnValue(method="decorateDisplayNameComponent", at=@At("RETURN"))
    private MutableComponent insertTitle(MutableComponent displayName) {
        if (!CivsAndTitles.playerMixinEnabled()) return displayName;

        UUID id = ((Player) (Object) this).getUUID();
        Title title = TitleUtilities.getCache(id);

        if (title == null) return displayName;

        return Component.empty()
                .setStyle(displayName.getStyle())
                .append(title.format())
                .append(" ")
                .append(displayName)
                .withStyle(style -> style);
    }
}
