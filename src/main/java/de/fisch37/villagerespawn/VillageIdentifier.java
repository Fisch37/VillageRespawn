package de.fisch37.villagerespawn;

import de.fisch37.villagerespawn.server.ServerState;
import de.fisch37.villagerespawn.server.VillageNameRandomizer;
import net.minecraft.client.resource.language.I18n;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public record VillageIdentifier(
        // NOTE: This constructor should never be used!
        // If someone ever dares to set the id manually, things will break!
        // I can't hide it though, because java sucks and I don't know why I'm doing this to myself please help
        int id,
        Location location,
        BlockBox boundingBox,
        String name,
        @Nullable BlockPos geographicalCenter
) {
    private static VillageNameRandomizer RANDOMIZER;
    private static ServerState STATE;

    public VillageIdentifier(
            Location location,
            BlockBox boundingBox,
            String name,
            @Nullable BlockPos geographicalCenter
    ) {
        this(
                STATE.getNewVillageID(),
                location,
                boundingBox,
                name,
                geographicalCenter
        );
    }

    public Text translatable() {
        return Text.translatable(name);
    }

    public String translated() {
        return I18n.translate(name);
    }

    public BlockPos getCenter() {
        return geographicalCenter != null ? geographicalCenter : boundingBox.getCenter();
    }

    @Override
    public int hashCode() {
        return location.hashCode();
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
                RANDOMIZER.getRandomName(structure),
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
        compound.putInt("id", id);
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
        int id;
        if (nbt.contains("id")) id = nbt.getInt("id");
        else id = STATE.getNewVillageID();

        return new VillageIdentifier(
                id,
                new Location(world, pos),
                box,
                name,
                geographicalCenter
        );
    }

    public static void initialise(ServerWorld world, ServerState state) {
        RANDOMIZER = new VillageNameRandomizer(world);
        STATE = state;
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
