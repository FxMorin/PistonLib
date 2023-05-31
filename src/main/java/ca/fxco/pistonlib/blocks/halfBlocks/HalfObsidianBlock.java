package ca.fxco.pistonlib.blocks.halfBlocks;

import java.util.Map;

import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.SlabType;

public class HalfObsidianBlock extends Block {

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
    public boolean pl$usesConfigurablePistonBehavior() {
        return true;
    }

    @Override
    public boolean pl$canPistonPush(Level level,  BlockPos pos, BlockState state, Direction dir) {
        return dir.getOpposite() != state.getValue(FACING);
    }

    @Override
    public boolean pl$canPistonPull(Level level, BlockPos pos, BlockState state, Direction dir) {
        return dir != state.getValue(FACING);
    }

    @Override
    public boolean pl$usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public  Map<Direction, StickyType> pl$stickySides(BlockState state) {
        return Map.of(state.getValue(FACING), StickyType.NO_STICK);
    }

    @Override
    public StickyType pl$sideStickiness(BlockState state, Direction dir) {
        return dir == state.getValue(FACING) ? StickyType.NO_STICK : StickyType.DEFAULT;
    }

    @Override
    public boolean pl$usesConfigurablePistonMerging() {
        return true;
    }

    @Override
    public boolean pl$canUnMerge(BlockState state, BlockGetter level, BlockPos pos,
                              BlockState neighborState, Direction direction) {
        return true;
    }

    @Override
    public Pair<BlockState, BlockState> pl$doUnMerge(BlockState state, BlockGetter level,
                                                  BlockPos pos, Direction direction) {
        return new Pair<>(
                Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM),
                ModBlocks.OBSIDIAN_SLAB_BLOCK.defaultBlockState().setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP)
        );
    }
}
