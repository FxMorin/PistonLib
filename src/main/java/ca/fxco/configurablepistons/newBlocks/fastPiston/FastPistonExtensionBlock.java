package ca.fxco.configurablepistons.newBlocks.fastPiston;

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

public class FastPistonExtensionBlock extends BasicPistonExtensionBlock {

    public FastPistonExtensionBlock() {
        super();
    }

    @Override
    public BlockEntity createPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock,
                                               Direction facing, boolean extending, boolean source) {
        return new FastPistonBlockEntity(pos, state, pushedBlock, facing, extending, source, ConfigurablePistons.FAST_MOVING_PISTON);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World w, BlockState state, BlockEntityType<T> t) {
        return BasicPistonExtensionBlock.checkType(t, ConfigurablePistons.FAST_PISTON_BLOCK_ENTITY, FastPistonBlockEntity::tick);
    }
}
