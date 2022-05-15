package ca.fxco.configurablepistons.blocks.pistons.longPiston;

import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurableLongPistonHandler;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurablePistonHandler;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LongPistonBlock extends BasicPistonBlock {

    public LongPistonBlock(boolean sticky) {
        this(sticky, ModBlocks.LONG_MOVING_PISTON, ModBlocks.LONG_PISTON_HEAD);
    }

    public LongPistonBlock(boolean sticky, AbstractBlock.Settings settings) {
        this(sticky, settings, ModBlocks.LONG_MOVING_PISTON, ModBlocks.LONG_PISTON_HEAD);
    }

    public LongPistonBlock(boolean sticky, LongPistonExtensionBlock extensionBlock, LongPistonHeadBlock headBlock) {
        this(sticky, FabricBlockSettings.copyOf(Blocks.PISTON), extensionBlock, headBlock);
    }

    public LongPistonBlock(boolean sticky, AbstractBlock.Settings settings,
                            LongPistonExtensionBlock extensionBlock, LongPistonHeadBlock headBlock) {
        super(sticky, settings, extensionBlock, headBlock);
    }

    @Override
    public ConfigurablePistonHandler getPistonHandler(World world, BlockPos pos, Direction dir, boolean retract) {
        return new ConfigurableLongPistonHandler(world, pos, dir, retract, EXTENSION_BLOCK);
    }

    @Override
    public LongPistonExtensionBlock getExtensionBlock() {
        return (LongPistonExtensionBlock)EXTENSION_BLOCK;
    }

    @Override
    public LongPistonHeadBlock getHeadBlock() {
        return (LongPistonHeadBlock)HEAD_BLOCK;
    }

    @Override
    public void setExtensionBlock(BasicPistonExtensionBlock extensionBlock) {
        if (!(extensionBlock instanceof LongPistonExtensionBlock))
            throw new IllegalStateException("LongPistonBlock's extension block must extend LongPistonExtensionBlock");
        EXTENSION_BLOCK = extensionBlock;
    }

    @Override
    public void setHeadBlock(BasicPistonHeadBlock headBlock) {
        if (!(headBlock instanceof LongPistonHeadBlock))
            throw new IllegalStateException("LongPistonBlock's head block must extend LongPistonHeadBlock");
        HEAD_BLOCK = headBlock;
    }
}
