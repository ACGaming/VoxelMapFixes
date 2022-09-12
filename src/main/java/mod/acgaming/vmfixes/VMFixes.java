package mod.acgaming.vmfixes;

import java.awt.image.BufferedImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = VMFixes.MODID, name = VMFixes.NAME, version = VMFixes.VERSION, acceptedMinecraftVersions = "[1.12.2]", dependencies = "required-after:mixinbooter;required-after:voxelmap", clientSideOnly = true)
public class VMFixes
{
    public static final String MODID = "vmfixes";
    public static final String NAME = "VoxelMap Fixes";
    public static final String VERSION = "1.12.2-1.0.6";
    public static final Logger LOGGER = LogManager.getLogger();

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
        LOGGER.info("VoxelMap Fixes initialized");
    }
}