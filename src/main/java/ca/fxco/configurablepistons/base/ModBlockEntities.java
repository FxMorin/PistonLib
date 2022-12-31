package ca.fxco.configurablepistons.base;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.fastPiston.FastPistonBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.slipperyPiston.SlipperyPistonBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.speedPiston.SpeedPistonBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.translocationPiston.TranslocationPistonBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston.StickyPistonBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static ca.fxco.configurablepistons.ConfigurablePistons.id;

public class ModBlockEntities {

    public static BlockEntityType<BasicPistonBlockEntity> BASIC_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<LongPistonBlockEntity> LONG_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<SpeedPistonBlockEntity> SPEED_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<FastPistonBlockEntity> FAST_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<StickyPistonBlockEntity> STICKY_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<TranslocationPistonBlockEntity> TRANSLOCATION_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<SlipperyPistonBlockEntity> SLIPPERY_PISTON_BLOCK_ENTITY;

    static {
        BASIC_PISTON_BLOCK_ENTITY = register(
                "basic",
                BasicPistonBlockEntity::new,
                ModBlocks.BASIC_MOVING_PISTON
        );
        LONG_PISTON_BLOCK_ENTITY = register(
                "long",
                LongPistonBlockEntity::new,
                ModBlocks.LONG_MOVING_PISTON
        );
        SPEED_PISTON_BLOCK_ENTITY = register(
                "speed",
                SpeedPistonBlockEntity::new,
                ModBlocks.STRONG_MOVING_PISTON
        );
        FAST_PISTON_BLOCK_ENTITY = register(
                "fast",
                FastPistonBlockEntity::new,
                ModBlocks.FAST_MOVING_PISTON
        );
        STICKY_PISTON_BLOCK_ENTITY = register(
                "sticky",
                StickyPistonBlockEntity::new,
                ModBlocks.STICKY_MOVING_PISTON
        );
        TRANSLOCATION_PISTON_BLOCK_ENTITY = register(
                "translocation",
                TranslocationPistonBlockEntity::new,
                ModBlocks.TRANSLOCATION_MOVING_PISTON
        );
        SLIPPERY_PISTON_BLOCK_ENTITY = register(
                "slippery",
                SlipperyPistonBlockEntity::new,
                ModBlocks.SLIPPERY_MOVING_PISTON
        );
    }

    public static <T extends BasicPistonBlockEntity> BlockEntityType<T> register(
            String id,
            FabricBlockEntityTypeBuilder.Factory<T> blockEntityFactory,
            BasicPistonExtensionBlock extensionBlock
    ) {
        Identifier identifier = id(id+"_piston_entity");
        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                identifier,
                FabricBlockEntityTypeBuilder.create(
                        blockEntityFactory,
                        extensionBlock
                ).build(null)
        );
    }

    public static void order() {}
}
