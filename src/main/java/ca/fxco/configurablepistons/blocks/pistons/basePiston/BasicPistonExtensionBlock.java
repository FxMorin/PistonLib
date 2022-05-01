package ca.fxco.configurablepistons.blocks.pistons.basePiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class BasicPistonExtensionBlock extends PistonExtensionBlock {

    private static boolean never(BlockState a, BlockView b, BlockPos c) {
        return false;
    }

    public BasicPistonExtensionBlock() {
        super(FabricBlockSettings.of(Material.PISTON)
                .strength(-1.0f)
                .dynamicBounds()
                .dropsNothing()
                .nonOpaque()
                .solidBlock(BasicPistonExtensionBlock::never)
                .suffocates(BasicPistonExtensionBlock::never)
                .blockVision(BasicPistonExtensionBlock::never)
        );
    }

    public BlockEntity createPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock,
                                                      Direction facing, boolean extending, boolean source) {
        return new BasicPistonBlockEntity(pos, state, pushedBlock, facing, extending, source);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World w, BlockState state, BlockEntityType<T> t) {
        return checkType(t, ModBlockEntities.BASIC_PISTON_BLOCK_ENTITY, BasicPistonBlockEntity::tick);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BasicPistonBlockEntity bpbe) bpbe.finish();
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        BlockPos blockPos = pos.offset(state.get(FACING).getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() instanceof BasicPistonBlock && blockState.get(BasicPistonBlock.EXTENDED))
            world.removeBlock(blockPos, false);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && world.getBlockEntity(pos) == null) {
            world.removeBlock(pos, false);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        PistonBlockEntity blockEntity = this.getPistonBlockEntity(
                builder.getWorld(),
                new BlockPos(builder.get(LootContextParameters.ORIGIN))
        );
        return blockEntity == null ? Collections.emptyList() : blockEntity.getPushedBlock().getDroppedStacks(builder);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BasicPistonBlockEntity pistonBlockEntity = this.getPistonBlockEntity(world, pos);
        return pistonBlockEntity != null ? pistonBlockEntity.getCollisionShape(world, pos) : VoxelShapes.empty();
    }

    @Nullable
    private BasicPistonBlockEntity getPistonBlockEntity(BlockView world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof BasicPistonBlockEntity bpbe ? bpbe : null;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
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
        builder.add(FACING, TYPE);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}
