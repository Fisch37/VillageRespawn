package de.fisch37.villagerespawn.server;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import static de.fisch37.villagerespawn.VillageRespawn.MOD_ID;

public class VillageNameRandomizer {
    public static final short VILLAGE_NAME_POOL_SIZE = 666;
    private final static Identifier VILLAGE_RANDOMIZER = Identifier.of(MOD_ID, "village_name_random");

    private final Random random;

    public VillageNameRandomizer(ServerWorld world) {
        random = world.getOrCreateRandom(VILLAGE_RANDOMIZER);
    }

    public String getRandomName(StructureStart structure) {
        int value = getRandForStructure(structure);
        return String.format("village.name.%d", value);
    }

    private int getRandForStructure(StructureStart structure) {
        // TODO: Use some kind of hash-like system to generate a "random" value.
        //  Essentially, I want to base the name of a village off the world seed
        //  and its unique identifier (i.e. the BlockPos of the structure).
        //  This means village names are seed-dependent and therefore consistent
        //  across users.
        return random.nextBetween(0, VILLAGE_NAME_POOL_SIZE);
    }
}
