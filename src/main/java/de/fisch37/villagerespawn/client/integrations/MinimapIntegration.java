package de.fisch37.villagerespawn.client.integrations;

import de.fisch37.villagerespawn.client.ClientNetworking;
import de.fisch37.villagerespawn.packets.NewVillageEnteredPacket;
import de.fisch37.villagerespawn.packets.VisitedVillagesPacket;
import de.fisch37.villagerespawn.packets.VisitedVillagesRequestPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Pair;

import java.lang.reflect.Type;
import java.util.List;

public interface MinimapIntegration {
    void addNewVillage(
            NewVillageEnteredPacket newVillageEnteredPacket,
            ClientPlayerEntity clientPlayerEntity,
            PacketSender packetSender
    );


    void addVisitedVillages(
            VisitedVillagesPacket visitedVillagesPacket,
            ClientPlayerEntity clientPlayerEntity,
            PacketSender packetSender
    );
}
