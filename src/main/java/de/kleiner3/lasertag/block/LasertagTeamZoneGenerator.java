package de.kleiner3.lasertag.block;

import de.kleiner3.lasertag.block.entity.LasertagTeamZoneGeneratorBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Étienne Muser
 */
public class LasertagTeamZoneGenerator extends BlockWithEntity {

    protected LasertagTeamZoneGenerator(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LasertagTeamZoneGeneratorBlockEntity lasertagTeamZoneGeneratorBlockEntity) {
            lasertagTeamZoneGeneratorBlockEntity.openScreen(player);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LasertagTeamZoneGeneratorBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {

        // Get the stacks nbt data
        var nbt = BlockItem.getBlockEntityNbt(stack);

        // Sanity check
        if (nbt == null) {
            return;
        }

        var teamName = "Not given";
        var radiusString = "Not given";
        var heightString = "Not given";

        if (nbt.contains("teamName")) {
            teamName = nbt.getString("teamName");
        }

        if (nbt.contains("radius")) {
            radiusString = String.valueOf(nbt.getInt("radius"));
        }

        if (nbt.contains("height")) {
            heightString = String.valueOf(nbt.getInt("height"));
        }

        tooltip.add(Text.literal("Team name: " + teamName));
        tooltip.add(Text.literal("Radius: " + radiusString));
        tooltip.add(Text.literal("Height: " + heightString));
    }
}
