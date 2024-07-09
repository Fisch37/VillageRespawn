package de.fisch37.villagerespawn;

import de.fisch37.villagerespawn.server.ServerState;
import de.fisch37.villagerespawn.server.VillageIdentifier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.fisch37.villagerespawn.server.ServerUtils.findSafePosition;

public class VillageRespawn implements ModInitializer {
    public final static String MOD_ID = "village_respawn";
    public final static Logger LOG = LoggerFactory.getLogger(VillageRespawn.class);

    private static ServerState STATE;

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        ServerWorldEvents.LOAD.register(this::initialiseServer);
        UseBlockCallback.EVENT.register(this::setSpawnBellClicked);
    }

    private ActionResult setSpawnBellClicked(
            PlayerEntity player,
            World world,
            Hand hand,
            BlockHitResult blockHitResult
    ) {
        if (!player.isPartOfGame() || !player.isSneaking())
            return ActionResult.PASS;
        BlockState block = world.getBlockState(blockHitResult.getBlockPos());
        if (!(block.getBlock() instanceof BellBlock))
            return ActionResult.PASS;

        if (player instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) player).setSpawnPoint(
                    world.getRegistryKey(),
                    findSafePosition(world, blockHitResult.getBlockPos()),
                    -player.getYaw(),
                    true,
                    true
            );
        }
        return ActionResult.SUCCESS;
    }

    public void initialiseServer(MinecraftServer server, ServerWorld world) {
        STATE = ServerState.getServerState(server);
        VillageIdentifier.initialise(world, STATE);
    }

    public static ServerState getState() {
        return STATE;
    }
}
