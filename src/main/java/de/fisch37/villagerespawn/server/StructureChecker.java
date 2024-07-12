package de.fisch37.villagerespawn.server;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
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

    public static @Nullable BlockPos getBellIn(
            ServerWorld world,
            StructureStart structure
    ) {
        try{
            return world.getPointOfInterestStorage().getNearestPosition(
                    reg -> reg.matchesKey(PointOfInterestTypes.MEETING),
                    pos -> structure.getBoundingBox().contains(pos),
                    structure.getBoundingBox().getCenter(),
                    getLargestSize(structure),
                    PointOfInterestStorage.OccupationStatus.ANY
                ).orElseThrow();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private static int getLargestSize(StructureStart structure) {
        Vec3i size = structure.getBoundingBox().getDimensions();
        return Math.max(Math.max(size.getX(), size.getY()), size.getZ());
    }

    public static @Nullable StructureStart isInVillage(ServerPlayerEntity player) {
        return isInVillage(player.getServerWorld(), player.getBlockPos());
    }
    public static @Nullable StructureStart isInVillage(ServerWorld world, BlockPos position) {
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
                return start;
        }

        return null;
    }

    private static boolean structureIsVillage(Structure structure, ServerWorld world) {
        RegistryEntryList<Structure> validStructures;
        try{
            validStructures = world.getRegistryManager()
                    .get(RegistryKeys.STRUCTURE)
                    .getEntryList(VILLAGES_TAG)
                    .orElseThrow();
        } catch (NoSuchElementException e) {
            LOG.error("Could not find villages tag. VillageRespawn will not function!");
            return false;
        }

        return validStructures.stream()
                .anyMatch(entry -> entry.value() == structure);
    }

    public static final class BellCache {
        private final ServerWorld world;
        private final StructureStart structure;
        private BlockPos pos;

        public BellCache(ServerWorld world, StructureStart structure) {
            this.world = world;
            this.structure = structure;
        }

        private void evaluate() {
            this.pos = getBellIn(world, structure);
        }

        public BlockPos get() {
            if (pos == null) evaluate();
            return pos;
        }
    }
}
