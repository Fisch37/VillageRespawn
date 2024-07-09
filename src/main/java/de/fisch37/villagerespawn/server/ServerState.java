package de.fisch37.villagerespawn.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

import static de.fisch37.villagerespawn.VillageRespawn.MOD_ID;

public class ServerState extends PersistentState {
    private final Map<UUID, Set<BlockPos>> visitedVillages;
    private final Map<BlockPos, VillageIdentifier> villages;

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
        return visitedVillages.get(player)
                .stream()
                .map(villages::get);
    }

    public boolean setVillageVisited(PlayerEntity player, VillageIdentifier village) {
        Set<BlockPos> playerVisited = visitedVillages.computeIfAbsent(player.getUuid(), k -> new HashSet<>());
        return playerVisited.add(village.position());
    }

    public @Nullable VillageIdentifier getVillage(StructureStart structure) {
        return getVillage(VillageIdentifier.posFromStructure(structure));
    }
    public @Nullable VillageIdentifier getVillage(BlockPos pos) {
        return villages.get(pos);
    }

    public VillageIdentifier getOrCreateVillage(StructureStart structure) {
        VillageIdentifier identifier = getVillage(structure);
        if (identifier == null) {
            identifier = VillageIdentifier.fromStructure(structure);
            villages.put(identifier.position(), identifier);
        }
        return identifier;
    }


    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList villageNbtList = new NbtList();
        villages.values().forEach(village -> villageNbtList.add(village.toNbt()));
        nbt.put("villages", villageNbtList);

        NbtCompound visitationTable = new NbtCompound();
        for (Map.Entry<UUID, Set<BlockPos>> entry : visitedVillages.entrySet()) {
            NbtList list = new NbtList();
            for (BlockPos villagePos : entry.getValue()) {
                // Stores as NbtCompound which is slightly inefficient, but it's finnnnee
                list.add(NbtHelper.fromBlockPos(villagePos));
            }
            visitationTable.put(entry.getKey().toString(), list);
        }
        nbt.put("visited", visitationTable);

        return nbt;
    }

    public static ServerState createFromNbt(NbtCompound tag) {
        ServerState state = new ServerState();

        for (NbtElement element : tag.getList("villages", NbtElement.COMPOUND_TYPE)) {
            VillageIdentifier identifier = VillageIdentifier.fromNbt((NbtCompound) element);
            state.villages.put(identifier.position(), identifier);
        }

        NbtCompound visitationTable = tag.getCompound("visited");
        for (String playerUuid : visitationTable.getKeys()) {
            Set<BlockPos> playerHasVisited = new HashSet<>();
            NbtList playerVisitedNbt = (NbtList) Objects.requireNonNull(visitationTable.get(playerUuid));
            for (NbtElement element : playerVisitedNbt) {
                playerHasVisited.add(NbtHelper.toBlockPos((NbtCompound)element));
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
