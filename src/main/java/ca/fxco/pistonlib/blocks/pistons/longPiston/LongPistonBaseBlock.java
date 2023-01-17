package ca.fxco.pistonlib.blocks.pistons.longPiston;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.BasicStructureResolver;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.ConfigurableLongPistonHandler;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.PistonType;

public class LongPistonBaseBlock extends BasicPistonBaseBlock {

    public LongPistonBaseBlock(PistonFamily family, PistonType type) {
        this(family, type, FabricBlockSettings.copyOf(Blocks.PISTON));
    }

    public LongPistonBaseBlock(PistonFamily family, PistonType type, Properties properties) {
        super(family, type, properties);
    }

    @Override
    public BasicStructureResolver newStructureResolver(Level level, BlockPos pos, Direction facing, boolean extend) {
        return new ConfigurableLongPistonHandler(this, level, pos, facing, extend);
    }
}
