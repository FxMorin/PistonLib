package ca.fxco.pistonlib.blocks.pistons.basePiston;

import java.util.Collections;
import java.util.List;

import ca.fxco.pistonlib.PistonLibConfig;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.properties.PistonType;
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

    @Getter
    private final PistonFamily family;

    public BasicMovingBlock(PistonFamily family) {
        this(family, createDefaultSettings());
    }

    public BasicMovingBlock(PistonFamily family, Properties properties) {
        super(properties.isValidSpawn((a,b,c,d) -> !PistonLibConfig.mobsSpawnOnMovingPistonsFix));

        this.family = family;
        this.family.setMoving(this);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, this.family.getMovingBlockEntityType(), (l, p, s, mbe) -> mbe.tick());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!newState.is(this)) {
            BasicMovingBlockEntity mbe = this.getMovingBlockEntity(level, pos);

            if (mbe != null) {
                mbe.finalTick();
            }
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        PistonType type = state.getValue(TYPE);
        BlockPos behindPos = pos.relative(state.getValue(FACING).getOpposite());
        BlockState behindState = level.getBlockState(behindPos);

        if (behindState.is(this.family.getBase(type)) && behindState.getValue(BasicPistonBaseBlock.EXTENDED)) {
            level.removeBlock(behindPos, false);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide() && this.getMovingBlockEntity(level, pos) == null) {
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

    public static Properties createDefaultSettings() {
        return FabricBlockSettings.of(Material.PISTON)
                .strength(-1.0f)
                .dynamicBounds()
                .dropsNothing()
                .nonOpaque()
                .solidBlock(BasicMovingBlock::never)
                .suffocates(BasicMovingBlock::never)
                .blockVision(BasicMovingBlock::never);
    }

    private static boolean never(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }
}
