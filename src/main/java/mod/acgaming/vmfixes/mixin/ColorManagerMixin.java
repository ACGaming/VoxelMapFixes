package mod.acgaming.vmfixes.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import com.mamiyaotaru.voxelmap.ColorManager;
import com.mamiyaotaru.voxelmap.util.MutableBlockPos;
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
        if (Minecraft.getMinecraft().world.getTileEntity(blockPos) != null) cir.setReturnValue(452984832);
    }

    @Inject(method = "getColorForBlockPosBlockStateAndFacing", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getColorForBlockPosBlockStateAndFacing(BlockPos blockPos, IBlockState blockState, EnumFacing facing, CallbackInfoReturnable<Integer> cir)
    {
        if (Minecraft.getMinecraft().world.getTileEntity(blockPos) != null) cir.setReturnValue(452984832);
    }

    @Inject(method = "getBlockColorWithDefaultTint", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getBlockColorWithDefaultTint(MutableBlockPos blockPos, int blockStateID, CallbackInfoReturnable<Integer> cir)
    {
        if (Minecraft.getMinecraft().world.getTileEntity(blockPos) != null) cir.setReturnValue(0);
    }

    @Inject(method = "getBlockColor(Lcom/mamiyaotaru/voxelmap/util/MutableBlockPos;I)I", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getBlockColorI(MutableBlockPos blockPos, int blockStateID, CallbackInfoReturnable<Integer> cir)
    {
        if (Minecraft.getMinecraft().world.getTileEntity(blockPos) != null) cir.setReturnValue(0);
    }

    @Inject(method = "getBlockColor(Lcom/mamiyaotaru/voxelmap/util/MutableBlockPos;II)I", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getBlockColorII(MutableBlockPos blockPos, int blockStateID, int biomeID, CallbackInfoReturnable<Integer> cir)
    {
        if (Minecraft.getMinecraft().world.getTileEntity(blockPos) != null) cir.setReturnValue(0);
    }

    @Inject(method = "tintFromFakePlacedBlock", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_tintFromFakePlacedBlock(IBlockState blockState, MutableBlockPos loopBlockPos, byte biomeID, CallbackInfoReturnable<Integer> cir)
    {
        if (Minecraft.getMinecraft().world.getTileEntity(loopBlockPos) != null) cir.setReturnValue(-1);
    }

//    @Inject(method = "createTintTable", at = @At(value = "HEAD"), remap = false, cancellable = true)
//    public void VMFixes_createTintTable(IBlockState blockState, MutableBlockPos loopBlockPos, CallbackInfo ci)
//    {
//        if (Minecraft.getMinecraft().world.getTileEntity(loopBlockPos) != null) ci.cancel();
//    }
}