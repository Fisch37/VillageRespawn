package de.fisch37.villagerespawn.client;

import de.fisch37.villagerespawn.VillageIdentifier;
import de.fisch37.villagerespawn.packets.NewVillageEnteredPacket;
import de.fisch37.villagerespawn.packets.VisitedVillagesPacket;
import de.fisch37.villagerespawn.packets.VisitedVillagesRequestPacket;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.Displayable;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.display.WaypointGroup;
import journeymap.client.api.event.ClientEvent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.BlockPos;

import static de.fisch37.villagerespawn.VillageRespawn.MOD_ID;
import static de.fisch37.villagerespawn.VillageRespawn.LOG;

public class JourneyMapIntegration extends MinimapIntegration implements IClientPlugin {
    private IClientAPI api;
    private WaypointGroup group;

    /**
     * Called by JourneyMap during the init phase of mod loading.  Your implementation
     * should retain a reference to the IClientAPI passed in, since that is what your plugin
     * will use to add overlays, etc. to JourneyMap.
     * <p>
     * This is also a good time to call {@link IClientAPI#subscribe(String, EnumSet)} to subscribe to any
     * desired ClientEvent types.
     *
     * @param jmClientApi Client API implementation
     */
    @Override
    public void initialize(IClientAPI jmClientApi) {
        api = jmClientApi;
        super.initialize();
        group = new WaypointGroup(MOD_ID, "villages", "Villages");
        LOG.info("Added Journey Map Integration!");
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

    /**
     * Used by JourneyMap to associate your mod id with your plugin instance.
     */
    @Override
    public String getModId() {
        return MOD_ID;
    }

    /**
     * Called by JourneyMap on the main Minecraft thread when a {@link ClientEvent} occurs.
     * Be careful to minimize the time spent in this method so you don't lag the game.
     * <p>
     * You must call {@link IClientAPI#subscribe(String, EnumSet)} to subscribe to these events ( preferably during
     * {@link #initialize(IClientAPI)} ), otherwise this method will never be called.
     * <p>
     * If the event type is {@link ClientEvent.Type#DISPLAY_UPDATE},
     * this is a signal to {@link IClientAPI#show(Displayable)}
     * all relevant Displayables for the {@link ClientEvent#dimension} indicated.
     * (Note: ModWaypoints with persisted==true will already be shown.)
     *
     * @param event the event
     */
    @Override
    public void onEvent(ClientEvent event) {

    }
}
