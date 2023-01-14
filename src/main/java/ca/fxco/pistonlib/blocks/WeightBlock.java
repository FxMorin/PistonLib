package ca.fxco.pistonlib.blocks;

import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WeightBlock extends Block implements ConfigurablePistonBehavior {

    private final int weight;

    public WeightBlock(Properties properties, int weight) {
        super(properties);

        this.weight = weight;
    }

    public int getWeight(BlockState state) {
        return this.weight;
    }
}
