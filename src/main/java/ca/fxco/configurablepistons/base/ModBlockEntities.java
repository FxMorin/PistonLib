package ca.fxco.configurablepistons.base;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.fastPiston.FastPistonBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.speedPiston.SpeedPistonBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston.StickyPistonBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import static ca.fxco.configurablepistons.ConfigurablePistons.id;

public class ModBlockEntities {

    public static BlockEntityType<BasicPistonBlockEntity> BASIC_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<SpeedPistonBlockEntity> SPEED_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<FastPistonBlockEntity> FAST_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<StickyPistonBlockEntity> STICKY_PISTON_BLOCK_ENTITY;

    static {
        BASIC_PISTON_BLOCK_ENTITY = register(
                "basic_piston_entity",
                BasicPistonBlockEntity::new,
                ModBlocks.BASIC_MOVING_PISTON
        );
        SPEED_PISTON_BLOCK_ENTITY = register(
                "speed_piston_entity",
                SpeedPistonBlockEntity::new,
                ModBlocks.STRONG_MOVING_PISTON
        );
        FAST_PISTON_BLOCK_ENTITY = register(
                "fast_piston_entity",
                FastPistonBlockEntity::new,
                ModBlocks.FAST_MOVING_PISTON
        );
        STICKY_PISTON_BLOCK_ENTITY = register(
                "sticky_piston_entity",
                StickyPistonBlockEntity::new,
                ModBlocks.STICKY_MOVING_PISTON
        );
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(
            String identifier,
            FabricBlockEntityTypeBuilder.Factory<T> blockEntityFactory,
            BasicPistonExtensionBlock basicPistonExtensionBlock
    ) {
        return Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                id(identifier),
                FabricBlockEntityTypeBuilder.create(
                        blockEntityFactory,
                        basicPistonExtensionBlock
                ).build(null));
    }
}
