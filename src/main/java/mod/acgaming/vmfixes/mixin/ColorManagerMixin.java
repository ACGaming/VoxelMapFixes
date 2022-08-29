package mod.acgaming.vmfixes.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.mamiyaotaru.voxelmap.ColorManager;
import com.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
import com.mamiyaotaru.voxelmap.util.MutableBlockPos;
import mod.acgaming.vmfixes.VMFixes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ColorManager.class)
public class ColorManagerMixin
{
    @Inject(method = "getColor", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getColor(MutableBlockPos blockPos, IBlockState blockState, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.ripBozo(blockPos)) cir.setReturnValue(452984832);
    }

    @Inject(method = "getColorForBlockPosBlockStateAndFacing", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getColorForBlockPosBlockStateAndFacing(BlockPos blockPos, IBlockState blockState, EnumFacing facing, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.ripBozo((MutableBlockPos) blockPos)) cir.setReturnValue(452984832);
    }

    @Inject(method = "getBlockColorWithDefaultTint", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getBlockColorWithDefaultTint(MutableBlockPos blockPos, int blockStateID, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.ripBozo(blockPos)) cir.setReturnValue(0);
    }

    @Inject(method = "getBlockColor(Lcom/mamiyaotaru/voxelmap/util/MutableBlockPos;I)I", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getBlockColorI(MutableBlockPos blockPos, int blockStateID, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.ripBozo(blockPos)) cir.setReturnValue(0);
    }

    @Inject(method = "getBlockColor(Lcom/mamiyaotaru/voxelmap/util/MutableBlockPos;II)I", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getBlockColorII(MutableBlockPos blockPos, int blockStateID, int biomeID, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.ripBozo(blockPos)) cir.setReturnValue(0);
    }

    @Inject(method = "checkForBiomeTinting", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_checkForBiomeTinting(MutableBlockPos blockPos, IBlockState blockState, int color, CallbackInfo ci)
    {
        if (VMFixes.ripBozo(blockPos)) ci.cancel();
    }

    @Inject(method = "tintFromFakePlacedBlock", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_tintFromFakePlacedBlock(IBlockState blockState, MutableBlockPos loopBlockPos, byte biomeID, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.ripBozo(loopBlockPos)) cir.setReturnValue(-1);
    }

    @Inject(method = "getCustomBlockBiomeTintFromUnloadedChunk", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getCustomBlockBiomeTintFromUnloadedChunk(AbstractMapData mapData, World world, IBlockState blockState, MutableBlockPos blockPos, MutableBlockPos loopBlockPos, int startX, int startZ, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.ripBozo(blockPos)) cir.setReturnValue(-1);
    }

    @Inject(method = "createTintTable", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_createTintTable(IBlockState blockState, MutableBlockPos loopBlockPos, CallbackInfo ci)
    {
        if (VMFixes.ripBozo(loopBlockPos)) ci.cancel();
    }
}