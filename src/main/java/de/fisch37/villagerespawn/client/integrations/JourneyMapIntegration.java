package de.fisch37.villagerespawn.client.integrations;

import de.fisch37.villagerespawn.VillageIdentifier;
import de.fisch37.villagerespawn.packets.NewVillageEnteredPacket;
import de.fisch37.villagerespawn.packets.VisitedVillagesPacket;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.display.WaypointGroup;
import journeymap.client.api.event.ClientEvent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

import static de.fisch37.villagerespawn.VillageRespawn.MOD_ID;
import static de.fisch37.villagerespawn.VillageRespawn.LOG;

public class JourneyMapIntegration implements MinimapIntegration {
    private IClientAPI api;
    private WaypointGroup group;

    public class JourneyMapInner implements IClientPlugin {
        @Override
        public void initialize(IClientAPI jmClientApi) {
            api = jmClientApi;
            group = new WaypointGroup(MOD_ID, "villages", "Villages");
            LOG.info("Added Journey Map Integration!");
        }

        @Override
        public void onEvent(ClientEvent event) {

        }

        @Override
        public String getModId() {
            return MOD_ID;
        }
    }

    @Override
    public void addVisitedVillages(
            VisitedVillagesPacket packet,
            ClientPlayerEntity player,
            PacketSender responseSender
    ) {
        packet.villages().forEach(this::addVillageWaypoint);
    }

    @Override
    public void addNewVillage(
            NewVillageEnteredPacket newVillageEnteredPacket,
            ClientPlayerEntity clientPlayerEntity,
            PacketSender packetSender
    ) {
        addVillageWaypoint(newVillageEnteredPacket.village());
    }

    private static String generateVillageID(VillageIdentifier village) {
        String world = village.location().world().getValue().toString();
        BlockPos pos = village.location().pos();
        return String.format(
                "%s-%d-%d-%d-%s",
                world,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                village.name()
        );
    }

    public void addVillageWaypoint(VillageIdentifier village) {
        if (!api.playerAccepts(MOD_ID, DisplayType.Waypoint)) {
            LOG.warn("Tried to add village waypoint but wasn't allowed to :(");
            return;
        }

        Waypoint waypoint = new Waypoint(
                MOD_ID,
                generateVillageID(village),
                village.translated(),
                village.location().world(),
                village.getCenter()
        );
        waypoint.setGroup(group);
        waypoint.setEditable(false);
        waypoint.setPersistent(false);
        try {
            api.show(waypoint);
        } catch (Exception e) {
            LOG.error("Failed to add village waypoint: {}", e.getMessage());
        }
    }
}
