package mod.acgaming.vmfixes.mixin;

import net.minecraft.world.chunk.Chunk;

import com.mamiyaotaru.voxelmap.persistent.CachedRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CachedRegion.class)
public class CachedRegionMixin
{
    @Inject(method = "loadChunkData", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_loadChunkData(Chunk chunk, int chunkX, int chunkZ, CallbackInfo ci)
    {
        if (chunk == null) ci.cancel();
    }
}