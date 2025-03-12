package mod.acgaming.vmfixes;

import java.util.ArrayList;
import java.util.List;
import zone.rong.mixinbooter.ILateMixinLoader;

public class VMFixesMixinLoader implements ILateMixinLoader
{
    @Override
    public List<String> getMixinConfigs()
    {
        List<String> mixins = new ArrayList<>();
        mixins.add("mixins.vmfixes.json");
        return mixins;
    }
}