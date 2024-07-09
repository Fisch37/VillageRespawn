package de.fisch37.villagerespawn.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

        setTitleAndSubtitle(
                client,
                Text.translatable(villageTranslationKey)
                        .formatted(Formatting.DARK_GREEN),
                null
        );
    }

    public static void oldVillage(
            MinecraftClient client,
            ClientPlayNetworkHandler handler,
            PacketByteBuf buf,
            PacketSender responseHandler
    ) {
        String villageTranslationKey = buf.readString();
        setTitleAndSubtitle(
                client,
                Text.translatable(villageTranslationKey)
                        .formatted(Formatting.GRAY),
                null
        );
    }

    public static void leftVillage(
            MinecraftClient client,
            ClientPlayNetworkHandler handler,
            PacketByteBuf buf,
            PacketSender responseHandler
    ) {
        setActionBar(
                client,
                Text.translatable("village.leaving")
                        .append(Text.translatable(buf.readString()))
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
