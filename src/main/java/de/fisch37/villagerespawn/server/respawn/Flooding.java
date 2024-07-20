package de.fisch37.villagerespawn.server.respawn;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Predicate;

// WARNING: BLACK MAGIC AHEAD
class Flooding {
    private final BlockPos center;
    private final Vec3i localCenter;
    private final Predicate<BlockPos> query;
    private final byte radius;

    private final @Nullable Node[][][] area;
    // A better nerd might do this in an array if they knew maximum number of source nodes.
    // I don't though, and I spent considerable time trying.
    private LinkedList<Vec3i> sources = new LinkedList<>();
    private LinkedList<Vec3i> temp_sources = new LinkedList<>();

    protected Flooding(
            BlockPos center,
            Predicate<BlockPos> query,
            byte radius
    ) {
        this.center = center;
        this.query = query;
        this.radius = radius;

        final int dim = 2*radius + 1;
        area = new Node[dim][dim][dim];
        localCenter = new Vec3i(radius, radius, radius);
        area[radius][radius][radius] = new Node(radius);
        sources.add(localCenter);
    }

    public Optional<BlockPos> search() {
        for (byte b = 0; b < radius; b++) {
            @Nullable Vec3i searchResult = step();
            if (searchResult != null)
                return Optional.of(localToGlobal(searchResult));
        }
        for (Vec3i pos : sources) {
            if (testPos(pos))
                return Optional.of(localToGlobal(pos));
        }
        return Optional.empty();
    }

    private @Nullable Vec3i step() {
        // Redirection shenanigans
        LinkedList<Vec3i> local_sources = sources;
        sources = temp_sources;
        for (Vec3i pos : local_sources) {
            if (testPos(pos)) {
                return pos;
            }
            propagateNode(pos);
        }
        local_sources.clear();
        temp_sources = local_sources;
        return null;
    }

    private @Nullable Node get(Vec3i pos) {
        return area[pos.getX()][pos.getY()][pos.getZ()];
    }

    private void set(Vec3i pos, Node val) {
        area[pos.getX()][pos.getY()][pos.getZ()] = val;
    }

    private boolean testPos(Vec3i pos) {
        return query.test(localToGlobal(pos));
    }

    protected BlockPos localToGlobal(Vec3i local) {
        return center.add(local).subtract(localCenter);
    }


    private void propagateNode(Vec3i pos) {
        Node node = get(pos);
        assert node != null;
        for (byte b = 0; b < Node.CHILD_DIRECTIONS.length; b++) {
            Vec3i direction = node.children[b];
            if (direction == null) continue;
            Vec3i childPos = pos.add(direction);

            Node child = get(childPos);
            if (child == null) {
                child = new Node(node.level - 1);
                set(childPos, child);
                sources.add(childPos);
            }
            child.removeOrigin(b);
        }
    }

    private record Node(int level, @Nullable Vec3i[] children) {
        private Node(int level) {
            this(level, CHILD_DIRECTIONS.clone());
        }

        private void removeOrigin(byte dirIndex) {
            children[getInverse(dirIndex)] = null;
        }

        private final static Vec3i[] CHILD_DIRECTIONS = new Vec3i[]{
                new Vec3i(1,0,0),
                new Vec3i(-1,0,0),
                new Vec3i(0,0,1),
                new Vec3i(0,0,-1),
                new Vec3i(0,1,0),
                new Vec3i(0,-1,0),
        };

        private static byte getInverse(byte dirIndex) {
            // This thing is cursed but the math checks out
            if ((dirIndex & 1) == 0) {
                return (byte) (dirIndex + 1);
            } else {
                return (byte) (dirIndex - 1);
            }
        }
    }
}
