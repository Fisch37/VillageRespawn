package de.fisch37.villagerespawn.server;

import net.minecraft.server.world.ServerWorld;
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

    public String getRandomName() {
        int value = random.nextBetween(0, VILLAGE_NAME_POOL_SIZE);
        return String.format("village.name.%d", value);
    }
}
