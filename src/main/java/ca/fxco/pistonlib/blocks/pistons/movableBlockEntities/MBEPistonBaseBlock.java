package ca.fxco.pistonlib.blocks.pistons.movableBlockEntities;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

public class MBEPistonBaseBlock extends BasicPistonBaseBlock {

    public MBEPistonBaseBlock(PistonType type) {
        this(type, FabricBlockSettings.copyOf(Blocks.PISTON));
    }

    public MBEPistonBaseBlock(PistonType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public boolean canMoveBlock(BlockState state) {
        return true;
    }
}
