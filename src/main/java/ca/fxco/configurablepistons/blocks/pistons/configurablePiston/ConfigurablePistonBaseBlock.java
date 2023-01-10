package ca.fxco.configurablepistons.blocks.pistons.configurablePiston;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock;
import ca.fxco.configurablepistons.helpers.Utils;
import ca.fxco.configurablepistons.pistonLogic.MotionType;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurablePistonStructureResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class ConfigurablePistonBaseBlock extends BasicPistonBaseBlock {

    protected final boolean verySticky;
    protected final boolean frontPowered;
    protected final int pushLimit;
    protected final boolean quasi;
    protected final boolean slippery;
    protected final boolean canRetractOnExtending;
    protected final boolean canExtendOnRetracting;

    public ConfigurablePistonBaseBlock(PistonType type, Settings settings) {
        super(type);
        verySticky = settings.verySticky;
        frontPowered = settings.frontPowered;
        pushLimit = settings.pushLimit;
        quasi = settings.quasi;
        slippery = settings.slippery;
        canRetractOnExtending = settings.canRetractOnExtending;
        canExtendOnRetracting = settings.canExtendOnRetracting;
    }

    @Override
    public PistonStructureResolver newStructureResolver(Level level, BlockPos pos, Direction facing, boolean extend) {
        return new ConfigurablePistonStructureResolver(this, level, pos, facing, extend);
    }

    @Override
    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        return (frontPowered ? level.hasNeighborSignal(pos) :
                Utils.hasNeighborSignalExceptFromFacing(level, pos, facing)) ||
                (quasi && level.hasNeighborSignal(pos.above()));
    }

    @Override
    protected int getPullType(ServerLevel level, BlockPos pos, Direction facing) {
        return canRetractOnExtending ? super.getPullType(level, pos, facing) : MotionType.NONE;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(this) && level.getBlockEntity(pos) == null) {
            this.checkIfExtend(level, pos, state);
            if (slippery && !level.isClientSide) {
                level.scheduleTick(pos, this, SLIPPERY_DELAY);
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (slippery && !level.isClientSide())
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
        return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (slippery && BaseSlipperyBlock.calculateDistance(level, pos) >= MAX_DISTANCE)
            FallingBlockEntity.fall(level, pos, state.setValue(EXTENDED,false));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return !slippery || BaseSlipperyBlock.calculateDistance(level, pos) < MAX_DISTANCE;
    }

    // All settings are by default the vanilla piston settings
    public static class Settings {

        protected boolean verySticky = false;
        protected boolean frontPowered = false;
        protected boolean translocation = false;
        protected boolean slippery = false;
        protected boolean quasi = true;
        protected int pushLimit = 12;
        protected float extendingSpeed = 1;
        protected float retractingSpeed = 1;
        protected boolean canRetractOnExtending = true;
        protected boolean canExtendOnRetracting = false;

        public Settings verySticky() {
            this.verySticky = true;
            return this;
        }

        public Settings frontPowered() {
            this.frontPowered = true;
            return this;
        }

        public Settings translocation() {
            this.translocation = true;
            return this;
        }

        public Settings slippery() {
            this.slippery = true;
            return this;
        }

        public Settings noQuasi() {
            this.quasi = false;
            return this;
        }

        public Settings pushLimit(int pushLimit) {
            this.pushLimit = pushLimit;
            return this;
        }

        public Settings speed(float generalSpeed) {
            this.extendingSpeed = generalSpeed;
            this.retractingSpeed = generalSpeed;
            return this;
        }

        public Settings speed(float extendingSpeed, float retractingSpeed) {
            this.extendingSpeed = extendingSpeed;
            this.retractingSpeed = retractingSpeed;
            return this;
        }

        public Settings canRetractOnExtending(boolean enable) {
            this.canRetractOnExtending = enable;
            return this;
        }

        public Settings canExtendOnRetracting(boolean enable) {
            this.canExtendOnRetracting = enable;
            return this;
        }
    }
}
