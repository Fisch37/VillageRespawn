package de.fisch37.villagerespawn.packets;

import de.fisch37.villagerespawn.VillageIdentifier;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

import java.util.Objects;

public record NewVillageEnteredPacket(VillageIdentifier village) implements FabricPacket {
    public static final PacketType<NewVillageEnteredPacket> TYPE = PacketType.create(
            PacketTypes.VILLAGE_ENTERED_NEW,
            NewVillageEnteredPacket::new
    );


    public NewVillageEnteredPacket(PacketByteBuf buf) {
        this(VillageIdentifier.fromNbt(Objects.requireNonNull(buf.readNbt())));
    }

    /**
     * Writes the contents of this packet to the buffer.
     *
     * @param buf the output buffer
     */
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeNbt(village.toNbt());
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
