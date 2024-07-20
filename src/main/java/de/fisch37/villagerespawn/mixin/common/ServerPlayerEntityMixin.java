package de.fisch37.villagerespawn.mixin.common;

import de.fisch37.villagerespawn.server.ServerNetworking;
import de.fisch37.villagerespawn.server.StructureChecker;
import de.fisch37.villagerespawn.VillageIdentifier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.fisch37.villagerespawn.VillageRespawn.LOG;
import static de.fisch37.villagerespawn.VillageRespawn.getState;
import static de.fisch37.villagerespawn.server.ServerUtils.findSafePosition;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Unique
    private ChunkSectionPos lastPosition;
    @Unique @Nullable
    private VillageIdentifier lastVillage;

    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity)((Object)this);
        ServerWorld world = player.getServerWorld();
        assert world != null;

        ChunkSectionPos subchunkPos = ChunkSectionPos.from(player.getBlockPos());
        if (!subchunkPos.equals(lastPosition)) {
            lastPosition = subchunkPos;

            StructureStart structure = StructureChecker.isInVillage(player);
            if (structure != null) {
                StructureChecker.BellCache bellCache = new StructureChecker.BellCache(world, structure);
                VillageIdentifier village = getState().getOrCreateVillage(
                        world.getRegistryKey(),
                        structure,
                        bellCache::get
                );
                if (village == lastVillage)
                    return;
                lastVillage = village;

                boolean villageIsNew = getState().setVillageVisited(player, village);
                if (villageIsNew) {
                    System.out.format("Found new village %s", village.name());
                    updateSpawn(player, world, bellCache.get());
                } else {
                    System.out.format("Found old village %s", village.name());
                }
                System.out.println();
                ServerNetworking.sendVillage(player, village, villageIsNew);
            } else {
                if (lastVillage != null)
                    ServerNetworking.sendLeftVillage(player, lastVillage);
                lastVillage = null;
            }
        }
    }

    @Unique
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
}
