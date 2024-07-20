package de.fisch37.villagerespawn.client.integrations;

import com.mamiyaotaru.voxelmap.VoxelConstants;
import com.mamiyaotaru.voxelmap.VoxelMap;
import com.mamiyaotaru.voxelmap.WaypointManager;
import com.mamiyaotaru.voxelmap.util.DimensionContainer;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import de.fisch37.villagerespawn.VillageIdentifier;
import de.fisch37.villagerespawn.VillageRespawn;
import de.fisch37.villagerespawn.packets.NewVillageEnteredPacket;
import de.fisch37.villagerespawn.packets.VisitedVillagesPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Hashtable;

import static de.fisch37.villagerespawn.VillageRespawn.LOG;

public class VoxelIntegration implements MinimapIntegration {
    private final VoxelMap voxel;
    private final Hashtable<Integer, Waypoint> referenced = new Hashtable<>();

    public VoxelIntegration() {
        voxel = VoxelConstants.getVoxelMapInstance();
    }

    @Override
    public void addNewVillage(NewVillageEnteredPacket packet, ClientPlayerEntity player, PacketSender response) {
        addVillage(packet.village());
    }

    @Override
    public void addVisitedVillages(VisitedVillagesPacket packet, ClientPlayerEntity player, PacketSender response) {
        packet.villages().forEach(this::addVillage);
    }

    private void addVillage(VillageIdentifier village) {
        DimensionContainer dimension = voxel.getDimensionManager().getDimensionContainerByResourceLocation(
                village.location()
                        .world()
                        .getValue()
        );

        BlockPos pos = village.getCenter();
        Waypoint waypoint = new Waypoint(
                village.name(),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                true,
                1,
                1,
                1,
                "(" + VillageRespawn.class.getName() + ")",
                "randomNonsenseGoooooo",
                new java.util.TreeSet<>(java.util.Set.of(dimension))
        );
        boolean isNew = referenced.putIfAbsent(village.id(), waypoint) == null;
        if (isNew)
            voxel.getWaypointManager().addWaypoint(waypoint);
        else
            LOG.warn("Received duplicate village id {} {}", village.id(), village.name());
    }
}
