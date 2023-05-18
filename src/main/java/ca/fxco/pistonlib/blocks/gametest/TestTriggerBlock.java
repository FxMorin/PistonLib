package ca.fxco.pistonlib.blocks.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

public class TestTriggerBlock extends Block implements GameMasterBlock {

    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public TestTriggerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(INVERTED, false));
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos,
                                Block block, BlockPos blockPos2, boolean bl) {
        if (!blockState.getValue(POWERED) && level.hasNeighborSignal(blockPos)) {
            level.setBlock(blockPos, blockState.setValue(POWERED, true), 4);
        }
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player,
                                 InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        blockState = blockState.cycle(INVERTED).setValue(POWERED, false);
        level.setBlock(blockPos, blockState, 4);
        return InteractionResult.CONSUME;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(INVERTED, POWERED);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.BLOCK;
    }
}
