package mod.acgaming.vmfixes.compat.tombmanygraves.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import com.m4thg33k.tombmanygraves.tiles.TileGrave;
import com.mamiyaotaru.voxelmap.VoxelMap;
import com.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileGrave.class, remap = false)
public abstract class TileGraveMixin extends TileEntity
{
    @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(Lnet/minecraft/util/math/BlockPos;)Z"))
    public void vmfOnGraveCollision(EntityPlayer player, CallbackInfo ci)
    {
        IWaypointManager waypointManager = VoxelMap.getInstance().getWaypointManager();
        Waypoint toDel = null;
        for (Waypoint pt : waypointManager.getWaypoints())
        {
            if ((pt.name.equals("Latest Death") || pt.name.contains("Previous Death")) && pt.getX() == this.pos.getX() && pt.getZ() == this.pos.getZ())
            {
                toDel = pt;
                break;
            }
        }
        if (toDel != null)
        {
            waypointManager.deleteWaypoint(toDel);
        }
    }
}
