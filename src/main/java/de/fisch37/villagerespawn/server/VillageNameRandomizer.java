package de.fisch37.villagerespawn.server;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandomImpl;

import static de.fisch37.villagerespawn.VillageRespawn.MOD_ID;

public class VillageNameRandomizer {
    public static final short VILLAGE_NAME_POOL_SIZE = 666;

    private final long seed;

    public VillageNameRandomizer(ServerWorld world) {
        seed = world.getSeed();
    }

    public String getRandomName(StructureStart structure) {
        int value = getRandForStructure(structure);
        return String.format("village.name.%d", value);
    }

    private int getRandForStructure(StructureStart structure) {
        // Generates a deterministic village name.
        // The aim here is to ensure that:
        //  1. Village names are dependent on the world seed.
        //  2. Village names are independent of their discovery time.
        // This is accomplished by seeding a new random generator
        // with the world seed and the position of the village structure.
        return getRandForLong(structure
                .getBoundingBox()
                .getCenter()
                .asLong()
        );
    }

    private int getRandForLong(long pos) {
        // I don't know shit about random distributions.
        // I'm just hoping this doesn't create bias anywhere
        long rawValue = new Xoroshiro128PlusPlusRandomImpl(seed, pos).next();
        return (int) Math.abs(rawValue % VILLAGE_NAME_POOL_SIZE);
    }
}
