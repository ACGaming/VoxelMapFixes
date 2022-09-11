package mod.acgaming.vmfixes.mixin;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import com.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
import com.mamiyaotaru.voxelmap.interfaces.IChangeObserver;
import com.mamiyaotaru.voxelmap.interfaces.IPersistentMap;
import com.mamiyaotaru.voxelmap.persistent.PersistentMap;
import com.mamiyaotaru.voxelmap.util.BlockRepository;
import com.mamiyaotaru.voxelmap.util.MutableBlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PersistentMap.class)
public abstract class PersistentMapMixin implements IPersistentMap, IChangeObserver
{
    /**
     * @author ACGaming
     * @reason VMFixes
     */
    @Overwrite(remap = false)
    public void getAndStoreData(AbstractMapData mapData, World world, Chunk chunk, MutableBlockPos blockPos, boolean underground, int startX, int startZ, int imageX, int imageY)
    {
        blockPos = blockPos.withXYZ(startX + imageX, 0, startZ + imageY);
        IBlockState blockState;
        int biomeID;
        if (!chunk.isEmpty())
        {
            biomeID = Biome.getIdForBiome(chunk.getBiome(blockPos, world.provider.getBiomeProvider()));
        }
        else
        {
            biomeID = -1;
        }
        mapData.setBiomeID(imageX, imageY, biomeID);
        if (biomeID == -1) return;
        int surfaceHeight;
        boolean solid = false;
        surfaceHeight = getBlockHeight(underground, chunk, blockPos, startX + imageX, startZ + imageY);
        blockState = chunk.getBlockState(blockPos.withXYZ(startX + imageX, surfaceHeight, startZ + imageY));
        if (blockState.getMaterial() != Material.SNOW) blockState = chunk.getBlockState(blockPos.withXYZ(startX + imageX, surfaceHeight - 1, startZ + imageY));
        blockState = blockState.getBlock().hasTileEntity(blockState) ? blockState.getBlock().getDefaultState() : blockState.getActualState(world, blockPos);
        mapData.setHeight(imageX, imageY, surfaceHeight);
        mapData.setBlockstate(imageX, imageY, blockState);
        if (surfaceHeight == -1)
        {
            surfaceHeight = 80;
            solid = true;
        }
        if (blockState.getMaterial() == Material.LAVA) solid = false;
        int light;
        if (!solid)
        {
            light = getLight(blockState, chunk, blockPos, startX + imageX, startZ + imageY, surfaceHeight, solid);
            mapData.setLight(imageX, imageY, light);
        }
        int seafloorHeight = 0;
        int seafloorLight = 0;
        int underwaterTransparentHeight = 0;
        Material material = blockState.getMaterial();
        if (material == Material.WATER || material == Material.ICE)
        {
            int[] underwaterHeights = getSeafloorHeight(chunk, blockPos, startX + imageX, startZ + imageY, surfaceHeight);
            seafloorHeight = underwaterHeights[0];
            underwaterTransparentHeight = underwaterHeights[1];
            blockPos.setXYZ(startX + imageX, seafloorHeight - 1, startZ + imageY);
            blockState = chunk.getBlockState(blockPos);
            if (blockState.getMaterial() == Material.WATER) blockState = BlockRepository.air.getDefaultState();
        }
        if (blockState != BlockRepository.air.getDefaultState())
        {
            blockState = blockState.getBlock().hasTileEntity(blockState) ? blockState.getBlock().getDefaultState() : blockState.getActualState(world, blockPos);
            seafloorLight = getLight(blockState, chunk, blockPos, startX + imageX, startZ + imageY, seafloorHeight, solid);
        }
        mapData.setOceanFloorHeight(imageX, imageY, seafloorHeight);
        mapData.setOceanFloorBlockstate(imageX, imageY, blockState);
        mapData.setOceanFloorLight(imageX, imageY, seafloorLight);
        int transparentHeight;
        int transparentLight = 0;
        transparentHeight = getTransparentHeight(underground, chunk, blockPos, startX + imageX, startZ + imageY, surfaceHeight);
        if (transparentHeight == 0 && underwaterTransparentHeight > 0) transparentHeight = underwaterTransparentHeight;
        if (transparentHeight != 0)
        {
            blockPos.setXYZ(startX + imageX, transparentHeight - 1, startZ + imageY);
            blockState = chunk.getBlockState(blockPos);
        }
        else
        {
            blockState = BlockRepository.air.getDefaultState();
        }
        if (blockState != BlockRepository.air.getDefaultState())
        {
            blockState = blockState.getBlock().hasTileEntity(blockState) ? blockState.getBlock().getDefaultState() : blockState.getActualState(world, blockPos);
            transparentLight = getLight(blockState, chunk, blockPos, startX + imageX, startZ + imageY, transparentHeight, solid);
        }
        mapData.setTransparentHeight(imageX, imageY, transparentHeight);
        mapData.setTransparentBlockstate(imageX, imageY, blockState);
        mapData.setTransparentLight(imageX, imageY, transparentLight);
        int foliageHeight = 0;
        int foliageLight = 0;
        IBlockState foliageBlockState = null;
        if (transparentHeight != surfaceHeight + 1 && !solid)
        {
            foliageHeight = surfaceHeight + 1;
            blockPos.setXYZ(startX + imageX, foliageHeight - 1, startZ + imageY);
            foliageBlockState = chunk.getBlockState(blockPos);
            material = foliageBlockState.getMaterial();
            if (material == Material.SNOW || material == Material.AIR || material == Material.LAVA) foliageHeight = 0;
            if (foliageBlockState == blockState) foliageHeight = 0;
        }
        if (foliageHeight == 0 && !solid && seafloorHeight > 0 && transparentHeight != seafloorHeight + 1)
        {
            foliageHeight = seafloorHeight + 1;
            blockPos.setXYZ(startX + imageX, foliageHeight - 1, startZ + imageY);
            foliageBlockState = chunk.getBlockState(blockPos);
            material = foliageBlockState.getMaterial();
            if (material == Material.AIR || material == Material.LAVA || material == Material.WATER || material == Material.ICE) foliageHeight = 0;
            if (foliageBlockState == blockState) foliageHeight = 0;
        }
        if (foliageHeight != 0)
        {
            blockState = foliageBlockState;
        }
        else
        {
            blockState = BlockRepository.air.getDefaultState();
        }
        if (blockState != BlockRepository.air.getDefaultState())
        {
            blockState = blockState.getBlock().hasTileEntity(blockState) ? blockState.getBlock().getDefaultState() : blockState.getActualState(world, blockPos);
            foliageLight = getLight(blockState, chunk, blockPos, startX + imageX, startZ + imageY, foliageHeight, solid);
        }
        mapData.setFoliageHeight(imageX, imageY, foliageHeight);
        mapData.setFoliageBlockstate(imageX, imageY, blockState);
        mapData.setFoliageLight(imageX, imageY, foliageLight);
    }

    @Shadow
    protected abstract int getBlockHeight(boolean underground, Chunk chunk, MutableBlockPos blockPos, int x, int z);

    @Shadow
    protected abstract int getLight(IBlockState blockState, Chunk chunk, MutableBlockPos blockPos, int x, int z, int height, boolean solid);

    @Shadow
    protected abstract int[] getSeafloorHeight(Chunk chunk, MutableBlockPos blockPos, int x, int z, int height);

    @Shadow
    protected abstract int getTransparentHeight(boolean underground, Chunk chunk, MutableBlockPos blockPos, int x, int z, int height);
}