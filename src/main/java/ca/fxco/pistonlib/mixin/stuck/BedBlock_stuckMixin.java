package ca.fxco.pistonlib.mixin.stuck;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.base.ModTags;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static net.minecraft.world.level.block.BedBlock.PART;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

@Mixin(BedBlock.class)
public abstract class BedBlock_stuckMixin extends Block
        implements ConfigurablePistonStickiness, ConfigurablePistonBehavior {

    @Shadow public abstract BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2,
                                                   LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2);

    public BedBlock_stuckMixin(Properties properties) {
        super(properties);
    }

    private static Direction getNeighbourDirection(BlockState state) {
        BedPart bedPart = state.getValue(PART);
        Direction direction = state.getValue(FACING);
        return bedPart == BedPart.FOOT ? direction : direction.getOpposite();
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
        return Map.of(getNeighbourDirection(state), StickyType.CONDITIONAL);
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction dir) {
        return dir == getNeighbourDirection(state) ? StickyType.CONDITIONAL : StickyType.DEFAULT;
    }

    @Override
    public boolean matchesStickyConditions(BlockState state, BlockState neighborState, Direction dir) {
        return state.is(neighborState.getBlock()) && getNeighbourDirection(neighborState) == dir.getOpposite();
    }
}
