package de.fisch37.villagerespawn;

import de.fisch37.villagerespawn.server.PlayerInteractions;
import de.fisch37.villagerespawn.server.ServerNetworking;
import de.fisch37.villagerespawn.server.ServerState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VillageRespawn implements ModInitializer {
    public final static String MOD_ID = "village_respawn";
    public final static Logger LOG = LoggerFactory.getLogger(VillageRespawn.class);

    private static ServerState STATE;

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        ServerWorldEvents.LOAD.register(this::initialiseServer);
        UseBlockCallback.EVENT.register(PlayerInteractions::setSpawnBellClicked);
    }

    public void initialiseServer(MinecraftServer server, ServerWorld world) {
        STATE = ServerState.getServerState(server);
        VillageIdentifier.initialise(world, STATE);
        ServerNetworking.register(STATE);
    }

    public static ServerState getState() {
        return STATE;
    }
}
