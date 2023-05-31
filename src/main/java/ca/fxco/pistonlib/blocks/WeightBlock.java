package ca.fxco.pistonlib.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WeightBlock extends Block {

    private final int weight;

    public WeightBlock(Properties properties, int weight) {
        super(properties);

        this.weight = weight;
    }

    public int pl$getWeight(BlockState state) {
        return this.weight;
    }
}
