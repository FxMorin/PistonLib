package ca.fxco.configurablepistons.blocks.pistons.movableBlockEntities;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.configurablepistons.mixin.accessors.BlockEntityAccessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MBEMovingBlockEntity extends BasicMovingBlockEntity {

    protected BlockEntity movedBlockEntity;

    public MBEMovingBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.BASIC_MOVING_BLOCK_ENTITY, pos, state, ModBlocks.BASIC_MOVING_BLOCK);
    }

    public MBEMovingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, BasicMovingBlock movingBlock) {
        super(type, pos, state, movingBlock);
    }

    public MBEMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, BlockEntity movedBlockEntity,
                                  Direction facing, boolean extending, boolean isSourcePiston) {
        this(ModBlockEntities.BASIC_MOVING_BLOCK_ENTITY, pos, state, movedState, movedBlockEntity, facing, extending,
            isSourcePiston, ModBlocks.BASIC_MOVING_BLOCK);
    }
    public MBEMovingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, BlockState movedState,
                                  BlockEntity movedBlockEntity, Direction facing, boolean extending, boolean isSourcePiston,
                                  BasicMovingBlock movingBlock) {
        super(type, pos, state, movedState, facing, extending, isSourcePiston, movingBlock);
    }

    public BlockEntity getMovedBlockEntity() {
        return this.movedBlockEntity;
    }

    @Override
    protected boolean placeMovedBlock() {
        boolean success = super.placeMovedBlock();

        if (success && this.movedBlockEntity != null) {
            ((BlockEntityAccessor)this.movedBlockEntity).setPos(this.worldPosition);
            this.level.setBlockEntity(this.movedBlockEntity);
        }

        return success;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        if (this.movedState.hasBlockEntity() && nbt.contains("blockEntity")) {
            EntityBlock movedBlock = (EntityBlock)this.movedState.getBlock();
            this.movedBlockEntity = movedBlock.newBlockEntity(this.worldPosition, this.movedState);

            this.movedBlockEntity.load(nbt.getCompound("blockEntity"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);

        if (this.movedState.hasBlockEntity() && this.movedBlockEntity != null) {
            nbt.put("blockEntity", this.movedBlockEntity.saveWithoutMetadata());
        }
    }
}
