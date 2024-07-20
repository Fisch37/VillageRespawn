package de.fisch37.villagerespawn.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.LinkedList;

public abstract class BeaconUtils {
    private static final Identifier BEAM_TEXTURE = BeaconBlockEntityRenderer.BEAM_TEXTURE;
    private static final LinkedList<RendererTask> TASKS = new LinkedList<>();
    private static final float[] COLOUR = new float[]{ 1, 1, 0 };  // TODO: Make a better colour!

    public static void spawnBeam(World world, BlockPos pos) {
        TASKS.add(new RendererTask(world, pos));
    }

    private static void beaconRendering(WorldRenderContext context) {
        MatrixStack matrices = context.matrixStack();
        Vec3d camPos = context.camera().getPos();
        for (RendererTask task : TASKS) {
            BlockPos pos = task.pos;

            matrices.push();
            matrices.translate(
                    (double)pos.getX() - camPos.getX(),
                    (double)pos.getY() - camPos.getY(),
                    (double)pos.getZ() - camPos.getZ()
            );
            BeaconBlockEntityRenderer.renderBeam(
                    matrices,
                    context.consumers(), // Not null here
                    BEAM_TEXTURE,
                    context.tickDelta(),
                    1.0f,
                    task.world.getTime(),
                    0,  // TODO: Get a good value
                    task.world.getTopY(),
                    COLOUR,
                    0.15f, 0.175f
            );
            matrices.pop();
        }
    }

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(BeaconUtils::beaconRendering);
    }


    private record RendererTask(World world, BlockPos pos) { }
}
