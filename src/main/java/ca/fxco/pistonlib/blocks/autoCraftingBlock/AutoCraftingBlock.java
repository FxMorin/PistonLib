package ca.fxco.pistonlib.blocks.autoCraftingBlock;

import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonMerging;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AutoCraftingBlock extends BaseEntityBlock implements ConfigurablePistonMerging {

    // TODO: Add GUI so people can see whats happening internally

    public AutoCraftingBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AutoCraftingBlockEntity(blockPos, blockState);
    }

    @Override
    public boolean usesConfigurablePistonMerging() {
        return true;
    }
}
