package de.fisch37.villagerespawn.server;

import de.fisch37.villagerespawn.VillageIdentifier;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

import static de.fisch37.villagerespawn.VillageRespawn.LOG;
import static de.fisch37.villagerespawn.VillageRespawn.getState;

public class VillageUpdateManager {
    @Nullable
    public static VillageIdentifier villageUpdate(
            ServerPlayerEntity player,
            @Nullable VillageIdentifier lastVillage
    ) {
        ServerWorld world = player.getServerWorld();

        StructureStart structure = StructureChecker.isInVillage(player);
        VillageIdentifier village;
        if (structure != null) {
            StructureChecker.BellCache bellCache = new StructureChecker.BellCache(world, structure);
            village = getState().getOrCreateVillage(
                    world.getRegistryKey(),
                    structure,
                    bellCache::get
            );
            if (village != lastVillage) {
                boolean villageIsNew = getState().setVillageVisited(player, village);
                if (villageIsNew) {
                    updateSpawn(player, world, bellCache.get());
                }
                ServerNetworking.sendVillage(player, village, villageIsNew);
            }
        } else {
            if (lastVillage != null)
                ServerNetworking.sendLeftVillage(player, lastVillage);
            village = null;
        }
        return village;
    }

    private static void updateSpawn(
            ServerPlayerEntity player,
            ServerWorld world,
            BlockPos bell
    ) {
        if (bell != null) {
            bell = findSafePosition(world, bell);
        }
        if (bell == null) {
            LOG.warn("Could not find bell in village or was obscured. Attempting to use player position");
            bell = findSafePosition(world, player.getBlockPos());
            if (bell == null) {
                LOG.error("Could not generate valid spawnpoint for village :c");
                return;
            }
        }
        // FIXME: Fix spawnpoint obstruction
        player.setSpawnPoint(
                world.getRegistryKey(),
                bell,
                0,
                true,
                true
        );
    }

    public static @Nullable BlockPos findSafePosition(CollisionView world, BlockPos pos) {
        return RespawnAnchorBlock
                .findRespawnPosition(EntityType.PLAYER, world, pos)
                .map(BlockPos::ofFloored)
                .orElse(null);
    }
}
