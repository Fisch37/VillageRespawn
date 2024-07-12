package de.fisch37.villagerespawn.server;

import de.fisch37.villagerespawn.VillageIdentifier;
import de.fisch37.villagerespawn.packets.*;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ServerNetworking {
    private static ServerState state;

    public static void register(ServerState state) {
        ServerNetworking.state = state;
        ServerPlayNetworking.registerGlobalReceiver(
                VisitedVillagesRequestPacket.TYPE,
                ServerNetworking::autoSendVisitedVillages
        );
    }

    public static void sendVillage(ServerPlayerEntity player, VillageIdentifier village, boolean isNew) {
        assert player.getServer() != null;
        player.getServer().execute(() -> {
            FabricPacket packet;
            if (isNew) packet = new NewVillageEnteredPacket(village);
            else packet = new OldVillageEnteredPacket(village.name());
            ServerPlayNetworking.send(player, packet);
        });
    }

    public static void sendLeftVillage(ServerPlayerEntity player, VillageIdentifier village) {
        assert player.getServer() != null;
        player.getServer().execute(() -> ServerPlayNetworking.send(player, new VillageLeftPacket(village.name())));
    }

    private static void autoSendVisitedVillages(
            VisitedVillagesRequestPacket packet,
            ServerPlayerEntity player,
            PacketSender responseSender
    ) {
        responseSender.sendPacket(new VisitedVillagesPacket(
                state.getVisitedVillages(player)
                        .toList()
        ));
    }
}
