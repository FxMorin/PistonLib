package ca.fxco.pistonlib.blocks.halfBlocks;

import java.util.Map;

import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class HalfObsidianBlock extends Block implements ConfigurablePistonBehavior, ConfigurablePistonStickiness {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public HalfObsidianBlock(Properties properties) {
        super(properties);
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
    public boolean usesConfigurablePistonBehavior() {
        return true;
    }

    @Override
    public boolean canPistonPush(BlockState state, Direction dir) {
        return dir.getOpposite() != state.getValue(FACING);
    }

    @Override
    public boolean canPistonPull(BlockState state, Direction dir) {
        return dir != state.getValue(FACING);
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public  Map<Direction, StickyType> stickySides(BlockState state) {
        return Map.of(state.getValue(FACING), StickyType.NO_STICK);
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction dir) {
        return dir == state.getValue(FACING) ? StickyType.NO_STICK : StickyType.DEFAULT;
    }
}
