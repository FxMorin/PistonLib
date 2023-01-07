package ca.fxco.configurablepistons.pistonLogic.families;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonArmBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.PistonType;

public class PistonFamily {

    @Nullable
    protected BasicPistonHeadBlock headBlock;
    @Nullable
    protected BasicPistonBaseBlock normalBaseBlock = null;
    @Nullable
    protected BasicPistonBaseBlock stickyBaseBlock = null;
    @Nullable
    protected BasicMovingBlock movingBlock = null;
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
        PistonFamilies.registerBlockId(id, this); // Adds the blockId to the quick lookup table
    }

    public @Nullable BasicPistonHeadBlock getHeadBlock() {
        return this.headBlock;
    }

    public @Nullable BasicPistonBaseBlock getBaseBlock(PistonType type) {
        return getBaseBlock(Objects.requireNonNull(type) == PistonType.STICKY);
    }

    public @Nullable BasicPistonBaseBlock getBaseBlock(boolean sticky) {
        return sticky ? this.stickyBaseBlock : this.normalBaseBlock;
    }

    public Block getBaseBlock() {
        return this.normalBaseBlock == null ? this.stickyBaseBlock : this.normalBaseBlock;
    }

    public @Nullable BasicMovingBlock getMovingBlock() {
        return this.movingBlock;
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

    public void head(BasicPistonHeadBlock block) {
        this.headBlock = block;
        PistonFamilies.registerPistonHead(block,this); // Adds the piston head to the quick lookup table
    }

    public void base(PistonType type, BasicPistonBaseBlock block) {
        Objects.requireNonNull(type);
        if (type == PistonType.STICKY) {
            this.stickyBaseBlock = block;
        } else {
            this.normalBaseBlock = block;
        }
    }

    public void moving(BasicMovingBlock block) {
        this.movingBlock = block;
    }

    // For modded pistons that extend further than one block
    public void arm(LongPistonArmBlock block) {
        this.armBlock = block;
    }
}
