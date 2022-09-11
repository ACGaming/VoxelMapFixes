package mod.acgaming.vmfixes.config;

import java.util.Arrays;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import mod.acgaming.vmfixes.VMFixes;

@Config(modid = VMFixes.MODID, name = "voxelmap_fixes")
public class VMFixesConfig
{
    @Config.Name("A) Skipped Tile Entities")
    @Config.Comment(
        {
            "List of mod IDs whose tile entities are skipped.",
            "Use this first when you notice errors in log files.",
            "You can use specific block names (mod_id:block) or wildcards (mod_id:*)."
        })
    public static String[] a_skippedTileEntities = new String[] {};

    @Config.Name("B) Skipped Blocks")
    @Config.Comment(
        {
            "List of mod IDs whose blocks are skipped.",
            "Add to this as well if skipping tile entities alone doesn't work.",
            "You can use specific block names (mod_id:block) or wildcards (mod_id:*)."
        })
    public static String[] b_skippedBlocks = new String[] {};

    @Mod.EventBusSubscriber(modid = VMFixes.MODID)
    public static class EventHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(VMFixes.MODID))
            {
                ConfigManager.sync(VMFixes.MODID, Config.Type.INSTANCE);
                VMFixes.teList = Arrays.asList(VMFixesConfig.a_skippedTileEntities);
                VMFixes.blockList = Arrays.asList(VMFixesConfig.b_skippedBlocks);
                VMFixes.LOGGER.info("VoxelMap Fixes config reloaded");
            }
        }
    }
}