package ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.base.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
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
        return new StickyPistonBlockEntity(pos, state, pushedBlock, facing, extending, source,
                ModBlocks.STICKY_MOVING_PISTON);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World w, BlockState state, BlockEntityType<T> t) {
        return checkType(t, ModBlockEntities.STICKY_PISTON_BLOCK_ENTITY,StickyPistonBlockEntity::tick);
    }
}
