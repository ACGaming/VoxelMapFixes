package mod.acgaming.vmfixes.compat.ingameinfoxml.mixin;

import com.github.lunatrius.ingameinfo.handler.ConfigurationHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mamiyaotaru.voxelmap.Map;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.Collections;

@Mixin(Map.class)
public class MapMixin {

    /**
     * @author atferrys
     * @reason Prevents the Minimap from shifting down to make space for the
     * potion overlay even when the potion overlay is disabled by InGame Info XML
     */
    @WrapOperation(
            method = "drawMinimap",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/entity/EntityPlayerSP;getActivePotionEffects()Ljava/util/Collection;",
                    ordinal = 0,
                    remap = true
            ),
            remap = false
    )
    private Collection<PotionEffect> vmfPreventShifting(EntityPlayerSP instance, Operation<Collection<PotionEffect>> original) {

        if(!ConfigurationHandler.showOverlayPotions) {
            return Collections.emptyList();
        }

        return original.call(instance);

    }

}
