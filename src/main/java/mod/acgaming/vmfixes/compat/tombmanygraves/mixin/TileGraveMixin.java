package mod.acgaming.vmfixes.compat.tombmanygraves.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

import com.m4thg33k.tombmanygraves.network.TMGNetwork;
import com.m4thg33k.tombmanygraves.tiles.TileGrave;
import mod.acgaming.vmfixes.compat.tombmanygraves.RemoveDeathpointPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileGrave.class)
public abstract class TileGraveMixin extends TileEntity
{
    @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(Lnet/minecraft/util/math/BlockPos;)Z"))
    public void vmfOnGraveCollision(EntityPlayer player, CallbackInfo ci)
    {
        if (player instanceof EntityPlayerMP)
        {
            TMGNetwork.sendTo(new RemoveDeathpointPacket(this.pos), (EntityPlayerMP) player);
        }
    }
}