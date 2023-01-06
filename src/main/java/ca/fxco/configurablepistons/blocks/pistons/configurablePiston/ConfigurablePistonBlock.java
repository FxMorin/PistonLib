package ca.fxco.configurablepistons.blocks.pistons.configurablePiston;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurablePistonHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class ConfigurablePistonBlock extends BasicPistonBlock {

    protected final boolean verySticky;
    protected final boolean frontPowered;
    protected final int pushLimit;
    protected final boolean quasi;
    protected final boolean slippery;
    protected final boolean canRetractOnExtending;
    protected final boolean canExtendOnRetracting;

    public ConfigurablePistonBlock(boolean sticky, Settings settings) {
        super(sticky);
        verySticky = settings.verySticky;
        frontPowered = settings.frontPowered;
        pushLimit = settings.pushLimit;
        quasi = settings.quasi;
        slippery = settings.slippery;
        canRetractOnExtending = settings.canRetractOnExtending;
        canExtendOnRetracting = settings.canExtendOnRetracting;
    }

    public ConfigurablePistonHandler getPistonHandler(World world, BlockPos pos, Direction dir, boolean retract) {
        return new ConfigurablePistonHandler(world, pos, dir, retract, this.pushLimit);
    }

    @Override
    public boolean shouldExtend(World world, BlockPos pos, Direction pistonFace) {
        for(Direction dir : Direction.values())
            if ((frontPowered || dir != pistonFace) && world.isEmittingRedstonePower(pos.offset(dir), dir))
                return true;
        if (world.isEmittingRedstonePower(pos, Direction.DOWN))
            return true;
        if (!quasi) return false;
        BlockPos blockPos = pos.up();
        for(Direction dir : Direction.values())
            if (dir != Direction.DOWN && world.isEmittingRedstonePower(blockPos.offset(dir), dir))
                return true;
        return false;
    }

    @Override
    public void tryMove(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        boolean shouldExtend = this.shouldExtend(world, pos, direction);
        if (shouldExtend && !state.get(EXTENDED)) { // retracting
            if ((getPistonHandler(world, pos, direction, true)).calculatePullPush(false))
                world.addSyncedBlockEvent(pos, this, 0, direction.getId());
        } else if (!shouldExtend && state.get(EXTENDED)) { // extending
            BlockPos blockPos = pos.offset(direction, 2);
            BlockState blockState = world.getBlockState(blockPos);
            int i = 1;
            if (blockState.isOf(EXTENSION_BLOCK) && blockState.get(FACING) == direction) {
                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                if (blockEntity instanceof PistonBlockEntity pistonBlockEntity && pistonBlockEntity.isExtending()) { // Is already expanded
                    if (!canRetractOnExtending) return;
                    if ((pistonBlockEntity.getProgress(0.0F) < 0.5F ||
                            world.getTime() == pistonBlockEntity.getSavedWorldTime() ||
                            ((ServerWorld) world).isInBlockTick())) {
                        i = 2;
                    }
                }
            }
            world.addSyncedBlockEvent(pos, this, i, direction.getId());
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient && world.getBlockEntity(pos) == null) {
            this.tryMove(world, pos, state);
            if (slippery)
                world.scheduleBlockTick(pos, this, SLIPPERY_DELAY);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (slippery && !world.isClient())
            world.scheduleBlockTick(pos, this, SLIPPERY_DELAY);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (slippery && BaseSlipperyBlock.calculateDistance(world, pos) >= MAX_DISTANCE)
            FallingBlockEntity.spawnFromBlock(world, pos, state.with(EXTENDED,false));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return !slippery || BaseSlipperyBlock.calculateDistance(world, pos) < MAX_DISTANCE;
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
