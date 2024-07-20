package de.fisch37.villagerespawn.client.integrations;

import de.fisch37.villagerespawn.packets.NewVillageEnteredPacket;
import de.fisch37.villagerespawn.packets.VisitedVillagesPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayerEntity;

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
