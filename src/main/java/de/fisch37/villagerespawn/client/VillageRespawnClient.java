package de.fisch37.villagerespawn.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;

public class VillageRespawnClient implements ClientModInitializer {
    private static final String XAEROS_MOD_ID = "xaerominimap";
    private boolean hasTriggeredPostLoad = false;

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        ClientNetworking.register();

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            // Only the best loading triggers
            if (hasTriggeredPostLoad) return;
            postLoad();
            hasTriggeredPostLoad = true;
        });
    }

    private void postLoad() {
        mayLoadXaeros();
    }

    private void mayLoadXaeros() {
        if(!FabricLoader.getInstance().isModLoaded(XAEROS_MOD_ID))
            return;
        new XaerosIntegration();
    }
}
