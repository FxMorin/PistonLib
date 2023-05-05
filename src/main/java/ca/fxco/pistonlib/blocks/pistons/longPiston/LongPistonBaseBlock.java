package ca.fxco.pistonlib.blocks.pistons.longPiston;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.helpers.Utils;
import ca.fxco.pistonlib.impl.QLevel;
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
    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        return Utils.hasNeighborSignalExceptFromFacing(level, pos, facing) ||
                (this.getFamily().isQuasi() && ((QLevel)level).hasQuasiNeighborSignal(pos, 1));
    }

    @Override
    protected int getLength(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(EXTENDED)) {
            Direction facing = state.getValue(FACING);
            int length = this.getFamily().getMinLength();

            while (length++ < this.getFamily().getMaxLength()) {
                BlockPos frontPos = pos.relative(facing, length);
                BlockState frontState = level.getBlockState(frontPos);

                if (!frontState.is(this.getFamily().getArm())) {
                    break;
                }
            }

            return length;
        }
        return this.getFamily().getMinLength();
    }
}
