package ca.fxco.pistonlib.blocks.halfBlocks;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.helpers.HalfBlockUtils;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickRules;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroups;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import static ca.fxco.pistonlib.helpers.HalfBlockUtils.SIDES_LIST;

public class HalfSlimeBlock extends Block implements ConfigurablePistonStickiness {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public HalfSlimeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.isSuppressingBounce() || HalfBlockUtils.isOnFacingSide(entity.position(), pos, state)) {
            super.fallOn(level, state, pos, entity, fallDistance);
        } else {
            entity.causeFallDamage(fallDistance, 0.0F, DamageSource.FALL);
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce() || HalfBlockUtils.isOnFacingSide(level, entity.position(), entity.getOnPos())) {
            super.updateEntityAfterFallOn(level, entity);
        } else {
            this.bounce(entity);
        }
    }

    protected void bounce(Entity entity) {
        Vec3 velocity = entity.getDeltaMovement();
        if (velocity.y < 0.0) {
            double bounceStrength = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setDeltaMovement(velocity.x, -velocity.y * bounceStrength, velocity.z);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        double velocityY = Math.abs(entity.getDeltaMovement().y);
        if (velocityY < 0.1 && !(entity.isSteppingCarefully() || HalfBlockUtils.isOnFacingSide(entity.position(), pos, state))) {
            double bounceStrength = 0.4 + velocityY * 0.2;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(bounceStrength, 1.0, bounceStrength));
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
    public @Nullable StickyGroup getStickyGroup() {
        return StickyGroups.SLIME;
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
            return StickRules.test(StickyGroups.SLIME, group);
        }
        return true;
    }
}
