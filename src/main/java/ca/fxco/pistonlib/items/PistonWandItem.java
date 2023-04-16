package ca.fxco.pistonlib.items;

import ca.fxco.api.pistonlib.level.ServerLevelInteraction;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class PistonWandItem extends Item {
    public PistonWandItem(Item.Properties properties) {
        super(properties);
    }

    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        return player.canUseGameMasterBlocks(); //todo: change later
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        if (player == null || !player.canUseGameMasterBlocks()) { //todo: change later
            return InteractionResult.FAIL;
        }
        Level level = useOnContext.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            ItemStack wandItem = getWandItem(useOnContext.getItemInHand());
            if (wandItem != ItemStack.EMPTY) {
                BlockPos blockPos = useOnContext.getClickedPos();
                Direction face = useOnContext.getClickedFace();
                ((ServerLevelInteraction) serverLevel).triggerPistonEvent(((BasicPistonBaseBlock) ((BlockItem) wandItem.getItem()).getBlock()), blockPos.relative(face), face.getOpposite(), true);
            } else {
                ((ServerPlayer) player).sendSystemMessage(Component.literal("Piston Wand does not currently have a piston!"), true); // todo: translations
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // TODO: Fix the wand deleting items when those items can't be inserted

    @Override
    public boolean overrideStackedOnOther(ItemStack itemStack, Slot slot, ClickAction clickAction, Player player) {
        if (clickAction != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack itemStack2 = slot.getItem();
            if (itemStack2.isEmpty()) {
                removeItem(itemStack).ifPresent((itemStack2x) -> {
                    this.playRemoveItemSound(player);
                    add(itemStack, slot.safeInsert(itemStack2x));
                });
            } else if (itemStack2.getItem().canFitInsideContainerItems()) {
                int j = add(itemStack, slot.safeTake(itemStack2.getCount(), 1, player));
                if (j > 0) {
                    this.playInsertSound(player);
                }
            }
            return true;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack itemStack, ItemStack itemStack2, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        if (clickAction == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (itemStack2.isEmpty()) {
                removeItem(itemStack).ifPresent((itemStackx) -> {
                    this.playRemoveItemSound(player);
                    slotAccess.set(itemStackx);
                });
            } else {
                int i = add(itemStack, itemStack2);
                if (i > 0) {
                    this.playInsertSound(player);
                    itemStack2.shrink(i);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private static int add(ItemStack itemStack, ItemStack addingItem) {
        if (!addingItem.isEmpty() && addingItem.getItem().canFitInsideContainerItems()) {
            if (!(addingItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BasicPistonBaseBlock basicPistonBaseBlock)) {
                return 0;
            }
            CompoundTag compoundTag = itemStack.getOrCreateTag();
            if (!compoundTag.contains("Item")) { // its empty!
                ItemStack itemStack4 = addingItem.copy();
                itemStack4.setCount(1);
                CompoundTag compoundTag3 = new CompoundTag();
                itemStack4.save(compoundTag3);
                compoundTag.put("Item", compoundTag3);
                return 1;
            } else {
                //CompoundTag compoundTag2 = compoundTag.getCompound("Item");
                //ItemStack stack = ItemStack.of(compoundTag2);
                // Allow customization of the length based on pistons in wand
                /*if (addingItem.is(stack.getItem()) && basicPistonBaseBlock.getFamily().getMaxLength() > stack.getCount()) {
                    // add items to internal item
                }*/
                return 0;
            }
        }
        return 0;
    }

    private static Optional<ItemStack> removeItem(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag == null) {
            return Optional.empty();
        }
        if (!compoundTag.contains("Item")) {
            return Optional.empty();
        }
        CompoundTag compoundTag2 = compoundTag.getCompound("Item");
        ItemStack stack = ItemStack.of(compoundTag2);
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        itemStack.removeTagKey("Item");
        return Optional.of(stack);
    }

    private static ItemStack getWandItem(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag == null) {
            return ItemStack.EMPTY;
        }
        if (!compoundTag.contains("Item")) {
            return ItemStack.EMPTY;
        }
        CompoundTag compoundTag2 = compoundTag.getCompound("Item");
        ItemStack stack = ItemStack.of(compoundTag2);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BasicPistonBaseBlock) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        NonNullList<ItemStack> nonNullList = NonNullList.create();
        nonNullList.add(getWandItem(itemStack));
        return Optional.of(new BundleTooltip(nonNullList, 64));
    }

    private void playRemoveItemSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

}
