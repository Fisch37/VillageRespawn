package de.fisch37.villagerespawn.mixin.common;

import de.fisch37.villagerespawn.server.StructureChecker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Unique
    private ChunkSectionPos lastPosition;

    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity)((Object)this);

        ChunkSectionPos subchunkPos = ChunkSectionPos.from(player.getBlockPos());
        if (!subchunkPos.equals(lastPosition)) {
            lastPosition = subchunkPos;

            if (StructureChecker.isInVillage(player)) {
                System.out.println("I'm in a village!");
            } else {
                System.out.println("I'm not in a village!");
            }
        }
    }
}
