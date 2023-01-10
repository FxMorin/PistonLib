package ca.fxco.pistonlib.pistonLogic.families;

import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.pistonlib.blocks.pistons.longPiston.LongPistonArmBlock;

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
        return switch (type) {
            case DEFAULT -> normalBaseBlock;
            case STICKY -> stickyBaseBlock;
            default -> throw new IllegalArgumentException("unknown base type " + type);
        };
    }

    public Block getBaseBlock() {
        for (PistonType type : PistonType.values()) {
            Block baseBlock = getBaseBlock(type);

            if (baseBlock != null) {
                return baseBlock;
            }
        }

        return null;
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
        PistonFamilies.registerPistonHead(block, this); // Adds the piston head to the quick lookup table
    }

    public void base(BasicPistonBaseBlock block) {
        if (getBaseBlock(block.type) != null)
            throw new IllegalStateException("base of type " + block.type + " has already been registered!");
        switch (block.type) {
            case DEFAULT -> normalBaseBlock = block;
            case STICKY -> stickyBaseBlock = block;
            default -> throw new IllegalArgumentException("unknown base type " + block.type);
        };
    }

    public void moving(BasicMovingBlock block) {
        this.movingBlock = block;
    }

    // For modded pistons that extend further than one block
    public void arm(LongPistonArmBlock block) {
        this.armBlock = block;
    }
}
