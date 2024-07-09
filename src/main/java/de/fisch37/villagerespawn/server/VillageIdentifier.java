package de.fisch37.villagerespawn.server;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import static de.fisch37.villagerespawn.VillageRespawn.MOD_ID;

public record VillageIdentifier(BlockPos position, BlockBox boundingBox, String name) {
    public static final short VILLAGE_NAME_POOL_SIZE = 10;
    private final static Identifier VILLAGE_RANDOMIZER = Identifier.of(MOD_ID, "village_name_random");
    private static Random RANDOMIZER;

    public Text translatable() {
        return Text.translatable(name);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }

    private static String getRandomName() {
        int value = RANDOMIZER.nextBetween(0, VILLAGE_NAME_POOL_SIZE);
        return String.format("village.%s.%d", MOD_ID, value);
    }

    public static VillageIdentifier fromStructure(StructureStart structure) {
        // NOTE: This should never be called outside ServerState!
        return new VillageIdentifier(
                posFromStructure(structure),
                structure.getBoundingBox(),
                getRandomName()
        );
    }

    public static BlockPos posFromStructure(StructureStart structure) {
        return structure.getBoundingBox().getCenter();
    }


    public NbtCompound toNbt() {
        NbtCompound compound = new NbtCompound();
        compound.putIntArray("position", new int[]{
                position.getX(),
                position.getY(),
                position.getZ()
        });
        compound.putIntArray("bounds", new int[]{
                boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(),
                boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()
        });
        compound.putString("name", name);
        return compound;
    }

    public static VillageIdentifier fromNbt(NbtCompound nbt) {
        int[] posArray = nbt.getIntArray("position");
        BlockPos pos = new BlockPos(posArray[0], posArray[1], posArray[2]);

        int[] boundingBox = nbt.getIntArray("bounds");
        BlockBox box = new BlockBox(
                boundingBox[0], boundingBox[1], boundingBox[2],
                boundingBox[3], boundingBox[4], boundingBox[5]
        );

        String name = nbt.getString("name");

        return new VillageIdentifier(pos, box, name);
    }

    public static void initialise(ServerWorld world, ServerState state) {
        RANDOMIZER = world.getOrCreateRandom(VILLAGE_RANDOMIZER);
    }
}
