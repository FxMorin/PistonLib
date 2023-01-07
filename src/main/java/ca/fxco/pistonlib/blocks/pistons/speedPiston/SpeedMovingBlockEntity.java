package ca.fxco.pistonlib.blocks.pistons.speedPiston;

import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SpeedMovingBlockEntity extends BasicMovingBlockEntity {

    private float speed = 1.0F;

    public SpeedMovingBlockEntity(BlockPos pos, BlockState state) {
        this(1.0F, pos, state);
    }

    public SpeedMovingBlockEntity(float speed, BlockPos pos, BlockState state) {
        this(ModBlockEntities.SPEED_MOVING_BLOCK_ENTITY, speed, pos, state, ModBlocks.STRONG_MOVING_BLOCK);
    }

    public SpeedMovingBlockEntity(BlockEntityType<?> type, float speed, BlockPos pos, BlockState state, SpeedMovingBlock movingBlock) {
        super(type, pos, state, movingBlock);

        this.setSpeed(speed);
    }

    public SpeedMovingBlockEntity(float speed, BlockPos pos, BlockState state, BlockState movedState,
                                  Direction facing, boolean extending, boolean isSourcePiston) {
        this(ModBlockEntities.SPEED_MOVING_BLOCK_ENTITY, speed, pos, state, movedState, facing, extending, isSourcePiston, ModBlocks.STRONG_MOVING_BLOCK);
    }

    public SpeedMovingBlockEntity(BlockEntityType<?> type, float speed, BlockPos pos, BlockState state, BlockState movedState,
                                  Direction dir, boolean extending, boolean isSourcePiston, SpeedMovingBlock movingBlock) {
        super(type, pos, state, movedState, dir, extending, isSourcePiston, movingBlock);

        this.setSpeed(speed);
    }

    private void setSpeed(float speed) {
        if (speed <= 0.0F) {
            throw new IllegalArgumentException("piston speed must be positive!");
        }
        if (speed > 2.0F) {
            throw new IllegalArgumentException("piston speed must be at most 2!");
        }

        this.speed = speed;
    }

    @Override
    protected float speed() {
        return speed;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        if (nbt.contains("speed")) {
            setSpeed(nbt.getFloat("speed"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);

        if (this.speed != 1.0F) {
            nbt.putFloat("speed", this.speed);
        }
    }
}
