package ca.fxco.pistonlib.blocks.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class PulseStateBlock extends Block implements EntityBlock {

    public PulseStateBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PulseStateBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof PulseStateBlockEntity pulseStateBlockEntity) {
            if (level.isClientSide) {
                return pulseStateBlockEntity.usedBy(player) ?
                        InteractionResult.sidedSuccess(true) : InteractionResult.PASS;
            }
            return InteractionResult.sidedSuccess(false);
        }
        return InteractionResult.PASS;
    }
}
