package de.fisch37.villagerespawn.server;

import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public abstract class ServerUtils {
    public static @Nullable BlockPos findSafePosition(CollisionView world, BlockPos pos) {
        return RespawnAnchorBlock
                .findRespawnPosition(EntityType.PLAYER, world, pos)
                .map(BlockPos::ofFloored)
                .orElse(null);
    }
}
