package de.kleiner3.lasertag.block;

import de.kleiner3.lasertag.block.entity.LasertagStartGameButtonBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.StoneButtonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
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
        if (!world.isClient) {
            var server = world.getServer();
            var abortReasons = server.getLasertagServerManager().startGame(false);

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
