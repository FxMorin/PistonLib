package ca.fxco.configurablepistons.pistonLogic.families;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonArmBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
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
    private BasicPistonArmBlock armBlock = null;

    PistonFamily(BasicPistonHeadBlock headBlock) {
        this(headBlock, true);
    }

    PistonFamily(BasicPistonHeadBlock headBlock, boolean shouldGenerateAutomatically) {
        this.headBlock = headBlock;
        this.shouldGenerateAutomatically = shouldGenerateAutomatically;
    }

    private final boolean shouldGenerateAutomatically;

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

    public @Nullable BasicPistonArmBlock getArmBlock() {
        return this.armBlock;
    }

    public boolean shouldGenerateAutomatically() {
        return this.shouldGenerateAutomatically;
    }

    public static class Builder {
        private final PistonFamily family;

        public Builder(BasicPistonHeadBlock headBlock) {
            this.family = new PistonFamily(headBlock);
        }

        public PistonFamily getFamily() {
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
        public PistonFamily.Builder arm(BasicPistonArmBlock block) {
            this.family.armBlock = block;
            return this;
        }
    }
}
