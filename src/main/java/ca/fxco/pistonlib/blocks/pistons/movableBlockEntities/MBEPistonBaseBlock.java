package ca.fxco.pistonlib.blocks.pistons.movableBlockEntities;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

public class MBEPistonBaseBlock extends BasicPistonBaseBlock {

    public MBEPistonBaseBlock(PistonFamily family, PistonType type) {
        this(family, type, FabricBlockSettings.copyOf(Blocks.PISTON));
    }

    public MBEPistonBaseBlock(PistonFamily family, PistonType type, Properties properties) {
        super(family, type, properties);
    }

    @Override
    public boolean canMoveBlock(BlockState state) {
        return true;
    }
}
