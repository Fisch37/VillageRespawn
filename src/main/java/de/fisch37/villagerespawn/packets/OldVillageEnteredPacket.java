package de.fisch37.villagerespawn.packets;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record OldVillageEnteredPacket(String name) implements FabricPacket {
    public static final PacketType<OldVillageEnteredPacket> TYPE = PacketType.create(
            PacketTypes.VILLAGE_ENTERED_OLD,
            OldVillageEnteredPacket::new
    );


    public OldVillageEnteredPacket(PacketByteBuf buf) {
        this(buf.readString());
    }

    /**
     * Writes the contents of this packet to the buffer.
     *
     * @param buf the output buffer
     */
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(name);
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
