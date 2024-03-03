package de.pewpewproject.lasertag.block;

import de.pewpewproject.lasertag.block.entity.LasertagStartGameButtonBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.StoneButtonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * The lasertag start game button.
 *
 * @author Étienne Muser
 */
public class LasertagStartGameButton extends StoneButtonBlock implements BlockEntityProvider {
    public LasertagStartGameButton(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        // If player does not have permission to start the game
        if (!player.hasPermissionLevel(1)) {
            // Send feedback
            player.sendMessage(Text.translatable("chat.message.start_game_button_not_enough_permissions").formatted(Formatting.RED), true);
            return ActionResult.FAIL;
        }

        if (!world.isClient) {

            // Cast to server world
            var serverWorld = (ServerWorld) world;

            // Get the game managers
            var gameManager = serverWorld.getServerLasertagManager();

            var server = world.getServer();
            var abortReasons = gameManager.startGame(false);

            abortReasons.ifPresent(feedback -> server.getPlayerManager().broadcast(Text.literal("Start game aborted. Reasons:\n" + feedback).formatted(Formatting.RED), false));
        }

        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LasertagStartGameButtonBlockEntity(pos, state);
    }
}
