package ca.fxco.configurablepistons.pistonLogic.families;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonArmBlock;
import net.minecraft.block.Block;
import org.jetbrains.annotations.Nullable;

public class PistonFamily {
    @Nullable
    protected BasicPistonHeadBlock headBlock;
    @Nullable
    protected BasicPistonBlock pistonBlock = null;
    @Nullable
    protected BasicPistonBlock stickyPistonBlock = null;
    @Nullable
    protected BasicPistonExtensionBlock extensionBlock = null;
    @Nullable
    protected LongPistonArmBlock armBlock = null;

    protected final boolean generateAutomatically;
    protected final boolean customTextures;
    private final String id;

    PistonFamily(String id) {
        this(id, true);
    }

    PistonFamily(String id, boolean hasCustomTextures) {
        this(id, hasCustomTextures, true);
    }

    PistonFamily(String id, boolean hasCustomTextures, boolean shouldGenerateAutomatically) {
        this.id = id;
        this.customTextures = hasCustomTextures;
        this.generateAutomatically = shouldGenerateAutomatically;
        PistonFamilies.registerBlockId(id,this); // Adds the blockId to the quick lookup table
    }

    public @Nullable BasicPistonHeadBlock getHeadBlock() {
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

    public @Nullable LongPistonArmBlock getArmBlock() {
        return this.armBlock;
    }

    public boolean shouldGenerateAutomatically() {
        return this.generateAutomatically;
    }

    public boolean hasCustomTextures() {
        return this.customTextures;
    }

    // Use this method to implement custom block types in families
    public boolean hasCustomBlockLogic(Block block) {
        return false;
    }

    public String getId() {
        return this.id;
    }

    public boolean mustSetupHead() {
        return this.headBlock == null;
    }

    public Block getBaseBlock() {
        return this.pistonBlock == null ? this.stickyPistonBlock : this.pistonBlock;
    }

    public void head(BasicPistonHeadBlock headBlock) {
        this.headBlock = headBlock;
        PistonFamilies.registerPistonHead(headBlock,this); // Adds the piston head to the quick lookup table
    }

    public void piston(BasicPistonBlock block) {
        this.pistonBlock = block;
    }

    public void sticky(BasicPistonBlock block) {
        this.stickyPistonBlock = block;
    }

    public void extension(BasicPistonExtensionBlock block) {
        this.extensionBlock = block;
    }

    // For modded pistons that extend further than one block
    public void arm(LongPistonArmBlock block) {
        this.armBlock = block;
    }
}
