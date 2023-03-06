package ca.fxco.pistonlib.mixin.merging;

import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
import ca.fxco.pistonlib.helpers.Utils;
import ca.fxco.pistonlib.impl.BlockEntityMerging;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntity_mergeMixin implements BlockEntityMerging {

    @Shadow public abstract boolean hasGlowingText();

    @Shadow public abstract DyeColor getColor();

    @Shadow @Final public static int LINES;

    @Shadow public abstract Component getMessage(int i, boolean bl);

    @Override
    public boolean shouldStoreSelf(MergeBlockEntity mergeBlockEntity) {
        return true;
    }

    @Override
    public void onAdvancedFinalMerge(BlockEntity blockEntity) {
        if (blockEntity instanceof SignBlockEntity signBlockEntity) {
            if (this.hasGlowingText() && !signBlockEntity.hasGlowingText()) {
                signBlockEntity.setHasGlowingText(true);
            }
            if (this.getColor() != signBlockEntity.getColor() && signBlockEntity.getColor() != DyeColor.BLACK) {
                signBlockEntity.setColor(Utils.properDyeMixing(this.getColor(), signBlockEntity.getColor()));
            }
            for (int i = 0; i < LINES; i++) {
                Component comp = signBlockEntity.getMessage(i, false);
                if (comp == CommonComponents.EMPTY || comp.getString().isEmpty()) {
                    signBlockEntity.setMessage(i, this.getMessage(i, false));
                }
            }
        }
    }
}
