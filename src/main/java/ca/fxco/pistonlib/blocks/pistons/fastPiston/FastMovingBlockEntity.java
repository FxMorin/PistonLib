package ca.fxco.pistonlib.blocks.pistons.fastPiston;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import ca.fxco.api.pistonlib.pistonLogic.StructureGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FastMovingBlockEntity extends BasicMovingBlockEntity {

    public FastMovingBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public FastMovingBlockEntity(PistonFamily family, StructureGroup group, BlockPos pos, BlockState state,
                                 BlockState movedState, BlockEntity movedBlockEntity, Direction facing,
                                 boolean extending, boolean isSourcePiston) {
        super(family, group, pos, state, movedState, movedBlockEntity, facing, extending, isSourcePiston);
    }

    @Override
    public float speed() {
        // 2 is enough for the moving block
        // to reach 100% progress in one tick
        return 2.0F;
    }

    @Override
    public void finalTick() {
        if (this.level != null) {
            this.level.removeBlockEntity(this.worldPosition);
            this.setRemoved();

            if (this.level.getBlockState(this.worldPosition).is(this.getFamily().getMoving())) {
                BlockState state = this.isSourcePiston ?
                        Blocks.AIR.defaultBlockState() :
                        Block.updateFromNeighbourShapes(this.movedState, this.level, this.worldPosition);

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
