package ca.fxco.pistonlib.blocks.pistons.configurablePiston;

import ca.fxco.pistonlib.blocks.pistons.speedPiston.SpeedMovingBlockEntity;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import ca.fxco.pistonlib.pistonLogic.structureGroups.StructureGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ConfigurableMovingBlockEntity extends SpeedMovingBlockEntity {

    public ConfigurableMovingBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public ConfigurableMovingBlockEntity(PistonFamily family, StructureGroup group, BlockPos pos, BlockState state,
                                         BlockState movedState, BlockEntity movedBlockEntity, Direction facing,
                                         boolean extending, boolean isSourcePiston) {
        super(family, group, pos, state, movedState, movedBlockEntity, facing, extending, isSourcePiston);
    }
}
