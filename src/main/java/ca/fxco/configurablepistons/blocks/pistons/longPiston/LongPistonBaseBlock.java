package ca.fxco.configurablepistons.blocks.pistons.longPiston;

import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurableLongPistonHandler;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurablePistonStructureResolver;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.PistonType;

public class LongPistonBaseBlock extends BasicPistonBaseBlock {

    public LongPistonBaseBlock(PistonType type) {
        this(type, ModBlocks.LONG_MOVING_BLOCK, ModBlocks.LONG_PISTON_HEAD);
    }

    public LongPistonBaseBlock(PistonType type, Properties properties) {
        this(type, properties, ModBlocks.LONG_MOVING_BLOCK, ModBlocks.LONG_PISTON_HEAD);
    }

    public LongPistonBaseBlock(PistonType type, LongMovingBlock movingBlock, LongPistonHeadBlock headBlock) {
        this(type, FabricBlockSettings.copyOf(Blocks.PISTON), movingBlock, headBlock);
    }

    public LongPistonBaseBlock(PistonType type, Properties properties,
                            LongMovingBlock movingBlock, LongPistonHeadBlock headBlock) {
        super(type, properties, movingBlock, headBlock);
    }

    @Override
    public ConfigurablePistonStructureResolver createStructureResolver(Level world, BlockPos pos, Direction facing, boolean extend) {
        return new ConfigurableLongPistonHandler(world, pos, facing, extend, MOVING_BLOCK);
    }

    @Override
    public LongMovingBlock getMovingBlock() {
        return (LongMovingBlock)MOVING_BLOCK;
    }

    @Override
    public LongPistonHeadBlock getHeadBlock() {
        return (LongPistonHeadBlock)HEAD_BLOCK;
    }

    @Override
    public void setMovingBlock(BasicMovingBlock movingBlock) {
        if (!(movingBlock instanceof LongMovingBlock))
            throw new IllegalStateException("LongPistonBlock's extension block must extend LongMovingBlock");
        MOVING_BLOCK = movingBlock;
    }

    @Override
    public void setHeadBlock(BasicPistonHeadBlock headBlock) {
        if (!(headBlock instanceof LongPistonHeadBlock))
            throw new IllegalStateException("LongPistonBlock's head block must extend LongPistonHeadBlock");
        HEAD_BLOCK = headBlock;
    }
}
