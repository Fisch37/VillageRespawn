package de.fisch37.villagerespawn.client;

import de.fisch37.villagerespawn.packets.NewVillageEnteredPacket;
import de.fisch37.villagerespawn.packets.VisitedVillagesPacket;
import de.fisch37.villagerespawn.packets.VisitedVillagesRequestPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

public abstract class MinimapIntegration {
    public void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(
                VisitedVillagesPacket.TYPE,
                this::addVisitedVillages
        );
        ClientNetworking.setNewVillageListener(this::addNewVillage);
        ClientPlayConnectionEvents.JOIN.register(
                (ClientPlayNetworkHandler handler,
                 PacketSender sender,
                 MinecraftClient client)
                        -> ClientPlayNetworking.send(new VisitedVillagesRequestPacket())
        );
    }

    public abstract void addNewVillage(
            NewVillageEnteredPacket newVillageEnteredPacket,
            ClientPlayerEntity clientPlayerEntity,
            PacketSender packetSender
    );


    public abstract void addVisitedVillages(
            VisitedVillagesPacket visitedVillagesPacket,
            ClientPlayerEntity clientPlayerEntity,
            PacketSender packetSender
    );
}
