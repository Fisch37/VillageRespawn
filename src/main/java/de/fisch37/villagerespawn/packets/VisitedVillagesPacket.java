package de.fisch37.villagerespawn.packets;

import de.fisch37.villagerespawn.VillageIdentifier;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

import static de.fisch37.villagerespawn.VillageRespawn.LOG;

public record VisitedVillagesPacket(List<VillageIdentifier> villages) implements FabricPacket {
    public static final PacketType<VisitedVillagesPacket> TYPE = PacketType.create(
            PacketTypes.VISITED_VILLAGES,
            VisitedVillagesPacket::fromBuffer
    );

    public static VisitedVillagesPacket fromBuffer(PacketByteBuf buf) {
        List<VillageIdentifier> villages = new ArrayList<>();
        final int listLength = buf.readInt();
        for (int i = 0; i < listLength; i++) {
            NbtCompound nbt = buf.readNbt();
            if (nbt == null) {
                LOG.error("Found null while reading VisitedVillagesPacket. This will cause incorrect data!");
                break;
            }
            villages.add(VillageIdentifier.fromNbt(nbt));
        }
        return new VisitedVillagesPacket(villages);
    }

    /**
     * Writes the contents of this packet to the buffer.
     *
     * @param buf the output buffer
     */
    @Override
    public void write(PacketByteBuf buf) {
        // I sure hope this won't become a vulnerability later.
        // So far I only see it disconnecting a client
        buf.writeInt(villages.size());
        for (VillageIdentifier village : villages) {
            buf.writeNbt(village.toNbt());
        }
    }

    /**
     * Returns the packet type of this packet.
     *
     * <p>Implementations should store the packet type instance in a {@code static final}
     * field and return that here, instead of creating a new instance.
     *
     * @return the type of this packet
     */
    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
