package mod.acgaming.vmfixes.compat.tombmanygraves.mixin;

import com.m4thg33k.tombmanygraves.network.TMGNetwork;
import mod.acgaming.vmfixes.compat.tombmanygraves.RemoveDeathpointPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TMGNetwork.class)
public abstract class TMGNetworkMixin
{
    @Inject(method = "setup", at = @At(value = "TAIL"), remap = false)
    private static void vmfRegisterPacket(CallbackInfo ci)
    {
        TMGNetwork.registerPacketClient(RemoveDeathpointPacket.class);
    }
}