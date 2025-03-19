package mod.acgaming.vmfixes.mixin;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.mamiyaotaru.voxelmap.ColorManager;
import com.mamiyaotaru.voxelmap.util.GLUtils;
import com.mamiyaotaru.voxelmap.util.ImageUtils;
import com.mamiyaotaru.voxelmap.util.MutableBlockPos;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ColorManager.class)
public abstract class ColorManagerMixin
{
    @Shadow(remap = false)
    Minecraft game;

    @Inject(method = "createTintTable", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void vmfCreateTintTable(IBlockState blockState, MutableBlockPos loopBlockPos, CallbackInfo ci)
    {
        ci.cancel();
    }

    @Inject(method = "tintFromFakePlacedBlock", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void vmfTintFromFakePlacedBlock(IBlockState blockState, MutableBlockPos loopBlockPos, byte biomeID, CallbackInfoReturnable<Integer> cir)
    {
        cir.setReturnValue(-1);
    }

    @Inject(method = "loadTexturePackTerrainImage", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void vmfLoadTexturePackTerrainImage(CallbackInfo ci)
    {
        ci.cancel();
    }

    /**
     * @author ACGaming
     * @reason VMFixes
     */
    @Overwrite(remap = false)
    public final BufferedImage getBlockImage(IBlockState blockState, ItemStack stack, World world)
    {
        BufferedImage blockImage = null;
        try
        {
            if (GLUtils.fboEnabled)
            {
                IBakedModel model = this.game.getRenderItem().getItemModelWithOverrides(stack, world, null);
                drawModel(1.0F, 2, EnumFacing.EAST, blockState, model, stack);
                blockImage = ImageUtils.createBufferedImageFromGLID(GLUtils.fboTextureID);
                blockImage = ImageUtils.trimCentered(blockImage);
            }
        }
        catch (Exception ignored) {}
        return blockImage;
    }

    @Shadow(remap = false)
    protected abstract int getColorForTerrainSprite(IBlockState blockState, BlockRendererDispatcher blockRendererDispatcher);

    @Shadow(remap = false)
    protected abstract void drawModel(float scale, int captureDepth, EnumFacing facing, IBlockState blockState, IBakedModel model, ItemStack stack);

    /**
     * @author ACGaming
     * @reason VMFixes
     */
    @Overwrite(remap = false)
    private int getColorForBlockPosBlockStateAndFacing(BlockPos blockPos, IBlockState blockState, EnumFacing facing)
    {
        TextureAtlasSprite icon = null;
        try
        {
            EnumBlockRenderType blockRenderType = blockState.getRenderType();
            BlockRendererDispatcher blockRendererDispatcher = this.game.getBlockRendererDispatcher();
            if (blockRenderType == EnumBlockRenderType.LIQUID)
            {
                return getColorForTerrainSprite(blockState, blockRendererDispatcher);
            }
            if (!blockState.getBlock().hasTileEntity(blockState))
            {
                blockState = blockState.getActualState(this.game.world, blockPos);
                List<BakedQuad> quads = blockRendererDispatcher.getModelForState(blockState).getQuads(blockState, facing, 0);
                if (!quads.isEmpty())
                {
                    return getColorForIcon(quads.get(0).getSprite());
                }
            }
            icon = blockRendererDispatcher.getBlockModelShapes().getTexture(blockState);
        }
        catch (Exception ignored) {}
        if (icon == null)
        {
            MapColor mapColor = blockState.getMaterial().getMaterialMapColor();
            int index = mapColor.colorIndex;
            return mapColor.getMapColor(index);
        }
        return getColorForIcon(icon);
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
            BufferedImage iconBuff = vmfixes$getBufferedImage(icon);
            if (iconBuff == null) return color;
            Image singlePixel = iconBuff.getScaledInstance(1, 1, 4);
            BufferedImage singlePixelBuff = new BufferedImage(1, 1, iconBuff.getType());
            Graphics gfx = singlePixelBuff.createGraphics();
            gfx.drawImage(singlePixel, 0, 0, null);
            gfx.dispose();
            color = singlePixelBuff.getRGB(0, 0);
        }
        catch (Exception ignored) {}
        return color;
    }

    // Courtesy of mezz
    @Unique
    private BufferedImage vmfixes$getBufferedImage(TextureAtlasSprite textureAtlasSprite)
    {
        final int iconWidth = textureAtlasSprite.getIconWidth();
        final int iconHeight = textureAtlasSprite.getIconHeight();
        final int frameCount = textureAtlasSprite.getFrameCount();
        if (iconWidth <= 0 || iconHeight <= 0 || frameCount <= 0)
        {
            return null;
        }
        BufferedImage bufferedImage = new BufferedImage(iconWidth, iconHeight * frameCount, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < frameCount; i++)
        {
            int[][] frameTextureData = textureAtlasSprite.getFrameTextureData(i);
            int[] largestMipMapTextureData = frameTextureData[0];
            bufferedImage.setRGB(0, i * iconHeight, iconWidth, iconHeight, largestMipMapTextureData, 0, iconWidth);
        }
        return bufferedImage;
    }
}