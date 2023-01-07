package ca.fxco.configurablepistons.blocks.pistons.fastPiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FastMovingBlockEntity extends BasicMovingBlockEntity {

    public FastMovingBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.FAST_MOVING_BLOCK_ENTITY, pos, state, ModBlocks.FAST_MOVING_BLOCK);
    }

    public FastMovingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, FastMovingBlock movingBlock) {
        super(type, pos, state, movingBlock);
    }
    public FastMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, Direction facing,
                                 boolean extending, boolean isSourcePiston) {
        this(ModBlockEntities.FAST_MOVING_BLOCK_ENTITY, pos, state, movedState, facing, extending, isSourcePiston, ModBlocks.FAST_MOVING_BLOCK);
    }

    public FastMovingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, BlockState movedState,
                                 Direction facing, boolean extending, boolean isSourcePiston, FastMovingBlock movingBlock) {
        super(type, pos, state, movedState, facing, extending, isSourcePiston, movingBlock);
    }

    @Override
    protected float speed() {
        // 2 is enough for the moving block
        // to reach 100% progress in one tick
        return 2.0F;
    }

    @Override
    public void finalTick() {
        if (this.level != null) {
            this.level.removeBlockEntity(this.worldPosition);
            this.setRemoved();

            if (this.level.getBlockState(this.worldPosition).is(MOVING_BLOCK)) {
                BlockState state = this.isSourcePiston ?
                    Blocks.AIR.defaultBlockState() : Block.updateFromNeighbourShapes(this.movedState, this.level, this.worldPosition);

                this.level.setBlock(this.worldPosition, state, Block.UPDATE_ALL);
                this.level.neighborChanged(this.worldPosition, state.getBlock(), this.worldPosition);
            }
        }
    }

    @Override
    public void tick() {
        // first call moves entities and increments progress to 1.0
        // second call then finishes the movement and places the block
        super.tick();
        super.tick();
    }
}