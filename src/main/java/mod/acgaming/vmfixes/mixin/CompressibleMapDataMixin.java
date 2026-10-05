package mod.acgaming.vmfixes.mixin;

import com.google.common.collect.BiMap;
import net.minecraft.init.Blocks;

import com.mamiyaotaru.voxelmap.persistent.CompressibleMapData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CompressibleMapData.class)
public abstract class CompressibleMapDataMixin
{
	@Redirect(method = "createKeyFromCurrentBlocks", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/BiMap;get(Ljava/lang/Object;)Ljava/lang/Object;"), remap = false)
	public Object vmfCreateKeyFromCurrentBlocks(BiMap<?, ?> map, Object key)
	{
		Object blockState = map.get(key);
		if (key instanceof Integer && blockState == null) return Blocks.AIR.getDefaultState();
		return blockState;
	}
}