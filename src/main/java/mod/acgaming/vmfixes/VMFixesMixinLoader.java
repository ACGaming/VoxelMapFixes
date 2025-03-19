package mod.acgaming.vmfixes;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.Loader;

import java.util.List;
import zone.rong.mixinbooter.ILateMixinLoader;

@SuppressWarnings("unused")
public class VMFixesMixinLoader implements ILateMixinLoader
{
    @Override
    public List<String> getMixinConfigs()
    {
        return Lists.newArrayList("mixins.vmfixes.json", "mixins.vmfixes.tombmanygraves.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig)
    {
        if (mixinConfig.equals("mixins.vmfixes.json"))
        {
            return Loader.isModLoaded("voxelmap");
        }
        if (mixinConfig.equals("mixins.vmfixes.tombmanygraves.json"))
        {
            return Loader.isModLoaded("tombmanygraves");
        }
        return true;
    }
}