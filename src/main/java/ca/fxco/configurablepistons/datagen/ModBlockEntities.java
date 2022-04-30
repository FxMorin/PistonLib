package ca.fxco.configurablepistons.datagen;

import ca.fxco.configurablepistons.base.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.newBlocks.fastPiston.FastPistonBlockEntity;
import ca.fxco.configurablepistons.newBlocks.speedPiston.SpeedPistonBlockEntity;
import ca.fxco.configurablepistons.newBlocks.veryStickyPiston.StickyPistonBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import static ca.fxco.configurablepistons.ConfigurablePistons.id;

public class ModBlockEntities {

    public static BlockEntityType<BasicPistonBlockEntity> BASIC_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<SpeedPistonBlockEntity> SPEED_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<FastPistonBlockEntity> FAST_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<StickyPistonBlockEntity> STICKY_PISTON_BLOCK_ENTITY;

    static {
        BASIC_PISTON_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("basic_piston_entity"), FabricBlockEntityTypeBuilder.create(BasicPistonBlockEntity::new, ModBlocks.BASIC_MOVING_PISTON).build(null));
        SPEED_PISTON_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("speed_piston_entity"), FabricBlockEntityTypeBuilder.create(SpeedPistonBlockEntity::new, ModBlocks.STRONG_MOVING_PISTON).build(null));
        FAST_PISTON_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("fast_piston_entity"), FabricBlockEntityTypeBuilder.create(FastPistonBlockEntity::new, ModBlocks.FAST_MOVING_PISTON).build(null));
        STICKY_PISTON_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("sticky_piston_entity"), FabricBlockEntityTypeBuilder.create(StickyPistonBlockEntity::new, ModBlocks.STICKY_MOVING_PISTON).build(null));
    }
}
