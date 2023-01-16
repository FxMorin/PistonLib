package ca.fxco.pistonlib.blocks.pistons.speedPiston;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SpeedMovingBlockEntity extends BasicMovingBlockEntity {

    private float speed = 1.0F;

    public SpeedMovingBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public SpeedMovingBlockEntity(PistonFamily family, BlockPos pos, BlockState state, BlockState movedState,
                                  BlockEntity movedBlockEntity, Direction facing, boolean extending,
                                  boolean isSourcePiston) {
        super(family, pos, state, movedState, movedBlockEntity, facing, extending, isSourcePiston);

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
    public float speed() {
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
