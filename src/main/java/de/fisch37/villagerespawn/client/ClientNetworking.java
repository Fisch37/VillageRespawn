package de.fisch37.villagerespawn.client;

import com.mojang.datafixers.util.Function3;
import de.fisch37.villagerespawn.packets.NewVillageEnteredPacket;
import de.fisch37.villagerespawn.packets.OldVillageEnteredPacket;
import de.fisch37.villagerespawn.packets.VillageLeftPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
// I mean who cares, it's in there anyway
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ClientNetworking {
    private static @Nullable TriConsumer<NewVillageEnteredPacket, ClientPlayerEntity, PacketSender> newVillageListener;

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(NewVillageEnteredPacket.TYPE, ClientNetworking::newVillage);
        ClientPlayNetworking.registerGlobalReceiver(OldVillageEnteredPacket.TYPE, ClientNetworking::oldVillage);
        ClientPlayNetworking.registerGlobalReceiver(VillageLeftPacket.TYPE, ClientNetworking::leftVillage);
    }

    public static void setNewVillageListener(
            TriConsumer<NewVillageEnteredPacket, ClientPlayerEntity, PacketSender> listener
    ) {
        newVillageListener = listener;
    }

    private static void newVillage(
            NewVillageEnteredPacket packet,
            ClientPlayerEntity player,
            PacketSender responseHandler
    ) {
        setTitleAndSubtitle(
                MinecraftClient.getInstance(),
                packet.village().translatable()
                        .copy()
                        .formatted(Formatting.DARK_GREEN),
                null
        );
        if (newVillageListener != null)
            newVillageListener.accept(packet, player, responseHandler);
    }

    private static void oldVillage(
            OldVillageEnteredPacket packet,
            ClientPlayerEntity player,
            PacketSender responseHandler
    ) {
        setTitleAndSubtitle(
                MinecraftClient.getInstance(),
                Text.translatable(packet.name())
                        .formatted(Formatting.GRAY),
                null
        );
    }

    private static void leftVillage(
            VillageLeftPacket packet,
            ClientPlayerEntity player,
            PacketSender responseHandler
    ) {
        setActionBar(
                MinecraftClient.getInstance(),
                Text.translatable("village.leaving")
                        .append(Text.translatable(packet.name()))
        );
    }


    private static void setTitleAndSubtitle(
            MinecraftClient client,
            @NotNull Text title,
            @Nullable Text subtitle
    ) {
        client.inGameHud.clearTitle();
        client.inGameHud.setTitle(title);
        if (subtitle != null) {
            client.inGameHud.setSubtitle(subtitle);
        }
    }

    private static void setActionBar(
            MinecraftClient client,
            @NotNull Text actionbar
    ) {
        client.inGameHud.setOverlayMessage(actionbar, false);
    }
}
