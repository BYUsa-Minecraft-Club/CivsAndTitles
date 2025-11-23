package edu.byu.minecraft.cat.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.model.Title;
import edu.byu.minecraft.cat.util.TitleUtilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @ModifyReturnValue(method="addTellClickEvent", at=@At("RETURN"))
    private MutableText insertTitle(MutableText displayName) {
        if (!CivsAndTitles.playerMixinEnabled()) return displayName;

        UUID id = ((PlayerEntity) (Object) this).getUuid();
        Title title = TitleUtilities.getCache(id);

        if (title == null) return displayName;

        return Text.empty()
                .setStyle(displayName.getStyle())
                .append(title.format())
                .append(" ")
                .append(displayName)
                .styled(style -> style);
    }
}
