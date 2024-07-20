package de.fisch37.villagerespawn.client;

import de.fisch37.villagerespawn.client.integrations.JourneyMapIntegration;
import de.fisch37.villagerespawn.client.integrations.MinimapIntegration;
import de.fisch37.villagerespawn.client.integrations.VoxelIntegration;
import de.fisch37.villagerespawn.client.integrations.XaerosIntegration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class VillageRespawnClient implements ClientModInitializer {
    private static final String
            XAEROS_MOD_ID = "xaerominimap",
            JOURNEY_MOD_ID = "journeymap",
            VOXEL_MOD_ID = "voxelmap"
                    ;

    private boolean hasTriggeredPostLoad = false;
    private Optional<MinimapIntegration> integration;

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        ClientNetworking.register();

        IntegrationAPI.registerIntegration(XAEROS_MOD_ID, XaerosIntegration.class);
        IntegrationAPI.registerIntegration(JOURNEY_MOD_ID, JourneyMapIntegration.class);
        IntegrationAPI.registerIntegration(VOXEL_MOD_ID, VoxelIntegration.class);

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            // Only the best loading triggers
            if (hasTriggeredPostLoad) return;
            postLoad();
            hasTriggeredPostLoad = true;
        });
    }


    private void postLoad() {
        integration = IntegrationAPI.startIntegration();
    }
}
