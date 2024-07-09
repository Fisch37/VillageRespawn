package de.fisch37.villagerespawn.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import static de.fisch37.villagerespawn.packets.PacketTypes.*;

public abstract class ClientNetworking {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(VILLAGE_ENTERED_NEW, ClientNetworking::newVillage);
        ClientPlayNetworking.registerGlobalReceiver(VILLAGE_ENTERED_OLD, ClientNetworking::oldVillage);
        ClientPlayNetworking.registerGlobalReceiver(VILLAGE_LEFT, ClientNetworking::leftVillage);
    }

    public static void newVillage(
            MinecraftClient client,
            ClientPlayNetworkHandler handler,
            PacketByteBuf buf,
            PacketSender responseHandler
    ) {
        String villageTranslationKey = buf.readString();
    }

    public static void oldVillage(
            MinecraftClient client,
            ClientPlayNetworkHandler handler,
            PacketByteBuf buf,
            PacketSender responseHandler
    ) {
        String villageTranslationKey = buf.readString();
    }

    public static void leftVillage(
            MinecraftClient client,
            ClientPlayNetworkHandler handler,
            PacketByteBuf buf,
            PacketSender responseHandler
    ) {

    }
}
