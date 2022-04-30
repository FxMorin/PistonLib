package ca.fxco.configurablepistons.newBlocks.speedPiston;

import ca.fxco.configurablepistons.basePistons.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.base.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpeedPistonExtensionBlock extends BasicPistonExtensionBlock {

    private final float speed;

    public SpeedPistonExtensionBlock(float speed) {
        super();
        this.speed = speed;
    }

    @Override
    public BlockEntity createPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock,
                                                      Direction facing, boolean extending, boolean source) {
        return new SpeedPistonBlockEntity(this.speed, pos, state, pushedBlock, facing, extending, source);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World w, BlockState state, BlockEntityType<T> t) {
        return BasicPistonExtensionBlock.checkType(t, ModBlockEntities.SPEED_PISTON_BLOCK_ENTITY, SpeedPistonBlockEntity::tick);
    }
}
