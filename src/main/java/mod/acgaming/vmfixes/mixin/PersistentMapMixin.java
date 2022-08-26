package mod.acgaming.vmfixes.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
import com.mamiyaotaru.voxelmap.persistent.PersistentMap;
import com.mamiyaotaru.voxelmap.util.MutableBlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentMap.class)
public class PersistentMapMixin
{
//    @Inject(method = "getAndStoreData", at = @At(value = "HEAD"), remap = false, cancellable = true)
//    public void VMFixes_getAndStoreData(AbstractMapData mapData, World world, Chunk chunk, MutableBlockPos blockPos, boolean underground, int startX, int startZ, int imageX, int imageY, CallbackInfo ci)
//    {
//        if (Minecraft.getMinecraft().world.getTileEntity(blockPos) != null) ci.cancel();
//    }
//
//    @Inject(method = "getPixelColor", at = @At(value = "HEAD"), remap = false, cancellable = true)
//    public void VMFixes_getPixelColor(AbstractMapData mapData, World world, MutableBlockPos blockPos, MutableBlockPos loopBlockPos, boolean underground, int multi, int startX, int startZ, int imageX, int imageY, CallbackInfoReturnable<Integer> cir)
//    {
//        if (Minecraft.getMinecraft().world.getTileEntity(blockPos) != null || Minecraft.getMinecraft().world.getTileEntity(loopBlockPos) != null) cir.setReturnValue(0);
//    }
}