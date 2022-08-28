package mod.acgaming.vmfixes.mixin;

import com.google.common.collect.BiMap;
import net.minecraft.block.state.IBlockState;

import com.mamiyaotaru.voxelmap.persistent.CompressibleMapData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompressibleMapData.class)
public class CompressibleMapDataMixin
{
    @Shadow
    private BiMap<IBlockState, Integer> stateToInt;

    @Inject(method = "getStateToInt", at = @At(value = "RETURN"), remap = false)
    public void VMFixes_getStateToInt(CallbackInfoReturnable<BiMap<IBlockState, Integer>> cir)
    {
        if (this.stateToInt == null) cir.cancel();
    }
}