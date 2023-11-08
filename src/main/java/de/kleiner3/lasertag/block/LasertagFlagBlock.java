package de.kleiner3.lasertag.block;

import de.kleiner3.lasertag.block.entity.LasertagFlagBlockEntity;
import de.kleiner3.lasertag.entity.Entities;
import de.kleiner3.lasertag.item.Items;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.gamemode.implementation.CaptureTheFlagGameMode;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.management.settings.valuetypes.CTFFlagHoldingPlayerVisibility;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import net.minecraft.block.Blocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * the lasertag flag block
 *
 * @author Étienne Muser
 */
public class LasertagFlagBlock extends Block implements BlockEntityProvider {
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    public LasertagFlagBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        // If is not on server
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.PASS;
        }

        // Get the managers
        var gameManager = LasertagGameManager.getInstance();
        var flagManager = gameManager.getFlagManager();
        var teamManager = gameManager.getTeamManager();
        var settingsManager = gameManager.getSettingsManager();

        // Get the stack the player is holding
        var handStack = player.getStackInHand(hand);

        // If player is not holding a flag
        if (!handStack.isOf(Items.LASERTAG_FLAG)) {
            return ActionResult.PASS;
        }

        // Get the team of the flag the player is currently holding
        var teamOptional = flagManager.getPlayerHoldingFlagTeam(player.getUuid());

        // If player is currently not holding a flag
        if (teamOptional.isEmpty()) {
            return ActionResult.PASS;
        }

        // Get the block entity of the flag
        var blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof LasertagFlagBlockEntity flagBlockEntity)) {
            return ActionResult.PASS;
        }

        // Get the team of the flag
        var flagTeamOptional = teamManager.getTeamConfigManager().getTeamOfName(flagBlockEntity.getTeamName());

        // If flag has no team
        if (flagTeamOptional.isEmpty()) {
            return ActionResult.PASS;
        }

        // Get the team of the player
        var playersTeam = teamManager.getTeamOfPlayer(player.getUuid());

        // If player has no team
        if (playersTeam.isEmpty()) {
            return ActionResult.PASS;
        }

        // If player is not clicking his own flag
        if (!flagTeamOptional.get().equals(playersTeam.get())) {
            return ActionResult.PASS;
        }

        // Capture flag of team
        flagManager.flagCaptured(serverWorld, teamOptional.get(), player.getUuid());

        // Take flag away from player
        handStack.decrement(1);

        // Play score sound
        ServerEventSending.sendPlayerSoundEvent((ServerPlayerEntity)player, NetworkingConstants.PLAY_PLAYER_SCORED_SOUND);

        // If setting player visibility is set to glow
        if (settingsManager.getEnum(SettingDescription.CTF_FLAG_HOLDING_PLAYER_VISIBILITY) == CTFFlagHoldingPlayerVisibility.GLOW) {
            // Set player to not glow
            player.setGlowing(false);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {

        // If is on server
        if (!world.isClient) {

            // Get the block entity
            var blockEntity = world.getBlockEntity(pos);

            // If is not flag entity
            if (!(blockEntity instanceof LasertagFlagBlockEntity flagEntity)) {
                return;
            }

            // Get the team name
            var teamName = flagEntity.getTeamName();

            // Give the player the flag
            this.givePlayerTheFlag(teamName, player);

            // Get the manager
            var gameManager = LasertagGameManager.getInstance();

            // Get the team
            var teamOptional = gameManager.getTeamManager().getTeamConfigManager().getTeamOfName(teamName);

            teamOptional.ifPresent(team -> {

                // Get the game mode
                var gameMode = gameManager.getGameModeManager().getGameMode();

                // If is not capture the flag game mode
                if (!(gameMode instanceof CaptureTheFlagGameMode ctfGameMode)) {
                    return;
                }

                // Set player picked up flag
                gameManager.getFlagManager().playerPickedUpFlag((ServerWorld)world, (ServerPlayerEntity) player, team);

                // Remove all other flags of that team
                ctfGameMode.removeFlags((ServerWorld) world, team);
            });

            // If is upper half of flag
            if (state.get(HALF) == DoubleBlockHalf.UPPER) {

                // Get the block pos of the lower half
                var lowerHalfBlockPos = pos.down();

                // Get the block state of the lower half
                var lowerHalfBlockState = world.getBlockState(lowerHalfBlockPos);

                // If block state at the position of the lower half is in fact the lower half
                if (lowerHalfBlockState.isOf(state.getBlock()) && lowerHalfBlockState.get(HALF) == DoubleBlockHalf.LOWER) {

                    // Set the lower half to air
                    world.setBlockState(lowerHalfBlockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);

                    // Sync
                    world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, lowerHalfBlockPos, Block.getRawIdFromState(lowerHalfBlockState));
                }
            }
        }

        // Break the block
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        var blockPos = pos.up();
        world.setBlockState(blockPos, this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);

        // Get the block entity tag nbt data
        var nbt = BlockItem.getBlockEntityNbt(itemStack);

        // get the block entity of the upper half
        var blockEntity = world.getBlockEntity(blockPos);

        // Set the nbt of the block entity
        blockEntity.readNbt(nbt);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf doubleBlockHalf = state.get(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP) && (!neighborState.isOf(this) || neighborState.get(HALF) == doubleBlockHalf)) {
            return Blocks.AIR.getDefaultState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(HALF) != DoubleBlockHalf.UPPER) {
            return super.canPlaceAt(state, world, pos);
        } else {
            BlockState blockState = world.getBlockState(pos.down());
            return blockState.isOf(this) && blockState.get(HALF) == DoubleBlockHalf.LOWER;
        }
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LasertagFlagBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        var nbt = BlockItem.getBlockEntityNbt(stack);

        // Sanity check
        if (nbt == null) {
            return;
        }

        tooltip.add(Text.literal("Team: " + nbt.getString(LasertagFlagBlockEntity.TEAM_NAME_NBT_KEY)));
    }

    private void givePlayerTheFlag(String teamName, PlayerEntity player) {

        // Create an item stack for the flag
        var itemStack = new ItemStack(Items.LASERTAG_FLAG, 1);

        // Create a new nbt compound
        var nbt = new NbtCompound();

        // Set team name on the nbt
        nbt.putString(LasertagFlagBlockEntity.TEAM_NAME_NBT_KEY, teamName);

        // Set the nbt data
        BlockItem.setBlockEntityNbt(itemStack, Entities.LASERTAG_FLAG_BLOCK_ENTITY, nbt);

        // Give the player the item stack
        player.getInventory().insertStack(itemStack);
    }
}
