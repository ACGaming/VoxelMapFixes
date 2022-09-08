package mod.acgaming.vmfixes.mixin;

import java.awt.image.BufferedImage;

import net.minecraft.block.Block;
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
import mod.acgaming.vmfixes.color.ColorHelper;
import mod.acgaming.vmfixes.color.IntList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ColorManager.class)
public abstract class ColorManagerMixin
{
    @Shadow
    Minecraft game;

    @Inject(method = "getColor", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getColor(MutableBlockPos blockPos, IBlockState blockState, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.configBlocked(blockPos)) cir.setReturnValue(452984832);
    }

    @Inject(method = "getBlockColorWithDefaultTint", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getBlockColorWithDefaultTint(MutableBlockPos blockPos, int blockStateID, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.configBlocked(blockPos)) cir.setReturnValue(0);
    }

    @Inject(method = "getBlockColor(Lcom/mamiyaotaru/voxelmap/util/MutableBlockPos;I)I", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getBlockColorI(MutableBlockPos blockPos, int blockStateID, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.configBlocked(blockPos)) cir.setReturnValue(0);
    }

    @Inject(method = "getBlockColor(Lcom/mamiyaotaru/voxelmap/util/MutableBlockPos;II)I", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getBlockColorII(MutableBlockPos blockPos, int blockStateID, int biomeID, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.configBlocked(blockPos)) cir.setReturnValue(0);
    }

    @Inject(method = "checkForBiomeTinting", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_checkForBiomeTinting(MutableBlockPos blockPos, IBlockState blockState, int color, CallbackInfo ci)
    {
        if (VMFixes.configBlocked(blockPos)) ci.cancel();
    }

    @Inject(method = "tintFromFakePlacedBlock", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_tintFromFakePlacedBlock(IBlockState blockState, MutableBlockPos loopBlockPos, byte biomeID, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.configBlocked(loopBlockPos)) cir.setReturnValue(-1);
    }

    @Inject(method = "getCustomBlockBiomeTintFromUnloadedChunk", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_getCustomBlockBiomeTintFromUnloadedChunk(AbstractMapData mapData, World world, IBlockState blockState, MutableBlockPos blockPos, MutableBlockPos loopBlockPos, int startX, int startZ, CallbackInfoReturnable<Integer> cir)
    {
        if (VMFixes.configBlocked(blockPos)) cir.setReturnValue(-1);
    }

    @Inject(method = "createTintTable", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void VMFixes_createTintTable(IBlockState blockState, MutableBlockPos loopBlockPos, CallbackInfo ci)
    {
        if (VMFixes.configBlocked(loopBlockPos)) ci.cancel();
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

    /**
     * @author ACGaming
     * @reason VMFixes
     */
    @Overwrite(remap = false)
    private int getColorForBlockPosBlockStateAndFacing(BlockPos blockPos, IBlockState blockState, EnumFacing facing)
    {
        int color = 452984832;
        if (VMFixes.configBlocked((MutableBlockPos) blockPos)) return color;
        try
        {
            EnumBlockRenderType blockRenderType = blockState.getRenderType();
            BlockRendererDispatcher blockRendererDispatcher = this.game.getBlockRendererDispatcher();

            if (blockRenderType == EnumBlockRenderType.LIQUID)
            {
                return color = getColorForTerrainSprite(blockState, blockRendererDispatcher);
            }

            TextureAtlasSprite icon = blockRendererDispatcher.getModelForState(blockState).getQuads(blockState, facing, 0).get(0).getSprite();
            return getColorForIcon(icon);
        }
        catch (Exception ignored) {}
        return color;
    }

    /**
     * @author ACGaming, sblectric
     * @reason VMFixes
     */
    @Overwrite(remap = false)
    private int getColorForIcon(TextureAtlasSprite icon)
    {
        try
        {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            IntList colors = new IntList();
            int[] aint = icon.getFrameTextureData(0)[0];
            for (int x = 0; x < w; x++)
            {
                for (int y = 0; y < h; y++)
                {
                    int c = aint[x + y * h];
                    if (c == 0) continue;
                    colors.add(c);
                }
            }
            return ColorHelper.averageColors(colors);
        }
        catch (Exception e)
        {
            VMFixes.LOGGER.warn("Error getting color from icon " + icon);
            return 452984832;
        }
    }

    /**
     * @author ACGaming
     * @reason VMFixes
     */
    @Overwrite(remap = false)
    private void loadTexturePackTerrainImage() {}
}