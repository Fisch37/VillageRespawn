package de.fisch37.villagerespawn;

import de.fisch37.villagerespawn.server.ServerState;
import de.fisch37.villagerespawn.server.VillageIdentifier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class VillageRespawn implements ModInitializer {
    public final static String MOD_ID = "village_respawn";

    private static ServerState STATE;

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        ServerWorldEvents.LOAD.register(this::initialiseServer);
    }

    public void initialiseServer(MinecraftServer server, ServerWorld world) {
        STATE = ServerState.getServerState(server);
        VillageIdentifier.initialise(world, STATE);
    }

    public static ServerState getState() {
        return STATE;
    }
}
