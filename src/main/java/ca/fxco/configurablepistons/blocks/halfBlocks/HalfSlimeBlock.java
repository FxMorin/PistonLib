package ca.fxco.configurablepistons.blocks.halfBlocks;

import ca.fxco.configurablepistons.helpers.HalfBlockUtils;
import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Map;

import static ca.fxco.configurablepistons.helpers.HalfBlockUtils.SIDES_LIST;

public class HalfSlimeBlock extends Block implements ConfigurablePistonStickiness {

    public static final DirectionProperty FACING = Properties.FACING;

    public HalfSlimeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.bypassesLandingEffects() || HalfBlockUtils.isOnFacingSide(entity.getPos(), pos, state)) {
            super.onLandedUpon(world, state, pos, entity, fallDistance);
        } else {
            entity.handleFallDamage(fallDistance, 0.0F, DamageSource.FALL);
        }
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        if (entity.bypassesLandingEffects()) {
            super.onEntityLand(world, entity);
        } else {
            if (HalfBlockUtils.isOnFacingSide(world, entity.getPos(), entity.getLandingPos())) {
                super.onEntityLand(world, entity);
            } else {
                this.bounce(entity);
            }
        }
    }

    protected void bounce(Entity entity) {
        Vec3d vec3d = entity.getVelocity();
        if (vec3d.y < 0.0) {
            double d = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setVelocity(vec3d.x, -vec3d.y * d, vec3d.z);
        }

    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        double d = Math.abs(entity.getVelocity().y);
        if (d < 0.1 && !(entity.bypassesSteppingEffects() || HalfBlockUtils.isOnFacingSide(entity.getPos(), pos, state))) {
            double e = 0.4 + d * 0.2;
            entity.setVelocity(entity.getVelocity().multiply(e, 1.0, e));
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
