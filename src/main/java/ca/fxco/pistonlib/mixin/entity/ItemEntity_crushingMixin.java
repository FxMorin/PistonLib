package ca.fxco.pistonlib.mixin.entity;

import ca.fxco.api.pistonlib.containers.CrushingContainer;
import ca.fxco.api.pistonlib.recipes.PistonCrushingRecipe;
import ca.fxco.pistonlib.base.ModRecipeTypes;
import ca.fxco.pistonlib.impl.EntityPistonMechanics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

// Item crushing concept taken from: https://github.com/wisp-forest/things/blob/1.19/src/main/java/com/glisco/things/mixin/EntityMixin.java#L75
@Mixin(ItemEntity.class)
public abstract class ItemEntity_crushingMixin extends Entity implements EntityPistonMechanics {

    @Shadow public abstract ItemStack getItem();

    public ItemEntity_crushingMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean canPushIntoBlocks() {
        return true;
    }

    @Override
    public void onPistonCrushing(@Nullable Block crushedAgainst) {
        if (this.isRemoved()) {
            return;
        }

        List<ItemEntity> itemEntities = this.level.getEntities(
                EntityTypeTest.forClass(ItemEntity.class), new AABB(this.blockPosition()), ItemEntity::isAlive);

        if (itemEntities.isEmpty()) {
            return;
        }

        NonNullList<ItemStack> itemsToMerge = NonNullList.create();

        for (ItemEntity itemEntity : itemEntities) {
            itemsToMerge.add(itemEntity.getItem());
        }

        CrushingContainer crushingContainer = new CrushingContainer(itemsToMerge, crushedAgainst);

        Optional<PistonCrushingRecipe> optionalRecipe = this.level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.PISTON_CRUSHING, crushingContainer, this.level);
        if (optionalRecipe.isEmpty()) {
            return;
        }
        PistonCrushingRecipe crushingRecipe = optionalRecipe.get();
        NonNullList<ItemStack> results = crushingRecipe.getRemainingItems(crushingContainer);

        for(int i = 0; i < results.size(); ++i) {
            ItemStack toMerge = itemsToMerge.get(i);
            ItemStack result = results.get(i);
            if (!toMerge.isEmpty()) {
                toMerge.shrink(1);
            }

            if (!result.isEmpty()) {
                if (toMerge.isEmpty()) {
                    toMerge.setCount(result.getCount());
                } else if (ItemStack.isSame(toMerge, result) && ItemStack.tagMatches(toMerge, result)) {
                    toMerge.setCount(result.getCount() + toMerge.getCount());
                }
            }
        }
        this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), crushingRecipe.getResultItem()));
    }
}
