package de.fisch37.villagerespawn.client;

import de.fisch37.villagerespawn.client.integrations.JourneyMapIntegration;
import de.fisch37.villagerespawn.client.integrations.MinimapIntegration;
import de.fisch37.villagerespawn.client.integrations.XaerosIntegration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

import java.util.Optional;

public class VillageRespawnClient implements ClientModInitializer {
    private static final String XAEROS_MOD_ID = "xaerominimap";
    private static final String JOURNEY_MOD_ID = "journeymap";

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
