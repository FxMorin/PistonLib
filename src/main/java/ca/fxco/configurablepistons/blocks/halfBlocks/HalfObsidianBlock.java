package ca.fxco.configurablepistons.blocks.halfBlocks;

import ca.fxco.configurablepistons.helpers.Utils;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class HalfObsidianBlock extends Block implements ConfigurablePistonBehavior, ConfigurablePistonStickiness {

    public static final DirectionProperty FACING = Properties.FACING;

    public HalfObsidianBlock(Settings settings) {
        super(settings);
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
    public boolean usesConfigurablePistonBehavior() {
        return true;
    }

    @Override
    public boolean canPistonPush(BlockState state, Direction direction) {
        return direction.getOpposite() != state.get(FACING);
    }

    @Override
    public boolean canPistonPull(BlockState state, Direction direction) {
        return direction != state.get(FACING);
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public  Map<Direction, StickyType> stickySides(BlockState state) {
        return Map.of(Direction.NORTH, StickyType.NO_STICK);
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction direction) {
        return state.get(FACING) == direction ? StickyType.NO_STICK : StickyType.DEFAULT;
    }
}
