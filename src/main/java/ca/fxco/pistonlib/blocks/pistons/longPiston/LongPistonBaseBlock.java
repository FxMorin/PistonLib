package ca.fxco.pistonlib.blocks.pistons.longPiston;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

public class LongPistonBaseBlock extends BasicPistonBaseBlock {

    public LongPistonBaseBlock(PistonFamily family, PistonType type) {
        this(family, type, FabricBlockSettings.copyOf(Blocks.PISTON));
    }

    public LongPistonBaseBlock(PistonFamily family, PistonType type, Properties properties) {
        super(family, type, properties);
    }

    @Override
    protected int getLength(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(EXTENDED)) {
            Direction facing = state.getValue(FACING);
            int length = this.family.getMinLength();

            while (length++ < this.family.getMaxLength()) {
                BlockPos frontPos = pos.relative(facing, length);
                BlockState frontState = level.getBlockState(frontPos);

                if (!frontState.is(this.family.getArm())) {
                    break;
                }
            }

            return length;
        } else {
            return this.family.getMinLength();
        }
    }
}
