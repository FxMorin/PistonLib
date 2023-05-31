package ca.fxco.pistonlib.blocks.autoCraftingBlock;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
import ca.fxco.pistonlib.helpers.NbtUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class AutoCraftingBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, MenuProvider {

    // Players will not be able to modify this crafting inventory/container
    // The only world interactions will be unmerging & pulling items from below,
    // although you can only pull the output item if it's not a block item or if it has a rarity higher than common

    private static final int RESULT_SLOT = 9;
    private static final int[] EXTRACTION_SLOTS = {RESULT_SLOT, 0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int[] NO_SLOTS = new int[0];

    protected final CraftingContainer items;
    protected ItemStack resultItemStack;
    protected CraftingRecipe lastRecipe;
    protected CraftingRecipe lastSuccessfulRecipe;
    protected boolean hasPaid = false;

    public AutoCraftingBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.AUTO_CRAFTING_BLOCK_ENTITY, pos, state);
    }

    public AutoCraftingBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.items = new CraftingContainer(new AbstractContainerMenu(MenuType.CRAFTING, -1) {
            @Override public ItemStack quickMoveStack(Player player, int i) {return ItemStack.EMPTY;}
            @Override public void slotsChanged(Container container) {}
            @Override public boolean stillValid(Player player) {return true;}
        }, 3, 3);
        this.resultItemStack = ItemStack.EMPTY;
    }

    @Override
    public boolean pl$canMerge(BlockState state, BlockState mergingIntoState, Direction dir) {
        return areAnySlotsLeft();
    }

    @Override
    public boolean pl$canMultiMerge(BlockState state, BlockState mergingIntoState, Direction dir,
                                 Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return getRemainingSlotCount() - currentlyMerging.size() > 0; // are any spaces left?
    }

    @Override
    public boolean pl$canUnMerge(BlockState state, BlockState neighborState, Direction dir) {
        if (PistonLibConfig.extractBlocksFromAutoCrafting) {
            for (int i = 0; i < this.items.getContainerSize(); i++) {
                ItemStack stack = this.items.getItem(i);
                if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                    return true;
                }
            }
        }
        return !this.resultItemStack.isEmpty() && this.resultItemStack.getItem() instanceof BlockItem;
    }

    @Override
    public @Nullable Pair<BlockState, BlockState> pl$doUnMerge(BlockState state, Direction dir) {
        if (PistonLibConfig.extractBlocksFromAutoCrafting) {
            for (int slot : EXTRACTION_SLOTS) {
                ItemStack stack = getItem(slot);
                if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                    Pair<BlockState, BlockState> unmergedStates = new Pair<>(
                            ((BlockItem)removeItem(slot, 1).getItem()).getBlock().defaultBlockState(),
                            state
                    );
                    this.setChanged();
                    return unmergedStates;
                }
            }
            return null;
        }
        Pair<BlockState, BlockState> unmergedStates = new Pair<>(
                ((BlockItem)removeItem(RESULT_SLOT, 1).getItem()).getBlock().defaultBlockState(),
                state
        );
        this.setChanged();
        return unmergedStates;
    }

    @Override
    public boolean pl$shouldUnMergeBlockEntity(BlockState state, Direction dir) {
        return false;
    }

    @Override
    public void pl$afterInitialFinalMerge(BlockState finalState, Map<Direction, MergeBlockEntity.MergeData> mergedData) {
        for (MergeBlockEntity.MergeData data : mergedData.values()) {
            setItem(getNextSlot(), data.getState().getBlock().asItem().getDefaultInstance());
        }
    }

    private int getRemainingSlotCount() {
        int c = 0;
        for(int x = 0; x < this.items.getContainerSize(); ++x) {
            if (this.getItem(x).isEmpty()) {
                c++;
            }
        }
        return c;
    }

    private int getNextSlot() {
        for(int x = 0; x < this.items.getContainerSize(); ++x) {
            if (this.getItem(x).isEmpty()) {
                return x;
            }
        }
        return -1;
    }

    private boolean areAnySlotsLeft() {
        for(int x = 0; x < this.items.getContainerSize(); ++x) {
            if (this.getItem(x).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    //
    // Container Stuff, eww
    //

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.pistonlib.auto_crafting_block");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new AutoCraftingMenu(containerId, inventory, this);
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return direction == Direction.DOWN ? EXTRACTION_SLOTS : NO_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
        // Allow items to be pushed into the block if they are not block items
        return slot != RESULT_SLOT && this.getItem(slot).isEmpty() && direction == null &&
                !(itemStack.getItem() instanceof BlockItem);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return direction == Direction.DOWN && // Rare or higher blocks can be used as filters ;)
                (!(stack.getItem() instanceof BlockItem) || stack.getRarity().ordinal() >= Rarity.RARE.ordinal());
    }

    @Override
    public int getContainerSize() {
        return 10;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return (this.resultItemStack.isEmpty() || !this.hasPaid) && items.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot < RESULT_SLOT ? items.getItem(slot) : this.resultItemStack;
    }

    @Override
    public ItemStack removeItem(int slot, int amt) {
        if (slot == RESULT_SLOT) {
            if (!this.hasPaid) {
                this.hasPaid = true;
                this.items.clearContent();
            }
            this.lastSuccessfulRecipe = this.lastRecipe;
            ItemStack stack = this.resultItemStack.split(amt);
            if (this.resultItemStack.isEmpty()) {
                this.resultItemStack = craft();
            }
            return stack;
        }
        ItemStack stack = this.items.removeItem(slot, amt);
        if (this.resultItemStack.isEmpty() || !this.hasPaid) {
            this.resultItemStack = craft();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == RESULT_SLOT) {
            ItemStack result = this.resultItemStack;
            this.resultItemStack = ItemStack.EMPTY;
            this.lastSuccessfulRecipe = this.lastRecipe;
            if (!this.hasPaid) {
                this.hasPaid = true;
                this.items.clearContent();
            }
            return result;
        }
        ItemStack stack = this.items.removeItemNoUpdate(slot);
        if (this.resultItemStack.isEmpty() || !this.hasPaid) {
            this.resultItemStack = craft();
        }
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        if (slot == RESULT_SLOT) {
            this.resultItemStack = itemStack;
            if (this.resultItemStack.isEmpty()) {
                this.resultItemStack = craft();
            }
        } else {
            items.setItem(slot, itemStack);
            if (this.resultItemStack.isEmpty() || !this.hasPaid) {
                this.resultItemStack = craft();
            }
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition);
        if (blockEntity != this && !(blockEntity instanceof MergeBlockEntity mbe && mbe.getInitialBlockEntity() == this)) {
            return false;
        } else {
            return !(player.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) > 64.0);
        }
    }

    @Override
    public void clearContent() {
        this.items.clearContent();
        this.resultItemStack = ItemStack.EMPTY;
        this.hasPaid = false;
    }

    private CraftingRecipe getBestRecipe() {
        if (this.level == null || this.items.isEmpty()) {
            return null;
        }
        if (lastSuccessfulRecipe != null && lastSuccessfulRecipe.matches(this.items, this.level)) {
            return lastSuccessfulRecipe;
        }
        if (lastSuccessfulRecipe != lastRecipe && lastRecipe != null && lastRecipe.matches(this.items, this.level)) {
            return lastRecipe;
        }
        List<CraftingRecipe> recipeList = this.level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        for (CraftingRecipe recipe : recipeList) {
            if (recipe.matches(this.items, this.level)) {
                lastRecipe = recipe;
                return recipe;
            }
        }
        return null;
    }

    private ItemStack craft() {
        this.hasPaid = false;
        if (this.level == null) {
            return ItemStack.EMPTY;
        }
        CraftingRecipe recipe = getBestRecipe();
        if (recipe == null) {
            return ItemStack.EMPTY;
        }
        return recipe.assemble(this.items);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        if (compoundTag.contains("items")) {
            NbtUtils.loadAllItems(compoundTag.getCompound("items"), this.items);
        }
        if (compoundTag.contains("result")) {
            this.resultItemStack = ItemStack.of(compoundTag.getCompound("result"));
        } else {
            this.resultItemStack = ItemStack.EMPTY;
        }
        this.hasPaid = compoundTag.contains("paid");
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        if (!this.items.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            NbtUtils.saveAllItems(itemTag, this.items);
            compoundTag.put("items", itemTag);
        }
        if (!this.resultItemStack.isEmpty()) {
            compoundTag.put("result", this.resultItemStack.save(new CompoundTag()));
        }
        if (this.hasPaid) {
            compoundTag.putBoolean("paid", true);
        }
    }
}
