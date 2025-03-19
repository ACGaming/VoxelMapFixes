package mod.acgaming.vmfixes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies = "required-after:mixinbooter;after:voxelmap;after:tombmanygraves")
public class VMFixes
{
    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (FMLLaunchHandler.side().isClient() && !Loader.isModLoaded("voxelmap"))
        {
            LOGGER.fatal("Hey, where's my VoxelMap?!");
        }
        else
        {
            LOGGER.info("{} initialized", Tags.MOD_NAME);
            if (Loader.isModLoaded("tombmanygraves"))
            {
                LOGGER.info("Mod support for {} enabled", Loader.instance().getIndexedModList().get("tombmanygraves").getName());
            }
        }
    }
}