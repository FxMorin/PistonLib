package ca.fxco.configurablepistons.blocks.halfBlocks;

import ca.fxco.configurablepistons.helpers.HalfBlockUtils;
import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlimeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Map;

import static ca.fxco.configurablepistons.helpers.HalfBlockUtils.SIDES_LIST;

//TODO: Remove bouncing if not centered on the slime part. Remove bouncing when jumping on the back
public class HalfSlimeBlock extends SlimeBlock implements ConfigurablePistonStickiness {

    public static final DirectionProperty FACING = Properties.FACING;

    public HalfSlimeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (state.get(FACING) != Direction.UP || entity.bypassesLandingEffects()) { //TODO: Add half block effects when facing sideways
            super.onLandedUpon(world, state, pos, entity, fallDistance);
        } else {
            entity.handleFallDamage(fallDistance, 0.0F, DamageSource.FALL);
        }
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) { //TODO: Add half block effects when facing sideways
        if (entity.bypassesLandingEffects()) {
            super.onEntityLand(world, entity);
        } else {
            this.bounce(entity);
        }

    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (state.get(FACING) == Direction.UP) { //TODO: Add half block effects when facing sideways
            double d = Math.abs(entity.getVelocity().y);
            if (d < 0.1 && !entity.bypassesSteppingEffects()) {
                double e = 0.4 + d * 0.2;
                entity.setVelocity(entity.getVelocity().multiply(e, 1.0, e));
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return HalfBlockUtils.getSlabShape(state.get(FACING));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return SIDES_LIST[state.get(FACING).ordinal()];
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction direction) {
        Direction facing = state.get(FACING);
        if (direction == facing) { // front
            return StickyType.STICKY;
        } else if (direction == facing.getOpposite()) {
            return StickyType.DEFAULT;
        }
        return StickyType.CONDITIONAL;
    }

    // Only the sides call the conditional check
    @Override
    public boolean matchesStickyConditions(BlockState state, BlockState adjState, Direction direction) {
        if (adjState.getBlock() == this) { // Block attempting to stick another half slime block
            Direction facing = state.get(FACING);
            Direction adjFacing = adjState.get(FACING);
            return facing == adjFacing || facing.getOpposite() != adjFacing;
        }
        StickyGroup group = ((ConfigurablePistonStickiness)adjState.getBlock()).getStickyGroup();
        if (group != null) {
            return StickyGroup.canStick(StickyGroup.SLIME, group);
        }
        return false;
    }
}
