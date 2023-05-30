package ca.fxco.pistonlib.mixin.stuck;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.base.ModTags;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static net.minecraft.world.level.block.DoorBlock.HALF;

@Mixin(DoorBlock.class)
public abstract class DoorBlock_stuckMixin extends Block
        implements ConfigurablePistonStickiness, ConfigurablePistonBehavior {

    public DoorBlock_stuckMixin(Properties properties) {
        super(properties);
    }

    private static Direction getStickyDirection(BlockState state) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        return doubleBlockHalf == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN;
    }

    @Inject(
            method = "updateShape",
            at = @At("RETURN"),
            cancellable = true
    )
    private void lookWithinMovingPistons(BlockState blockState, Direction dir, BlockState blockState2,
                                         LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2,
                                         CallbackInfoReturnable<BlockState> cir) {
        if (cir.getReturnValue().isAir() && blockState2.is(ModTags.MOVING_PISTONS)) {
            BlockEntity entity = levelAccessor.getBlockEntity(blockPos2);
            if (entity instanceof PistonMovingBlockEntity mpbe && mpbe.progress >= 1.0F) {
                cir.setReturnValue(updateShape(blockState, dir, mpbe.movedState, levelAccessor, blockPos, blockPos2));
            }
        }
    }

    @Override
    public boolean usesConfigurablePistonBehavior() {
        return PistonLibConfig.stuckDoubleBlocks;
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return PistonLibConfig.stuckDoubleBlocks;
    }

    @Override
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return Map.of(getStickyDirection(state), StickyType.CONDITIONAL);
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction dir) {
        return dir == getStickyDirection(state) ? StickyType.CONDITIONAL : StickyType.DEFAULT;
    }

    @Override
    public boolean matchesStickyConditions(BlockState state, BlockState neighborState, Direction dir) {
        return state.is(neighborState.getBlock()) && getStickyDirection(neighborState) == dir.getOpposite();
    }
}
