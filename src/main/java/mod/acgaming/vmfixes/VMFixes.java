package mod.acgaming.vmfixes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = VMFixes.MODID,
    name = VMFixes.NAME,
    version = VMFixes.VERSION,
    acceptedMinecraftVersions = "[1.12.2]",
    dependencies = "required-after:mixinbooter;required-after:voxelmap")
public class VMFixes
{
    public static final String MODID = "vmfixes";
    public static final String NAME = "VoxelMap Fixes";
    public static final String VERSION = "1.12.2-1.0.3";
    public static final Logger LOGGER = LogManager.getLogger();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        LOGGER.info("VoxelMap Fixes initialized");
    }
}