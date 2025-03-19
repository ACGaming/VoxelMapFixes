package mod.acgaming.vmfixes.compat.tombmanygraves;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;

import com.m4thg33k.tombmanygraves.network.BaseThreadsafePacket;
import com.m4thg33k.tombmanygraves.util.LogHelper;
import com.mamiyaotaru.voxelmap.VoxelMap;
import com.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import io.netty.buffer.ByteBuf;
import mod.acgaming.vmfixes.VMFixes;

public class RemoveDeathpointPacket extends BaseThreadsafePacket
{
    private BlockPos pos;

    public RemoveDeathpointPacket() {}

    public RemoveDeathpointPacket(BlockPos pos)
    {
        this.pos = pos;
    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler)
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
            VMFixes.LOGGER.info("Grave at {} retrieved, deathpoint '{}' deleted", this.pos, toDel.name);
        }
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler)
    {
        LogHelper.error("Attempting to handle remove deathpoint packet on server!");
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.pos = this.readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        this.writePos(this.pos, buf);
    }
}