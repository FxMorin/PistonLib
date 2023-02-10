package ca.fxco.pistonlib.blocks.autoCraftingBlock;

import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.impl.BlockEntityMerging;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonMerging;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AutoCraftingBlock extends BaseEntityBlock implements ConfigurablePistonBehavior, ConfigurablePistonMerging {

    public AutoCraftingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player,
                                 InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(blockState.getMenuProvider(level, blockPos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState.is(blockState2.getBlock())) {
            if (!blockState2.is(ModBlocks.MERGE_BLOCK)) {
                BlockEntity blockEntity = level.getBlockEntity(blockPos);
                if (blockEntity instanceof AutoCraftingBlockEntity autoCraftingBlockEntity) {
                    autoCraftingBlockEntity.resultItemStack = ItemStack.EMPTY; // Prevent dupe xD
                    Containers.dropContents(level, blockPos, autoCraftingBlockEntity);
                    level.updateNeighbourForOutputSignal(blockPos, this);
                }
            }
            super.onRemove(blockState, level, blockPos, blockState2, bl);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(blockPos));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AutoCraftingBlockEntity(blockPos, blockState);
    }

    @Override
    public void onPushEntityInto(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AutoCraftingBlockEntity autoCraftingBlockEntity) {
                HopperBlockEntity.addItem(autoCraftingBlockEntity, itemEntity);
            }
        }
    }

    @Override
    public boolean usesConfigurablePistonMerging() {
        return true;
    }

    @Override
    public boolean canMerge(BlockState state, BlockGetter blockGetter, BlockPos blockPos,
                            BlockState mergingIntoState, Direction direction) {
        return state.getBlock().asItem() instanceof BlockItem; // Only accept block items
    }

    @Override
    public BlockState doMerge(BlockState state, BlockGetter blockGetter, BlockPos blockPos,
                              BlockState mergingIntoState, Direction direction) {
        return mergingIntoState; // Your still an auto crafting block, I know... what a disappointment
    }

    @Override
    public boolean canUnMerge(BlockState state, BlockGetter blockGetter, BlockPos blockPos,
                              BlockState neighborState, Direction direction) {
        return true; // Handled by the block entity
    }

    @Override
    public @Nullable Pair<BlockState, BlockState> doUnMerge(BlockState state, BlockGetter blockGetter,
                                                            BlockPos blockPos, Direction direction) {
        return null; // Handled by the block entity
    }

    public MergeRule getBlockEntityMergeRules() {
        return MergeRule.ALWAYS;
    }


    @Override
    public boolean usesConfigurablePistonBehavior() {
        return true;
    }

    @Override
    public boolean canPistonPush(Level level, BlockPos pos, BlockState state, Direction direction) {
        return false;
    }

    @Override
    public boolean canPistonPull(Level level, BlockPos pos, BlockState state, Direction direction) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof BlockEntityMerging bem && bem.canUnMerge(state, null, direction);
    }
}
