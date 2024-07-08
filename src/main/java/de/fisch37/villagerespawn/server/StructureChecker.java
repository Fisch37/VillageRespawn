package de.fisch37.villagerespawn.server;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.Set;

public class StructureChecker {
    private final static int RANGE = 4;
    private final static Logger LOG = LoggerFactory.getLogger(StructureChecker.class);
    public final static TagKey<Structure> VILLAGES_TAG = TagKey.of(RegistryKeys.STRUCTURE, Identifier.of(
            "village_respawn",
            "village"
    ));

    private static @Nullable BlockPos getNearestBell(
            ServerWorld world,
            BlockPos center
    ) {
        try{
            return world.getPointOfInterestStorage().getNearestTypeAndPosition(
                    reg -> reg.matchesKey(PointOfInterestTypes.MEETING),
                    center,
                    RANGE * 16,
                    PointOfInterestStorage.OccupationStatus.ANY
                ).orElseThrow().getSecond();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public static boolean isInVillage(ServerPlayerEntity player) {
        return isInVillage(player.getServerWorld(), player.getBlockPos());
    }
    public static boolean isInVillage(ServerWorld world, BlockPos position) {
        StructureAccessor accessor = world.getStructureAccessor();
        Set<Structure> structures = accessor
                .getStructureReferences(position)
                .keySet();

        for (Structure struct : structures) {
            // TODO:
            //  world.getRegistryManager().get(RegistryKeys.STRUCTURE).getEntrySet().iterator().next().getValue()
            //  == struct
            //  Use this with a new tag for all structures you want to match!
            if (!structureIsVillage(struct, world))
                continue;
            StructureStart start = accessor.getStructureAt(position, struct);
            if (start != StructureStart.DEFAULT)
                // Boils down to a successful box check ^-^
                return true;
        }

        return false;
    }

    private static boolean structureIsVillage(Structure structure, ServerWorld world) {
        RegistryEntryList<Structure> validStructures = world.getRegistryManager()
                .get(RegistryKeys.STRUCTURE)
                .getEntryList(VILLAGES_TAG)
                .orElseThrow();

        return validStructures.stream()
                .anyMatch(entry -> entry.value() == structure);
    }
}
