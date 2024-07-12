package de.fisch37.villagerespawn.packets;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record VisitedVillagesRequestPacket() implements FabricPacket {
    public static final PacketType<VisitedVillagesRequestPacket> TYPE = PacketType.create(
            PacketTypes.VISITED_VILLAGES_REQUEST,
            (buf) -> new VisitedVillagesRequestPacket()
    );

    /**
     * Writes the contents of this packet to the buffer.
     *
     * @param buf the output buffer
     */
    @Override
    public void write(PacketByteBuf buf) { }

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
