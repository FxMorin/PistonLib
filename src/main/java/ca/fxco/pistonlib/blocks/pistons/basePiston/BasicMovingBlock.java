package ca.fxco.pistonlib.blocks.pistons.basePiston;

import java.util.Collections;
import java.util.List;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.base.ModBlockEntities;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BasicMovingBlock extends MovingPistonBlock {

    private static boolean never(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    public BasicMovingBlock() {
        this(createDefaultSettings());
    }

    public BasicMovingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public BlockEntity createMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState,
                                               @Nullable BlockEntity movedBlockEntity, Direction facing,
                                               boolean extending, boolean isSourcePiston) {
        return new BasicMovingBlockEntity(pos, state, movedState, facing, extending, isSourcePiston);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(type, ModBlockEntities.BASIC_MOVING_BLOCK_ENTITY);
    }

    @Nullable
    protected static <T extends BlockEntity, B extends BasicMovingBlockEntity> BlockEntityTicker<T> createTicker(BlockEntityType<T> targetType, BlockEntityType<B> movingBlockEntityType) {
        return createTickerHelper(targetType, movingBlockEntityType, (l, p, s, mbe) -> mbe.tick());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!newState.is(this)) {
            BasicMovingBlockEntity mbe = getMovingBlockEntity(level, pos);

            if (mbe != null) {
                mbe.finalTick();
            }
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockPos behindPos = pos.relative(state.getValue(FACING).getOpposite());
        BlockState behindState = level.getBlockState(behindPos);

        if (behindState.getBlock() instanceof BasicPistonBaseBlock && behindState.getValue(BasicPistonBaseBlock.EXTENDED)) {
            level.removeBlock(behindPos, false);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide() && getMovingBlockEntity(level, pos) == null) {
            level.removeBlock(pos, false);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Level level = builder.getLevel();
        Vec3 origin = builder.getParameter(LootContextParams.ORIGIN);
        BasicMovingBlockEntity mbe = this.getMovingBlockEntity(level, new BlockPos(origin));

        return mbe == null ? Collections.emptyList() : mbe.getMovedState().getDrops(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        BasicMovingBlockEntity mbe = this.getMovingBlockEntity(level, pos);
        return mbe == null ? Shapes.empty() : mbe.getCollisionShape(level, pos);
    }

    @Nullable
    private BasicMovingBlockEntity getMovingBlockEntity(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof BasicMovingBlockEntity mbe ? mbe : null;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
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
        builder.add(FACING, TYPE);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    public static BlockBehaviour.Properties createDefaultSettings() {
        return FabricBlockSettings.of(Material.PISTON)
                .strength(-1.0f)
                .dynamicBounds()
                .dropsNothing()
                .nonOpaque()
                .solidBlock(BasicMovingBlock::never)
                .suffocates(BasicMovingBlock::never)
                .blockVision(BasicMovingBlock::never);
    }
}
