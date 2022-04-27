package ca.fxco.configurablepistons.families;

import ca.fxco.configurablepistons.base.BasicPistonBlock;
import ca.fxco.configurablepistons.base.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.base.BasicPistonHeadBlock;
import net.minecraft.block.Block;
import org.jetbrains.annotations.Nullable;

public class PistonFamily {
    private final BasicPistonHeadBlock headBlock;
    @Nullable
    private BasicPistonBlock pistonBlock = null;
    @Nullable
    private BasicPistonBlock stickyPistonBlock = null;
    @Nullable
    private BasicPistonExtensionBlock extensionBlock = null;
    @Nullable
    private Block armBlock = null;

    PistonFamily(BasicPistonHeadBlock headBlock) {
        this.headBlock = headBlock;
    }

    public BasicPistonHeadBlock getHeadBlock() {
        return this.headBlock;
    }

    public @Nullable BasicPistonBlock getPistonBlock() {
        return this.pistonBlock;
    }

    public @Nullable BasicPistonBlock getStickyPistonBlock() {
        return this.stickyPistonBlock;
    }

    public @Nullable BasicPistonExtensionBlock getExtensionBlock() {
        return this.extensionBlock;
    }

    public @Nullable Block getArmBlock() {
        return this.armBlock;
    }

    public static class Builder {
        private final PistonFamily family;

        public Builder(BasicPistonHeadBlock headBlock) {
            this.family = new PistonFamily(headBlock);
        }

        public PistonFamily build() {
            return this.family;
        }

        public PistonFamily.Builder piston(BasicPistonBlock block) {
            this.family.pistonBlock = block;
            return this;
        }

        public PistonFamily.Builder sticky(BasicPistonBlock block) {
            this.family.stickyPistonBlock = block;
            return this;
        }

        public PistonFamily.Builder extension(BasicPistonExtensionBlock block) {
            this.family.extensionBlock = block;
            return this;
        }

        // For modded pistons that extend further than one block
        public PistonFamily.Builder arm(BasicPistonExtensionBlock block) {
            this.family.armBlock = block;
            return this;
        }
    }
}
