package ca.fxco.pistonlib.blocks.autoCraftingBlock;

import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.helpers.NbtUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AutoCraftingBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

    // Players will not be able to modify this crafting inventory/container
    // The only world interactions will be unmerging & pulling items from below,
    // although you can only pull the output item if it's not a block item

    private static final int RESULT_SLOT = 9;
    private static final int[] EXTRACTION_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, RESULT_SLOT};
    private static final int[] NO_SLOTS = new int[0];

    protected final CraftingContainer craftSlots;
    protected ItemStack resultItemStack;
    protected CraftingRecipe lastRecipe;

    public AutoCraftingBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.AUTO_CRAFTING_BLOCK_ENTITY, pos, state);
    }

    public AutoCraftingBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.craftSlots = new CraftingContainer(null, 3, 3);
        this.resultItemStack = ItemStack.EMPTY;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.pistonlib.auto_crafting_block");
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new CraftingMenu(i, inventory);
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return direction == Direction.DOWN ? EXTRACTION_SLOTS : NO_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
        return false; // No item input, this will be handled by the block merging api
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
        return slot == RESULT_SLOT && direction == Direction.DOWN && !(itemStack.getItem() instanceof BlockItem);
    }

    @Override
    public int getContainerSize() {
        return 10;
    }

    @Override
    public boolean isEmpty() {
        return this.resultItemStack.isEmpty() && craftSlots.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot < RESULT_SLOT ? craftSlots.getItem(slot) : this.resultItemStack;
    }

    @Override
    public ItemStack removeItem(int slot, int amt) {
        if (slot == RESULT_SLOT) {
            if (this.resultItemStack.isEmpty()) {
                this.resultItemStack = craft();
            }
            return this.resultItemStack.split(amt);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == RESULT_SLOT) {
            ItemStack result = this.resultItemStack;
            this.resultItemStack = ItemStack.EMPTY;
            return result;
        }
        return this.craftSlots.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        if (slot == RESULT_SLOT) {
            this.resultItemStack = itemStack;
        } else {
            craftSlots.setItem(slot, itemStack);
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true; // TODO: Player cannot currently use this
        /*if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) > 64.0);
        }*/
    }

    @Override
    public void clearContent() {
        this.craftSlots.clearContent();
        this.resultItemStack = ItemStack.EMPTY;
    }

    private CraftingRecipe getBestRecipe() {
        if (this.level == null || this.craftSlots.isEmpty()) {
            return null;
        }
        if (lastRecipe != null && lastRecipe.matches(this.craftSlots, this.level)) {
            return lastRecipe;
        }
        List<CraftingRecipe> recipeList = this.level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        for (CraftingRecipe recipe : recipeList) {
            if (recipe.matches(this.craftSlots, this.level)) {
                lastRecipe = recipe;
                return recipe;
            }
        }
        return null;
    }

    private ItemStack craft() {
        if (this.level == null) {
            return ItemStack.EMPTY;
        }
        CraftingRecipe recipe = getBestRecipe();
        if (recipe == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = recipe.assemble(this.craftSlots);
        NonNullList<ItemStack> remaining = level.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, this.craftSlots, level);
        for (int i = 0; i < 9; i++) {
            ItemStack current = this.craftSlots.getItem(i);
            ItemStack remainingStack = remaining.get(i);
            if (!current.isEmpty()) {
                current.shrink(1);
            }
            if (!remainingStack.isEmpty()) {
                if (current.isEmpty()) {
                    this.craftSlots.setItem(i, remainingStack);
                    remaining.set(i, ItemStack.EMPTY);
                } else if (ItemStack.isSame(current, remainingStack) && ItemStack.tagMatches(current, remainingStack)) {
                    current.grow(remainingStack.getCount());
                    remaining.set(i, ItemStack.EMPTY);
                }
            }
        }
        Containers.dropContents(level, this.worldPosition, remaining);
        this.setChanged();
        return result;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        NbtUtils.loadAllItems(compoundTag, this.craftSlots);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        NbtUtils.saveAllItems(compoundTag, this.craftSlots);
    }
}
