package de.fisch37.villagerespawn.packets;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static de.fisch37.villagerespawn.VillageRespawn.MOD_ID;

public interface PacketTypes {
    @NotNull
    Identifier VILLAGE_ENTERED_NEW = Objects.requireNonNull(Identifier.of(MOD_ID, "new_village_entered"));
    @NotNull
    Identifier VILLAGE_ENTERED_OLD = Objects.requireNonNull(Identifier.of(MOD_ID, "old_village_entered"));
    @NotNull
    Identifier VILLAGE_LEFT = Objects.requireNonNull(Identifier.of(MOD_ID, "village_left"));
    @NotNull
    Identifier VISITED_VILLAGES = Objects.requireNonNull(Identifier.of(MOD_ID, "visited_villages"));
    @NotNull
    Identifier VISITED_VILLAGES_REQUEST = Objects.requireNonNull(Identifier.of(MOD_ID, "visited_villages_req"));
}
