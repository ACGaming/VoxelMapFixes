package mod.acgaming.vmfixes.mixin;

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
import net.minecraft.world.chunk.Chunk;

import com.mamiyaotaru.voxelmap.ColorManager;
import com.mamiyaotaru.voxelmap.util.GLUtils;
import com.mamiyaotaru.voxelmap.util.ImageUtils;
import com.mamiyaotaru.voxelmap.util.MutableBlockPos;
import java.awt.*;
import java.awt.image.BufferedImage;
import mod.acgaming.vmfixes.VMFixes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ColorManager.class)
public abstract class ColorManagerMixin
{
    @Shadow(remap = false)
    Minecraft game;

    @Redirect(method = "getBiomeTint", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;isLoaded()Z"))
    public boolean vmfGetBiomeTint(Chunk instance)
    {
        return false;
    }

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
            VMFixes.LOGGER.warn("Error getting block armor image for {},{}: {}", Block.getIdFromBlock(blockState.getBlock()), blockState.getBlock().getMetaFromState(blockState), e.getLocalizedMessage());
            return null;
        }
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
            VMFixes.LOGGER.warn("Error getting color from TextureAtlasSprite {}", icon);
        }
        return color;
    }
}