package mod.acgaming.vmfixes.mixin;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.mamiyaotaru.voxelmap.ColorManager;
import com.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
import com.mamiyaotaru.voxelmap.util.GLUtils;
import com.mamiyaotaru.voxelmap.util.ImageUtils;
import com.mamiyaotaru.voxelmap.util.MutableBlockPos;
import mod.acgaming.vmfixes.VMFixes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ColorManager.class)
public abstract class ColorManagerMixin
{
    @Shadow
    Minecraft game;
    @Shadow
    private boolean optifineInstalled;
    @Shadow
    private HashSet<Integer> biomeTintsAvailable;
    @Shadow
    private HashMap<Integer, int[][]> blockTintTables;

    /**
     * @author ACGaming
     * @reason VMFixes
     */
    @Overwrite(remap = false)
    public int getBiomeTint(AbstractMapData mapData, World world, IBlockState blockState, int blockStateID, MutableBlockPos blockPos, MutableBlockPos loopBlockPos, int startX, int startZ)
    {
        boolean live = false;
        int tint = -2;
        if (this.optifineInstalled || (!live && this.biomeTintsAvailable.contains(blockStateID))) try
        {
            int[][] tints = this.blockTintTables.get(blockStateID);
            if (tints != null)
            {
                int r = 0;
                int g = 0;
                int b = 0;
                int t;
                for (t = blockPos.getX() - 1; t <= blockPos.getX() + 1; t++)
                {
                    int s;
                    for (s = blockPos.getZ() - 1; s <= blockPos.getZ() + 1; s++)
                    {
                        int biomeID;
                        int dataX = t - startX;
                        int dataZ = s - startZ;
                        dataX = Math.max(dataX, 0);
                        dataX = Math.min(dataX, mapData.getWidth() - 1);
                        dataZ = Math.max(dataZ, 0);
                        dataZ = Math.min(dataZ, mapData.getHeight() - 1);
                        biomeID = mapData.getBiomeID(dataX, dataZ);
                        if (biomeID == -1) biomeID = 1;
                        int biomeTint = tints[biomeID][loopBlockPos.getY() / 8];
                        r += (biomeTint & 0xFF0000) >> 16;
                        g += (biomeTint & 0xFF00) >> 8;
                        b += biomeTint & 0xFF;
                    }
                }
                tint = 0xFF000000 | (r / 9 & 0xFF) << 16 | (g / 9 & 0xFF) << 8 | b / 9 & 0xFF;
            }
        }
        catch (Exception ignored) {}
        if (tint == -2) tint = getBuiltInBiomeTint(mapData, world, blockState, blockStateID, blockPos, loopBlockPos, startX, startZ, live);
        return tint;
    }

    /**
     * @author ACGaming
     * @reason VMFixes
     */
    @Overwrite(remap = false)
    public final BufferedImage getBlockImage(IBlockState blockState, ItemStack stack, World world)
    {
        try
        {
            BufferedImage blockImage = null;
            if (GLUtils.fboEnabled)
            {
                IBakedModel model = this.game.getRenderItem().getItemModelWithOverrides(stack, world, null);
                drawModel(1.0F, 2, EnumFacing.EAST, blockState, model, stack);
                blockImage = ImageUtils.createBufferedImageFromGLID(GLUtils.fboTextureID);
                blockImage = ImageUtils.trimCentered(blockImage);
            }
            return blockImage;
        }
        catch (Exception e)
        {
            VMFixes.LOGGER.warn("Error getting block armor image for " + Block.getIdFromBlock(blockState.getBlock()) + "," + blockState.getBlock().getMetaFromState(blockState) + ": " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Shadow
    protected abstract void drawModel(float scale, int captureDepth, EnumFacing facing, IBlockState blockState, IBakedModel model, ItemStack stack);

    @Shadow
    protected abstract int getColorForTerrainSprite(IBlockState blockState, BlockRendererDispatcher blockRendererDispatcher);

    @Shadow
    protected abstract int getBuiltInBiomeTint(AbstractMapData mapData, World world, IBlockState blockState, int blockStateID, MutableBlockPos blockPos, MutableBlockPos loopBlockPos, int startX, int startZ, boolean live);

    /**
     * @author ACGaming
     * @reason VMFixes
     */
    @Overwrite(remap = false)
    private int getColorForBlockPosBlockStateAndFacing(BlockPos blockPos, IBlockState blockState, EnumFacing facing)
    {
        int color;
        try
        {
            EnumBlockRenderType blockRenderType = blockState.getRenderType();
            BlockRendererDispatcher blockRendererDispatcher = this.game.getBlockRendererDispatcher();
            if (blockRenderType == EnumBlockRenderType.LIQUID)
            {
                return getColorForTerrainSprite(blockState, blockRendererDispatcher);
            }
            else
            {
                blockState = blockState.getBlock().hasTileEntity(blockState) ? blockState.getBlock().getDefaultState() : blockState.getActualState(this.game.world, blockPos);
            }
            TextureAtlasSprite icon = blockRendererDispatcher.getModelForState(blockState).getQuads(blockState, facing, 0).get(0).getSprite();
            return getColorForIcon(icon);
        }
        catch (Exception e)
        {
            MapColor mapColor = blockState.getMaterial().getMaterialMapColor();
            int index = mapColor.colorIndex;
            color = mapColor.getMapColor(index);
        }
        return color;
    }

    /**
     * @author ACGaming
     * @reason VMFixes
     */
    @Overwrite(remap = false)
    private int getColorForIcon(TextureAtlasSprite icon)
    {
        int color = 0;
        try
        {
            BufferedImage iconBuff = VMFixes.getBufferedImage(icon);
            if (iconBuff == null) return color;
            Image singlePixel = iconBuff.getScaledInstance(1, 1, 4);
            BufferedImage singlePixelBuff = new BufferedImage(1, 1, iconBuff.getType());
            Graphics gfx = singlePixelBuff.createGraphics();
            gfx.drawImage(singlePixel, 0, 0, null);
            gfx.dispose();
            color = singlePixelBuff.getRGB(0, 0);
        }
        catch (Exception e)
        {
            VMFixes.LOGGER.warn("Error getting color from TextureAtlasSprite " + icon);
        }
        return color;
    }

    /**
     * @author ACGaming
     * @reason VMFixes
     */
    @Overwrite(remap = false)
    private void loadTexturePackTerrainImage() {}
}