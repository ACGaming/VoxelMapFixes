package mod.acgaming.vmfixes.mixin;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import com.mamiyaotaru.voxelmap.Map;
import com.mamiyaotaru.voxelmap.MapSettingsManager;
import com.mamiyaotaru.voxelmap.interfaces.IColorManager;
import com.mamiyaotaru.voxelmap.interfaces.IMap;
import com.mamiyaotaru.voxelmap.util.BiomeRepository;
import com.mamiyaotaru.voxelmap.util.BlockRepository;
import com.mamiyaotaru.voxelmap.util.FullMapData;
import com.mamiyaotaru.voxelmap.util.MutableBlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Map.class)
public abstract class MapMixin implements Runnable, IMap
{
    @Shadow
    private MutableBlockPos blockPos;

    @Shadow
    private FullMapData[] mapData;

    @Shadow
    private MapSettingsManager options;

    @Shadow
    private IColorManager colorManager;

    @Shadow
    private int zoom;

    @Shadow
    private int lastY;
    @Shadow
    private Minecraft game;
    @Shadow
    private MutableBlockPos tempBlockPos;

    @Shadow
    protected abstract int[] getSeafloorHeight(World world, int x, int z, int height);

    @Shadow
    protected abstract int applyHeight(int color24, boolean nether, boolean caves, World world, int multi, int startX, int startZ, int imageX, int imageY, int height, boolean solid, int layer);

    @Shadow
    protected abstract int getLight(int color24, IBlockState blockState, World world, int x, int z, int height, boolean solid);

    @Shadow
    protected abstract int getTransparentHeight(boolean nether, boolean caves, World world, int x, int z, int height);

    @Shadow
    protected abstract int doSlimeAndGrid(int color24, int mcX, int mcZ);

    @Shadow
    protected abstract int getBlockHeight(boolean nether, boolean caves, World world, int x, int z);

