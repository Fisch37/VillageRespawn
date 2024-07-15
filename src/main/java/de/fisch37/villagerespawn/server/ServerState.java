package de.fisch37.villagerespawn.server;

import de.fisch37.villagerespawn.VillageIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static de.fisch37.villagerespawn.VillageRespawn.MOD_ID;

public class ServerState extends PersistentState {
    private final Map<UUID, Set<VillageIdentifier.Location>> visitedVillages;
    private final Map<VillageIdentifier.Location, VillageIdentifier> villages;
    private int lastVillageID = 0;

    public ServerState() {
        visitedVillages = new HashMap<>();
        villages = new HashMap<>();
    }

    public Collection<VillageIdentifier> getAllVillages() {
        return villages.values();
    }

    public Stream<VillageIdentifier> getVisitedVillages(PlayerEntity player) {
        return getVisitedVillages(player.getUuid());
    }
    public Stream<VillageIdentifier> getVisitedVillages(UUID player) {
        return visitedVillages.getOrDefault(player, Set.of())
                .stream()
                .map(villages::get);
    }

    public boolean setVillageVisited(PlayerEntity player, VillageIdentifier village) {
        Set<VillageIdentifier.Location> playerVisited = visitedVillages.computeIfAbsent(player.getUuid(), k -> new HashSet<>());
        return playerVisited.add(village.location());
    }

    public @Nullable VillageIdentifier getVillage(RegistryKey<World> world, StructureStart structure) {
        return getVillage(VillageIdentifier.locationFromStructureAndWorld(world, structure));
    }
    public @Nullable VillageIdentifier getVillage(VillageIdentifier.Location location) {
        return villages.get(location);
    }

    public VillageIdentifier getOrCreateVillage(
            RegistryKey<World> world,
            StructureStart structure,
            Supplier<@Nullable BlockPos> villageCenter
    ) {
        VillageIdentifier identifier = getVillage(world, structure);
        if (identifier == null) {
            identifier = VillageIdentifier.fromStructure(world, structure, villageCenter.get());
            villages.put(identifier.location(), identifier);
        }
        return identifier;
    }

    public int getNewVillageID() {
        return lastVillageID++;
    }


    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList villageNbtList = new NbtList();
        villages.values().forEach(village -> villageNbtList.add(village.toNbt()));
        nbt.put("villages", villageNbtList);

        NbtCompound visitationTable = new NbtCompound();
        for (Map.Entry<UUID, Set<VillageIdentifier.Location>> entry : visitedVillages.entrySet()) {
            NbtList list = new NbtList();
            for (VillageIdentifier.Location villagePos : entry.getValue()) {
                // Stores as NbtCompound which is slightly inefficient, but it's finnnnee
                list.add(villagePos.toNbt());
            }
            visitationTable.put(entry.getKey().toString(), list);
        }
        nbt.put("visited", visitationTable);

        nbt.putInt("lastVillageID", lastVillageID);

        return nbt;
    }

    public static ServerState createFromNbt(NbtCompound tag) {
        ServerState state = new ServerState();
        state.lastVillageID = tag.getInt("lastVillageID");

        for (NbtElement element : tag.getList("villages", NbtElement.COMPOUND_TYPE)) {
            VillageIdentifier identifier = VillageIdentifier.fromNbt((NbtCompound) element);
            state.villages.put(identifier.location(), identifier);
        }

        NbtCompound visitationTable = tag.getCompound("visited");
        for (String playerUuid : visitationTable.getKeys()) {
            Set<VillageIdentifier.Location> playerHasVisited = new HashSet<>();
            NbtList playerVisitedNbt = (NbtList) Objects.requireNonNull(visitationTable.get(playerUuid));
            for (NbtElement element : playerVisitedNbt) {
                playerHasVisited.add(VillageIdentifier.Location.fromNbt((NbtCompound)element));
            }
            state.visitedVillages.put(UUID.fromString(playerUuid), playerHasVisited);
        }

        return state;
    }

    public static ServerState getServerState(MinecraftServer server) {
        PersistentStateManager manager = server
                .getOverworld()
                .getPersistentStateManager();

        ServerState state = manager.getOrCreate(
                ServerState::createFromNbt,
                ServerState::new,
                MOD_ID
        );
        state.markDirty();
        return state;
    }
}
