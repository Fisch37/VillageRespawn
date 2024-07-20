package de.fisch37.villagerespawn.server;

import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import static de.fisch37.villagerespawn.VillageRespawn.MOD_ID;
import static de.fisch37.villagerespawn.server.VillageUpdateManager.findSafePosition;

public abstract class PlayerInteractions {
    public static ActionResult setSpawnBellClicked(
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

        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (StructureChecker.isInVillage(serverPlayer.getServerWorld(), blockHitResult.getBlockPos()) == null) {
                serverPlayer.sendMessageToClient(
                        Text.translatable(String.format("%s.bell.no_village_error", MOD_ID)),
                        true
                );
                return ActionResult.CONSUME;
            }
            serverPlayer.setSpawnPoint(
                    world.getRegistryKey(),
                    findSafePosition(world, blockHitResult.getBlockPos()),
                    -player.getYaw(),
                    true,
                    true
            );
        }
        return ActionResult.SUCCESS;
    }
}
