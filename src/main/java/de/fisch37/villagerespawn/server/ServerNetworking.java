package de.fisch37.villagerespawn.server;

import de.fisch37.villagerespawn.packets.NewVillageEnteredPacket;
import de.fisch37.villagerespawn.packets.OldVillageEnteredPacket;
import de.fisch37.villagerespawn.packets.VillageLeftPacket;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ServerNetworking {
    public static void sendVillage(ServerPlayerEntity player, VillageIdentifier village, boolean isNew) {
        String name = village.name();
        assert player.getServer() != null;
        player.getServer().execute(() -> {
            FabricPacket packet;
            if (isNew) packet = new NewVillageEnteredPacket(name);
            else packet = new OldVillageEnteredPacket(name);
            ServerPlayNetworking.send(player, packet);
        });
    }

    public static void sendLeftVillage(ServerPlayerEntity player, VillageIdentifier village) {
        assert player.getServer() != null;
        player.getServer().execute(() -> ServerPlayNetworking.send(player, new VillageLeftPacket(village.name())));
    }
}
