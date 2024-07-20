package de.fisch37.villagerespawn.server.respawn;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.CollisionView;

import java.util.Optional;

public abstract class RespawnFinder {
    private final static byte SEARCH_RADIUS = 4;
    private final static byte RAYCAST_DISTANCE = 8;

    public static Optional<BlockPos> findRespawnPosition(BlockPos origin, CollisionView world) {
        Raycast.RaycastResult result = new Raycast(origin, Direction.DOWN, RAYCAST_DISTANCE)
                .cast(pos -> canStandOn(pos, world));

        Flooding search = new Flooding(
                result.atRange() ? origin : result.location(),
                pos -> isPosSpawnable(pos, world),
                SEARCH_RADIUS
        );
        return search.search();
    }

    private static boolean isPosSpawnable(BlockPos pos, CollisionView world) {
        return canStandOn(pos.down(), world)
                && isBlockSpawnable(pos, world)
                && isBlockSpawnable(pos.up(), world);
    }

    private static boolean isBlockSpawnable(BlockPos pos, CollisionView world) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        return block.canMobSpawnInside(state);
    }

    private static boolean canStandOn(BlockPos pos, CollisionView world) {
        BlockState state = world.getBlockState(pos);
        return Block.isFaceFullSquare(state.getCollisionShape(world, pos), Direction.UP);
    }

}
