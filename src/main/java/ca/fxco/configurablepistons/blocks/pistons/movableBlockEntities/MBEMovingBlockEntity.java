package ca.fxco.configurablepistons.blocks.pistons.movableBlockEntities;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.configurablepistons.interfaces.ILevel;

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
        super(pos, state);
    }

    public MBEMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, BlockEntity movedBlockEntity,
                                Direction facing, boolean extending, boolean isSourcePiston,
                                BasicMovingBlock movingBlock) {
        this(pos, state, movedState, movedBlockEntity, facing, extending, isSourcePiston, movingBlock,
                ModBlockEntities.BASIC_MOVING_BLOCK_ENTITY);
    }
    public MBEMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, BlockEntity movedBlockEntity,
                                Direction facing, boolean extending, boolean isSourcePiston,
                                BasicMovingBlock movingBlock, BlockEntityType<?> type) {
        super(pos, state, movedState, facing, extending, isSourcePiston, movingBlock, type);
        this.movedBlockEntity = movedBlockEntity;
    }

    public BlockEntity getMovedBlockEntity() {
        return this.movedBlockEntity;
    }

    @Override
    protected boolean placeMovedBlock() {
        ((ILevel)this.level).prepareBlockEntityPlacement(this.worldPosition, this.movedState, this.movedBlockEntity);
        return super.placeMovedBlock();
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
