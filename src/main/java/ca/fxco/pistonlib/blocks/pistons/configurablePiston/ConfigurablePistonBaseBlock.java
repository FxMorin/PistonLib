package ca.fxco.pistonlib.blocks.pistons.configurablePiston;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock;
import ca.fxco.pistonlib.helpers.Utils;
import ca.fxco.pistonlib.impl.QLevel;
import ca.fxco.pistonlib.pistonLogic.MotionType;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.BasicStructureResolver;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class ConfigurablePistonBaseBlock extends BasicPistonBaseBlock {

    public ConfigurablePistonBaseBlock(PistonFamily family, PistonType type) {
        super(family, type);
    }

    @Override
    public BasicStructureResolver newStructureResolver(Level level, BlockPos pos, Direction facing, int length, boolean extend) {
        return new BasicStructureResolver(this, level, pos, facing, length, extend);
    }

    @Override
    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        return (this.getFamily().isFrontPowered() ? level.hasNeighborSignal(pos) :
                Utils.hasNeighborSignalExceptFromFacing(level, pos, facing)) ||
                (this.getFamily().isQuasi() && ((QLevel)level).hasQuasiNeighborSignal(pos, 1));
    }

    @Override
    protected int getPullType(ServerLevel level, BlockPos pos, Direction facing, int length) {
        return this.getFamily().isRetractOnExtending() ? super.getPullType(level, pos, facing, length) : MotionType.NONE;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(this) && level.getBlockEntity(pos) == null) {
            this.checkIfExtend(level, pos, state);
            if (this.getFamily().isSlippery() && !level.isClientSide) {
                level.scheduleTick(pos, this, SLIPPERY_DELAY);
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (this.getFamily().isSlippery() && !level.isClientSide())
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
        return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.getFamily().isSlippery() && BaseSlipperyBlock.calculateDistance(level, pos) >= MAX_DISTANCE)
            FallingBlockEntity.fall(level, pos, state.setValue(EXTENDED,false));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return !this.getFamily().isSlippery() || BaseSlipperyBlock.calculateDistance(level, pos) < MAX_DISTANCE;
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
