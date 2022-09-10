package mod.acgaming.vmfixes;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import com.mamiyaotaru.voxelmap.util.MutableBlockPos;
import mod.acgaming.vmfixes.config.VMFixesConfig;

@Mod(modid = VMFixes.MODID,
    name = VMFixes.NAME,
    version = VMFixes.VERSION,
    acceptedMinecraftVersions = "[1.12.2]",
    dependencies = "required-after:mixinbooter;required-after:voxelmap",
    clientSideOnly = true)
public class VMFixes
{
    public static final String MODID = "vmfixes";
    public static final String NAME = "VoxelMap Fixes";
    public static final String VERSION = "1.12.2-1.0.5";
    public static final Logger LOGGER = LogManager.getLogger();

    public static List<String> teList;
    public static List<String> blockList;

    public static boolean configBlocked(MutableBlockPos blockPos)
    {
        Block block = Minecraft.getMinecraft().world.getBlockState(blockPos).getBlock();
        ResourceLocation resLoc = Block.REGISTRY.getNameForObject(block);
        if (Minecraft.getMinecraft().world.getTileEntity(blockPos) != null)
        {
            return VMFixes.teList.contains(resLoc.getNamespace() + ":*") || VMFixes.teList.contains(resLoc.toString());
        }
        else
        {
            return VMFixes.blockList.contains(resLoc.getNamespace() + ":*") || VMFixes.blockList.contains(resLoc.toString());
        }
    }

    // Courtesy of mezz
    public static BufferedImage getBufferedImage(TextureAtlasSprite textureAtlasSprite)
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

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        teList = Arrays.asList(VMFixesConfig.a_skippedTileEntities);
        blockList = Arrays.asList(VMFixesConfig.b_skippedBlocks);
        LOGGER.info("VoxelMap Fixes initialized");
    }
}