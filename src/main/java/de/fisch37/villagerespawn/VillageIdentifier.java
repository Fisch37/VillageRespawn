package de.fisch37.villagerespawn;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static de.fisch37.villagerespawn.VillageRespawn.MOD_ID;

public record VillageIdentifier(
        Location location,
        BlockBox boundingBox,
        String name,
        @Nullable BlockPos geographicalCenter
) {
    public static final short VILLAGE_NAME_POOL_SIZE = 666;
    private final static Identifier VILLAGE_RANDOMIZER = Identifier.of(MOD_ID, "village_name_random");
    private static Random RANDOMIZER;

    public Text translatable() {
        return Text.translatable(name);
    }

    public BlockPos getCenter() {
        return geographicalCenter != null ? geographicalCenter : boundingBox.getCenter();
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    private static String getRandomName() {
        int value = RANDOMIZER.nextBetween(0, VILLAGE_NAME_POOL_SIZE);
        return String.format("village.name.%d", value);
    }

    public static VillageIdentifier fromStructure(
            RegistryKey<World> world,
            StructureStart structure,
            @Nullable BlockPos center
    ) {
        // NOTE: This should never be called outside ServerState!
        return new VillageIdentifier(
                locationFromStructureAndWorld(world, structure),
                structure.getBoundingBox(),
                getRandomName(),
                center
        );
    }

    public static Location locationFromStructureAndWorld(RegistryKey<World> world, StructureStart structure) {
        return new Location(world, structure.getBoundingBox().getCenter());
    }


    public NbtCompound toNbt() {
        NbtCompound compound = new NbtCompound();
        compound.putString("world", location.world.getValue().toString());
        compound.putIntArray("position", new int[]{
                location.pos.getX(),
                location.pos.getY(),
                location.pos.getZ()
        });
        if (geographicalCenter != null)
            compound.putIntArray("geographical", new int[]{
                    geographicalCenter.getX(),
                    geographicalCenter.getY(),
                    geographicalCenter.getZ()
            });
        compound.putIntArray("bounds", new int[]{
                boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(),
                boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()
        });
        compound.putString("name", name);
        return compound;
    }

    public static VillageIdentifier fromNbt(NbtCompound nbt) {
        RegistryKey<World> world = RegistryKey.of(
                RegistryKeys.WORLD,
                Identifier.tryParse(nbt.getString("world"))
        );

        int[] posArray = nbt.getIntArray("position");
        BlockPos pos = new BlockPos(posArray[0], posArray[1], posArray[2]);

        int[] geoArray = nbt.getIntArray("geographical");
        BlockPos geographicalCenter;
        if (geoArray.length == 0) geographicalCenter = null;
        else geographicalCenter = new BlockPos(geoArray[0], geoArray[1], geoArray[2]);

        int[] boundingBox = nbt.getIntArray("bounds");
        BlockBox box = new BlockBox(
                boundingBox[0], boundingBox[1], boundingBox[2],
                boundingBox[3], boundingBox[4], boundingBox[5]
        );

        String name = nbt.getString("name");

        return new VillageIdentifier(new Location(world, pos), box, name, geographicalCenter);
    }

    public static void initialise(ServerWorld world) {
        RANDOMIZER = world.getOrCreateRandom(VILLAGE_RANDOMIZER);
    }

    public record Location(RegistryKey<World> world, BlockPos pos) {
        public NbtCompound toNbt() {
            final NbtCompound nbt = NbtHelper.fromBlockPos(pos);
            nbt.putString("world", world.getValue().toString());
            return nbt;
        }

        public static Location fromNbt(final NbtCompound nbt) {
            return new Location(
                    RegistryKey.of(RegistryKeys.WORLD, Identifier.tryParse(nbt.getString("world"))),
                    NbtHelper.toBlockPos(nbt)
            );
        }
    }
}
