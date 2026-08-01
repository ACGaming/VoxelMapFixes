package mod.acgaming.vmfixes.mixin.early;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    /**
     * @author atferrys
     * @reason Restores the depth mask enabled in {@link com.mamiyaotaru.voxelmap.forgemod.mixins.MixinEntityRenderer}
     */
    @Inject(
            method = "renderWorldPass",
            at = @At(
                    value = "INVOKE_STRING",
                    target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
                    args = "ldc=translucent",
                    shift = At.Shift.AFTER
            )
    )
    private void vmfRestoreDepthMask(int pass, float partialTicks, long timeSlice, CallbackInfo ci) {
        GlStateManager.depthMask(false);
    }

}