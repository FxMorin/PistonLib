package ca.fxco.pistonlib.blocks.pistons.longPiston;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.pistonlib.pistonLogic.pistonHandlers.ConfigurableLongPistonHandler;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.properties.PistonType;

public class LongPistonBaseBlock extends BasicPistonBaseBlock {

    public LongPistonBaseBlock(PistonType type) {
        this(type, FabricBlockSettings.copyOf(Blocks.PISTON));
    }

    public LongPistonBaseBlock(PistonType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public PistonStructureResolver newStructureResolver(Level level, BlockPos pos, Direction facing, boolean extend) {
        return new ConfigurableLongPistonHandler(this, level, pos, facing, extend, MOVING_BLOCK);
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
