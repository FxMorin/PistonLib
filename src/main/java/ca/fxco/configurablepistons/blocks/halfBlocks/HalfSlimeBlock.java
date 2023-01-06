package ca.fxco.configurablepistons.blocks.halfBlocks;

import java.util.Map;

import ca.fxco.configurablepistons.helpers.HalfBlockUtils;
import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

import static ca.fxco.configurablepistons.helpers.HalfBlockUtils.SIDES_LIST;

//TODO: Remove bouncing if not centered on the slime part. Remove bouncing when jumping on the back
public class HalfSlimeBlock extends SlimeBlock implements ConfigurablePistonStickiness {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public HalfSlimeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (state.getValue(FACING) != Direction.UP || entity.isSuppressingBounce()) { //TODO: Add half block effects when facing sideways
            super.fallOn(level, state, pos, entity, fallDistance);
        } else {
            entity.causeFallDamage(fallDistance, 0.0F, DamageSource.FALL);
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) { //TODO: Add half block effects when facing sideways
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(level, entity);
        } else {
            this.bounceUp(entity);
        }

    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (state.getValue(FACING) == Direction.UP) { //TODO: Add half block effects when facing sideways
            double d = Math.abs(entity.getDeltaMovement().y);
            if (d < 0.1 && !entity.isSteppingCarefully()) {
                double e = 0.4 + d * 0.2;
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(e, 1.0, e));
            }
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return HalfBlockUtils.getSlabShape(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return SIDES_LIST[state.getValue(FACING).ordinal()];
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction dir) {
        Direction facing = state.getValue(FACING);
        if (dir == facing) { // front
            return StickyType.STICKY;
        } else if (dir == facing.getOpposite()) { // back
            return StickyType.DEFAULT;
        }
        return StickyType.CONDITIONAL;
    }

    // Only the sides call the conditional check
    @Override
    public boolean matchesStickyConditions(BlockState state, BlockState neighborState, Direction dir) {
        if (neighborState.getBlock() == this) { // Block attempting to stick another half slime block
            Direction facing = state.getValue(FACING);
            Direction neighborFacing = neighborState.getValue(FACING);
            return facing == neighborFacing || facing.getOpposite() != neighborFacing;
        }
        StickyGroup group = ((ConfigurablePistonStickiness)neighborState.getBlock()).getStickyGroup();
        if (group != null) {
            return StickyGroup.canStick(StickyGroup.SLIME, group);
        }
        return false;
    }
}
