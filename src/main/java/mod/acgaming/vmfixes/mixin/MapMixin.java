package mod.acgaming.vmfixes.mixin;

import net.minecraft.world.World;

import com.mamiyaotaru.voxelmap.Map;
import com.mamiyaotaru.voxelmap.util.MutableBlockPos;
import mod.acgaming.vmfixes.VMFixes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Map.class)
public class MapMixin
{
    @Shadow
    private MutableBlockPos blockPos;

    @Inject(method = "getPixelColor", at = @At(value = "RETURN"), remap = false, cancellable = true)
    public void VMFixes_getPixelColor(boolean needBiome, boolean needHeightAndID, boolean needTint, boolean needLight, boolean nether, boolean caves, World world, int multi, int startX, int startZ, int imageX, int imageY, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.configBlocked(blockPos)) cir.setReturnValue(0);
    }
}