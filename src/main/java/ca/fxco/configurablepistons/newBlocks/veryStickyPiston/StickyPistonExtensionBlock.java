package ca.fxco.configurablepistons.newBlocks.veryStickyPiston;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.base.BasicPistonExtensionBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StickyPistonExtensionBlock extends BasicPistonExtensionBlock {

    public StickyPistonExtensionBlock() {
        super();
    }

    @Override
    public BlockEntity createPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock,
                                               Direction facing, boolean extending, boolean source) {
        return new StickyPistonBlockEntity(pos, state, pushedBlock, facing, extending, source, ConfigurablePistons.STICKY_MOVING_PISTON);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World w, BlockState state, BlockEntityType<T> t) {
        return BasicPistonExtensionBlock.checkType(t, ConfigurablePistons.STICKY_PISTON_BLOCK_ENTITY, StickyPistonBlockEntity::tick);
    }
}
