package de.fisch37.villagerespawn.server.respawn;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.function.Predicate;

record Raycast(
        BlockPos origin,
        Direction direction,
        int maximumDistance
) {
    RaycastResult cast(Predicate<BlockPos> predicate) {
        BlockPos pos = origin;
        boolean hit = false;
        for (int i = 0; i < maximumDistance; i++) {
            if (predicate.test(pos)) {
                hit = true;
                break;
            }

            pos = pos.add(direction.getVector());
        }

        return new RaycastResult(pos, !hit);
    }

    record RaycastResult(
            BlockPos location,
            boolean atRange
    ) { }
}
