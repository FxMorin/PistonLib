package ca.fxco.configurablepistons.blocks.pistons.longPiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonBlockEntity.MAX_ARM_LENGTH;

public class LongPistonExtensionBlock extends BasicPistonExtensionBlock {

    public LongPistonExtensionBlock() {
        super();
    }

    public BlockEntity createLongPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock,
                                                   Direction facing, boolean extending, boolean source,
                                                   int maxLength, int length, boolean arm) {
        return new LongPistonBlockEntity(pos, state, pushedBlock, facing, extending, source, maxLength, length, arm);
    }

    @Override
    public BlockEntity createPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock,
                                               Direction facing, boolean extending, boolean source) {
        return new LongPistonBlockEntity(pos, state, pushedBlock, facing, extending, source,
                MAX_ARM_LENGTH, 0, false);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World w, BlockState state, BlockEntityType<T> t) {
        return checkType(t, ModBlockEntities.LONG_PISTON_BLOCK_ENTITY, LongPistonBlockEntity::tick);
    }
}
