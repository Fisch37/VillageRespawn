package de.fisch37.villagerespawn.client;

import de.fisch37.villagerespawn.VillageIdentifier;
import de.fisch37.villagerespawn.packets.NewVillageEnteredPacket;
import de.fisch37.villagerespawn.packets.VisitedVillagesPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointsManager;

import java.util.Hashtable;

import static de.fisch37.villagerespawn.VillageRespawn.MOD_ID;

public class XaerosIntegration extends MinimapIntegration {
    private final Hashtable<Integer, Waypoint> xaerosCustomWaypointsHook;

    public XaerosIntegration() {
        xaerosCustomWaypointsHook = WaypointsManager.getCustomWaypoints(MOD_ID);
        initialize();

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clearAllWaypoints());
    }

    @Override
    public void addNewVillage(
            NewVillageEnteredPacket packet,
            ClientPlayerEntity player,
            PacketSender responseSender
    ) {
        addVillage(packet.village());
    }

    @Override
    public void addVisitedVillages(
            VisitedVillagesPacket packet,
            ClientPlayerEntity player,
            PacketSender responseSender
    ) {
        packet.villages().forEach(this::addVillage);
    }

    private void addVillage(VillageIdentifier village) {
        BlockPos center = village.getCenter();
        xaerosCustomWaypointsHook.put(
                village.id(),
                new Waypoint(
                        center.getX(),
                        center.getY(),
                        center.getZ(),
                        village.name(),
                        "V",
                        2,
                        0,  // Don't disappear on arrival and be visible
                        true  // Don't persist across client sessions
                )
        );
    }

    private void clearAllWaypoints() {
        xaerosCustomWaypointsHook.clear();
    }
}
