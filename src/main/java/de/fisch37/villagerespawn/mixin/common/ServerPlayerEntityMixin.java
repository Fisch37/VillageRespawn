package de.fisch37.villagerespawn.mixin.common;

import de.fisch37.villagerespawn.server.ServerNetworking;
import de.fisch37.villagerespawn.server.StructureChecker;
import de.fisch37.villagerespawn.server.VillageIdentifier;
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
        /*
            TODO: Determine if a player has visited a given village
                This could be done using the StructureStart.
                Position and bounding box ought to uniquely identify a structure.
                Position would probably suffice.
                Reason I want this is to change behaviour if a village was visited before.
                    New villages should always set spawn, but old ones should notify and maybe ask?
                    or players might need to ring the bell
                I could also give each village a unique name if I wanted to?
                    Would be good for immersion, can use the bounding box and position as a random seed
                        (along with the... seed)
                Maybe integrate with a waypoint system at some other point?
                    Xaero doesn't really want to give people an API though
                    I can definitely do JourneyMap though!

         */

        ServerPlayerEntity player = (ServerPlayerEntity)((Object)this);
        ServerWorld world = player.getServerWorld();
        assert world != null;

        ChunkSectionPos subchunkPos = ChunkSectionPos.from(player.getBlockPos());
        if (!subchunkPos.equals(lastPosition)) {
            lastPosition = subchunkPos;

            StructureStart structure = StructureChecker.isInVillage(player);
            if (structure != null) {
                VillageIdentifier village = getState().getOrCreateVillage(structure);
                if (village == lastVillage)
                    return;
                lastVillage = village;

                boolean villageIsNew = getState().setVillageVisited(player, village);
                if (villageIsNew) {
                    System.out.format("Found new village %s", village.name());
                    updateSpawn(player, world, structure);
                } else {
                    System.out.format("Found old village %s", village.name());
                }
                System.out.println();
                ServerNetworking.sendVillage(player, village, villageIsNew);
            } else {
                if (lastVillage != null)
                    ServerNetworking.sendLeftVillage(player);
                lastVillage = null;
            }
        }
    }

    @Unique
    private static void updateSpawn(ServerPlayerEntity player, ServerWorld world, StructureStart structure) {
        BlockPos spawn = StructureChecker.getBellIn(world, structure);
        if (spawn != null) {
            spawn = findSafePosition(world, spawn);
        }
        if (spawn == null) {
            LOG.warn("Could not find bell in village or was obscured. Attempting to use player position");
            spawn = findSafePosition(world, player.getBlockPos());
            if (spawn == null) {
                LOG.error("Could not generate valid spawnpoint for village :c");
                return;
            }
        }
        // FIXME: Fix spawnpoint obstruction
        player.setSpawnPoint(
                world.getRegistryKey(),
                spawn,
                0,
                true,
                true
        );
    }
}
