package de.fisch37.villagerespawn.client;

import de.fisch37.villagerespawn.client.integrations.MinimapIntegration;
import de.fisch37.villagerespawn.packets.VisitedVillagesPacket;
import de.fisch37.villagerespawn.packets.VisitedVillagesRequestPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static de.fisch37.villagerespawn.VillageRespawn.LOG;

public abstract class IntegrationAPI {
    private final static Map<String, Class<? extends MinimapIntegration>> INTEGRATIONS = new LinkedHashMap<>();

    /**
     * Registers a new integration.
     * Only one integration may be registered for a given mod id at a time.
     * If another integration is already registered for the same mod id,
     * this method emits a warning but does not register the new integration.
     * <p>
     * Developers should always check the return value if they want to ensure their integration has been registered.
     *
     * @param target
     *  The id of the mod the integration supports
     * @param integration
     *  The class of the integration to be registered
     * @return
     *  <code>true</code> if the integration was registered or <code>false</code> otherwise
     */
    public static <T extends MinimapIntegration> boolean registerIntegration(String target, Class<T> integration) {
        Class<? extends MinimapIntegration> existing = INTEGRATIONS.putIfAbsent(target, integration);
        if (existing != null) {
            LOG.warn(
                    "Double registration for target mod {}. {} tried to register, but {} was already present",
                    target,
                    integration,
                    existing
            );
            return false;
        }
        return true;
    }

    /**
     * Override a registered integration.
     * Use this instead of registerIntegration to override the default integrations.
     *
     * @param target
     *  The id of the mod both integrations support
     * @param newIntegration
     *  The new integration to be added
     * @param oldIntegration
     *  The integration currently registered for <code>target</code>
     * @return
     *  <code>true</code> if the replacement happened successfully
     *  or <code>false</code> if <code>oldIntegration</code> does not match the actually registered value.
     */
    public static boolean overrideIntegration(
            String target,
            Class<MinimapIntegration> newIntegration,
            Class<MinimapIntegration> oldIntegration
    ) {
        Class<? extends MinimapIntegration> actualOld = INTEGRATIONS.get(target);
        if (!oldIntegration.equals(actualOld)) {
            LOG.error(
                    "{} attempted to override {} integration, but {} was present instead",
                    newIntegration,
                    oldIntegration,
                    actualOld
            );
            return false;
        }
        INTEGRATIONS.put(target, newIntegration);
        return true;
    }


    // TODO WTF is this typing?
    private static Optional<? extends Class<? extends MinimapIntegration>> getFirstAvailableIntegration() {
        FabricLoader loader = FabricLoader.getInstance();
        return INTEGRATIONS
                .entrySet()
                .stream()
                .filter(entry -> loader.isModLoaded(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    private static Optional<MinimapIntegration> makeAvailableIntegration() {
        return getFirstAvailableIntegration().map(clazz -> {
            try {
                return clazz.getConstructor().newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                // Concatenation is necessary here because we want the Throwable version of error.
                LOG.error("Failed to instantiate minimap integration " + clazz.toString(), e);
                return null;
            }
        });
    }

    static Optional<MinimapIntegration> startIntegration() {
        Optional<MinimapIntegration> integration = makeAvailableIntegration();
        if (integration.isPresent()) {
            MinimapIntegration integrationValue = integration.get();
            ClientPlayNetworking.registerGlobalReceiver(
                    VisitedVillagesPacket.TYPE,
                    integrationValue::addVisitedVillages
            );
            ClientNetworking.setNewVillageListener(integrationValue::addNewVillage);
            ClientPlayConnectionEvents.JOIN.register(
                    (ClientPlayNetworkHandler handler,
                     PacketSender sender,
                     MinecraftClient client)
                            -> ClientPlayNetworking.send(new VisitedVillagesRequestPacket())
            );
            LOG.info("Registered minimap integration {}", integrationValue.getClass());
        } else {
            LOG.info("No minimap Integration found, none started");
        }
        return integration;
    }
}
