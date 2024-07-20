package de.fisch37.villagerespawn.mixin.common;

import de.fisch37.villagerespawn.VillageIdentifier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.fisch37.villagerespawn.server.VillageUpdateManager.villageUpdate;

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

            lastVillage = villageUpdate(player, lastVillage);
        }
    }
}
