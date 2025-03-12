package mod.acgaming.vmfixes.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import com.mamiyaotaru.voxelmap.persistent.PersistentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PersistentMap.class)
public abstract class PersistentMapMixin
{
    @Redirect(method = "getAndStoreData", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getActualState(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState vmfGetAndStoreData(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos)
    {
        return blockState.getBlock().hasTileEntity(blockState) ? blockState : blockState.getActualState(blockAccess, blockPos);
    }
}