    /**
     * @author ACGaming
     * @reason VMFixes
     */
    @Overwrite(remap = false)
    private int getPixelColor(boolean needBiome, boolean needHeightAndID, boolean needTint, boolean needLight, boolean nether, boolean caves, World world, int multi, int startX, int startZ, int imageX, int imageY)
    {
        int surfaceHeight;
        int seafloorHeight = -1;
        int underwaterTransparentHeight = -1;
        int transparentHeight = -1;
        int foliageHeight = -1;
        int surfaceColor;
        int seafloorColor = 0;
        int transparentColor = 0;
        int foliageColor = 0;
        this.blockPos = this.blockPos.withXYZ(startX + imageX, 0, startZ + imageY);
        IBlockState blockState;
        int color24;
        int biomeID;
        if (needBiome)
        {
            if (world.getChunk(this.blockPos).isLoaded())
            {
                biomeID = Biome.getIdForBiome(world.getBiome(this.blockPos));
            }
            else
            {
                biomeID = -1;
            }
            this.mapData[this.zoom].setBiomeID(imageX, imageY, biomeID);
        }
        else
        {
            biomeID = this.mapData[this.zoom].getBiomeID(imageX, imageY);
        }
        if (this.options.biomeOverlay == 1)
        {
            if (biomeID >= 0)
            {
                color24 = BiomeRepository.getBiomeColor(biomeID) | 0xFF000000;
            }
            else
            {
                color24 = 0;
            }
            color24 = doSlimeAndGrid(color24, startX + imageX, startZ + imageY);
            return color24;
        }
        boolean blockChangeForcedTint = false;
        boolean solid = false;
        int blockStateID;
        if (needHeightAndID)
        {
            surfaceHeight = getBlockHeight(nether, caves, world, startX + imageX, startZ + imageY);
            blockState = world.getBlockState(this.blockPos.withXYZ(startX + imageX, surfaceHeight, startZ + imageY));
            if (blockState.getMaterial() != Material.SNOW) blockState = world.getBlockState(this.blockPos.withXYZ(startX + imageX, surfaceHeight - 1, startZ + imageY));
            blockState = blockState.getBlock().hasTileEntity(blockState) ? blockState.getBlock().getDefaultState() : blockState.getActualState(world, this.blockPos);
            blockStateID = BlockRepository.getStateId(blockState);
            if (this.options.biomes && blockState != this.mapData[this.zoom].getBlockstate(imageX, imageY)) blockChangeForcedTint = true;
            this.mapData[this.zoom].setHeight(imageX, imageY, surfaceHeight);
            this.mapData[this.zoom].setBlockstateID(imageX, imageY, blockStateID);
        }
        else
        {
            surfaceHeight = this.mapData[this.zoom].getHeight(imageX, imageY);
            blockStateID = this.mapData[this.zoom].getBlockstateID(imageX, imageY);
            blockState = BlockRepository.getStateById(blockStateID);
        }
        if (surfaceHeight == -1)
        {
            surfaceHeight = this.lastY + 1;
            solid = true;
        }
        if (blockState.getMaterial() == Material.LAVA) solid = false;
        if (this.options.biomes)
        {
            surfaceColor = this.colorManager.getBlockColor(this.blockPos, blockStateID, biomeID);
            int tint;
            if (needTint || blockChangeForcedTint)
            {
                tint = this.colorManager.getBiomeTint(this.mapData[this.zoom], world, blockState, blockStateID, this.blockPos, this.tempBlockPos, startX, startZ);
                this.mapData[this.zoom].setBiomeTint(imageX, imageY, tint);
            }
            else
            {
                tint = this.mapData[this.zoom].getBiomeTint(imageX, imageY);
            }
            if (tint != -1) surfaceColor = this.colorManager.colorMultiplier(surfaceColor, tint);
        }
        else
        {
            surfaceColor = this.colorManager.getBlockColorWithDefaultTint(this.blockPos, blockStateID);
        }
        surfaceColor = applyHeight(surfaceColor, nether, caves, world, multi, startX, startZ, imageX, imageY, surfaceHeight, solid, 1);
        int light;
        if (needLight)
        {
            light = getLight(surfaceColor, blockState, world, startX + imageX, startZ + imageY, surfaceHeight, solid);
            this.mapData[this.zoom].setLight(imageX, imageY, light);
        }
        else
        {
            light = this.mapData[this.zoom].getLight(imageX, imageY);
        }
        if (light == 0)
        {
            surfaceColor = 0;
        }
        else if (light != 255)
        {
            surfaceColor = this.colorManager.colorMultiplier(surfaceColor, light);
        }
        if (this.options.waterTransparency)
        {
            Material material = blockState.getMaterial();
            if (material == Material.WATER || material == Material.ICE)
            {
                if (needHeightAndID)
                {
                    int[] underwaterHeights = getSeafloorHeight(world, startX + imageX, startZ + imageY, surfaceHeight);
                    seafloorHeight = underwaterHeights[0];
                    underwaterTransparentHeight = underwaterHeights[1];
                    this.blockPos.setXYZ(startX + imageX, seafloorHeight - 1, startZ + imageY);
                    blockState = world.getBlockState(this.blockPos);
                    blockState = blockState.getBlock().hasTileEntity(blockState) ? blockState.getBlock().getDefaultState() : blockState.getActualState(world, this.blockPos);
                    if (blockState.getMaterial() == Material.WATER) blockState = BlockRepository.air.getDefaultState();
                    blockStateID = BlockRepository.getStateId(blockState);
                    if (this.options.biomes && blockState != this.mapData[this.zoom].getOceanFloorBlockstate(imageX, imageY)) blockChangeForcedTint = true;
                    this.mapData[this.zoom].setOceanFloorHeight(imageX, imageY, seafloorHeight);
                    this.mapData[this.zoom].setOceanFloorBlockstateID(imageX, imageY, blockStateID);
                }
                else
                {
                    seafloorHeight = this.mapData[this.zoom].getOceanFloorHeight(imageX, imageY);
                    blockStateID = this.mapData[this.zoom].getOceanFloorBlockstateID(imageX, imageY);
                    blockState = BlockRepository.getStateById(blockStateID);
                }
                if (this.options.biomes)
                {
                    seafloorColor = this.colorManager.getBlockColor(this.blockPos, blockStateID, biomeID);
                    int tint;
                    if (needTint || blockChangeForcedTint)
                    {
                        tint = this.colorManager.getBiomeTint(this.mapData[this.zoom], world, blockState, blockStateID, this.blockPos, this.tempBlockPos, startX, startZ);
                        this.mapData[this.zoom].setOceanFloorBiomeTint(imageX, imageY, tint);
                    }
                    else
                    {
                        tint = this.mapData[this.zoom].getOceanFloorBiomeTint(imageX, imageY);
                    }
                    if (tint != -1) seafloorColor = this.colorManager.colorMultiplier(seafloorColor, tint);
                }
                else
                {
                    seafloorColor = this.colorManager.getBlockColorWithDefaultTint(this.blockPos, blockStateID);
                }
                seafloorColor = applyHeight(seafloorColor, nether, caves, world, multi, startX, startZ, imageX, imageY, seafloorHeight, solid, 0);
                int seafloorLight;
                if (needLight)
                {
                    seafloorLight = getLight(seafloorColor, blockState, world, startX + imageX, startZ + imageY, seafloorHeight, solid);
                    this.blockPos.setXYZ(startX + imageX, seafloorHeight, startZ + imageY);
                    blockState = world.getBlockState(this.blockPos);
                    Material materialAbove = blockState.getMaterial();
                    if (this.options.lightmap && materialAbove == Material.ICE)
                    {
                        int multiplier = 255;
                        if (this.game.gameSettings.ambientOcclusion == 1)
                        {
                            multiplier = 200;
                        }
                        else if (this.game.gameSettings.ambientOcclusion == 2)
                        {
                            multiplier = 120;
                        }
                        seafloorLight = this.colorManager.colorMultiplier(seafloorLight, 0xFF000000 | multiplier << 16 | multiplier << 8 | multiplier);
                    }
                    this.mapData[this.zoom].setOceanFloorLight(imageX, imageY, seafloorLight);
                }
                else
                {
                    seafloorLight = this.mapData[this.zoom].getOceanFloorLight(imageX, imageY);
                }
                if (seafloorLight == 0)
                {
                    seafloorColor = 0;
                }
                else if (seafloorLight != 255)
                {
                    seafloorColor = this.colorManager.colorMultiplier(seafloorColor, seafloorLight);
                }
            }
        }
        if (this.options.blockTransparency)
        {
            if (needHeightAndID)
            {
                transparentHeight = getTransparentHeight(nether, caves, world, startX + imageX, startZ + imageY, surfaceHeight);
                if (transparentHeight == -1 && this.options.waterTransparency && underwaterTransparentHeight > 0) transparentHeight = underwaterTransparentHeight;
                if (transparentHeight != -1)
                {
                    this.blockPos.setXYZ(startX + imageX, transparentHeight - 1, startZ + imageY);
                    blockState = world.getBlockState(this.blockPos);
                    blockState = blockState.getBlock().hasTileEntity(blockState) ? blockState.getBlock().getDefaultState() : blockState.getActualState(world, this.blockPos);
                }
                else
                {
                    blockState = BlockRepository.air.getDefaultState();
                }
                blockStateID = BlockRepository.getStateId(blockState);
                if (this.options.biomes && blockState != this.mapData[this.zoom].getTransparentBlockstate(imageX, imageY)) blockChangeForcedTint = true;
                this.mapData[this.zoom].setTransparentHeight(imageX, imageY, transparentHeight);
                this.mapData[this.zoom].setTransparentBlockstateID(imageX, imageY, blockStateID);
            }
            else
            {
                transparentHeight = this.mapData[this.zoom].getTransparentHeight(imageX, imageY);
                blockStateID = this.mapData[this.zoom].getTransparentBlockstateID(imageX, imageY);
                blockState = BlockRepository.getStateById(blockStateID);
            }
            if (blockState != null && blockState != BlockRepository.air.getDefaultState())
            {
                if (this.options.biomes)
                {
                    transparentColor = this.colorManager.getBlockColor(this.blockPos, blockStateID, biomeID);
                    int tint;
                    if (needTint || blockChangeForcedTint)
                    {
                        tint = this.colorManager.getBiomeTint(this.mapData[this.zoom], world, blockState, blockStateID, this.blockPos, this.tempBlockPos, startX, startZ);
                        this.mapData[this.zoom].setTransparentBiomeTint(imageX, imageY, tint);
                    }
                    else
                    {
                        tint = this.mapData[this.zoom].getTransparentBiomeTint(imageX, imageY);
                    }
                    if (tint != -1) transparentColor = this.colorManager.colorMultiplier(transparentColor, tint);
                }
                else
                {
                    transparentColor = this.colorManager.getBlockColorWithDefaultTint(this.blockPos, blockStateID);
                }
                transparentColor = applyHeight(transparentColor, nether, caves, world, multi, startX, startZ, imageX, imageY, transparentHeight, solid, 3);
                int transparentLight;
                if (needLight)
                {
                    transparentLight = getLight(transparentColor, blockState, world, startX + imageX, startZ + imageY, transparentHeight, solid);
                    this.mapData[this.zoom].setTransparentLight(imageX, imageY, transparentLight);
                }
                else
                {
                    transparentLight = this.mapData[this.zoom].getTransparentLight(imageX, imageY);
                }
                if (transparentLight == 0)
                {
                    transparentColor = 0;
                }
                else if (transparentLight != 255)
                {
                    transparentColor = this.colorManager.colorMultiplier(transparentColor, transparentLight);
                }
            }
            if (needHeightAndID)
            {
                IBlockState foliageBlockState = null;
                if (transparentHeight != surfaceHeight + 1)
                {
                    foliageHeight = surfaceHeight + 1;
                    this.blockPos.setXYZ(startX + imageX, foliageHeight - 1, startZ + imageY);
                    foliageBlockState = world.getBlockState(this.blockPos);
                    foliageBlockState = foliageBlockState.getBlock().hasTileEntity(foliageBlockState) ? foliageBlockState.getBlock().getDefaultState() : foliageBlockState.getActualState(world, this.blockPos);
                    Material material = foliageBlockState.getMaterial();
                    if (material == Material.SNOW || material == Material.AIR || material == Material.LAVA) foliageHeight = -1;
                    if (foliageBlockState == blockState) foliageHeight = -1;
                }
                if (foliageHeight == -1 && this.options.waterTransparency && seafloorHeight > 0 && transparentHeight != seafloorHeight)
                {
                    foliageHeight = seafloorHeight + 1;
                    this.blockPos.setXYZ(startX + imageX, foliageHeight - 1, startZ + imageY);
                    foliageBlockState = world.getBlockState(this.blockPos);
                    foliageBlockState = foliageBlockState.getBlock().hasTileEntity(foliageBlockState) ? foliageBlockState.getBlock().getDefaultState() : foliageBlockState.getActualState(world, this.blockPos);
                    Material material = foliageBlockState.getMaterial();
                    if (material == Material.AIR || material == Material.LAVA || material == Material.WATER || material == Material.ICE) foliageHeight = -1;
                    if (foliageBlockState == blockState) foliageHeight = -1;
                }
                if (foliageHeight != -1)
                {
                    blockState = foliageBlockState;
                }
                else
                {
                    blockState = BlockRepository.air.getDefaultState();
                }
                blockStateID = BlockRepository.getStateId(blockState);
                if (this.options.biomes && blockState != this.mapData[this.zoom].getFoliageBlockstate(imageX, imageY)) blockChangeForcedTint = true;
                this.mapData[this.zoom].setFoliageHeight(imageX, imageY, foliageHeight);
                this.mapData[this.zoom].setFoliageBlockstateID(imageX, imageY, blockStateID);
            }
            else
            {
                foliageHeight = this.mapData[this.zoom].getFoliageHeight(imageX, imageY);
                blockStateID = this.mapData[this.zoom].getFoliageBlockstateID(imageX, imageY);
                blockState = BlockRepository.getStateById(blockStateID);
            }
            if (blockState != null && blockState != BlockRepository.air.getDefaultState())
            {
                if (this.options.biomes)
                {
                    foliageColor = this.colorManager.getBlockColor(this.blockPos, blockStateID, biomeID);
                    int tint;
                    if (needTint || blockChangeForcedTint)
                    {
                        tint = this.colorManager.getBiomeTint(this.mapData[this.zoom], world, blockState, blockStateID, this.blockPos, this.tempBlockPos, startX, startZ);
                        this.mapData[this.zoom].setFoliageBiomeTint(imageX, imageY, tint);
                    }
                    else
                    {
                        tint = this.mapData[this.zoom].getFoliageBiomeTint(imageX, imageY);
                    }
                    if (tint != -1) foliageColor = this.colorManager.colorMultiplier(foliageColor, tint);
                }
                else
                {
                    foliageColor = this.colorManager.getBlockColorWithDefaultTint(this.blockPos, blockStateID);
                }
                foliageColor = applyHeight(foliageColor, nether, caves, world, multi, startX, startZ, imageX, imageY, foliageHeight, solid, 2);
                int foliageLight;
                if (needLight)
                {
                    foliageLight = getLight(foliageColor, blockState, world, startX + imageX, startZ + imageY, foliageHeight, solid);
                    this.mapData[this.zoom].setFoliageLight(imageX, imageY, foliageLight);
                }
                else
                {
                    foliageLight = this.mapData[this.zoom].getFoliageLight(imageX, imageY);
                }
                if (foliageLight == 0)
                {
                    foliageColor = 0;
                }
                else if (foliageLight != 255)
                {
                    foliageColor = this.colorManager.colorMultiplier(foliageColor, foliageLight);
                }
            }
        }
        if (seafloorHeight > 0)
        {
            color24 = seafloorColor;
            if (foliageColor != 0 && foliageHeight <= surfaceHeight) color24 = this.colorManager.colorAdder(foliageColor, color24);
            if (transparentColor != 0 && transparentHeight <= surfaceHeight) color24 = this.colorManager.colorAdder(transparentColor, color24);
            color24 = this.colorManager.colorAdder(surfaceColor, color24);
        }
        else
        {
            color24 = surfaceColor;
        }
        if (foliageColor != 0 && foliageHeight > surfaceHeight) color24 = this.colorManager.colorAdder(foliageColor, color24);
        if (transparentColor != 0 && transparentHeight > surfaceHeight) color24 = this.colorManager.colorAdder(transparentColor, color24);
        if (this.options.biomeOverlay == 2)
        {
            int bc = 0;
            if (biomeID >= 0) bc = BiomeRepository.getBiomeColor(biomeID);
            bc = 0x7F000000 | bc;
            color24 = this.colorManager.colorAdder(bc, color24);
        }
        color24 = doSlimeAndGrid(color24, startX + imageX, startZ + imageY);
        return color24;
    }
}