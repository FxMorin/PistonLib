package ca.fxco.pistonlib.blocks.pistons.movableBlockEntities;

import ca.fxco.api.pistonlib.impl.PistonTicking;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.impl.ILevel;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import ca.fxco.pistonlib.pistonLogic.structureGroups.StructureGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MBEMovingBlockEntity extends BasicMovingBlockEntity {

    protected BlockEntity movedBlockEntity;

    public MBEMovingBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public MBEMovingBlockEntity(PistonFamily family, StructureGroup group, BlockPos pos, BlockState state,
                                BlockState movedState, BlockEntity movedBlockEntity, Direction facing,
                                boolean extending, boolean isSourcePiston) {
        super(family, group, pos, state, movedState, movedBlockEntity, facing, extending, isSourcePiston);

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
    protected void onMovingTick(Direction movingDirection, float speed) {
        super.onMovingTick(movingDirection, speed);
        if (this.movedBlockEntity instanceof PistonTicking pistonTicking) {
            pistonTicking.onMovingTick(this.level, this.movedState, this.worldPosition, movingDirection, this.progressO, speed, false);
        }
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
