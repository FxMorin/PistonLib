package ca.fxco.configurablepistons.base;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.fastPiston.FastMovingBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongMovingBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.slipperyPiston.SlipperyMovingBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.speedPiston.SpeedMovingBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.translocationPiston.TranslocationMovingBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston.StickyMovingBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static ca.fxco.configurablepistons.ConfigurablePistons.id;

public class ModBlockEntities {

    public static final BlockEntityType<BasicMovingBlockEntity> BASIC_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<LongMovingBlockEntity> LONG_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<SpeedMovingBlockEntity> SPEED_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<FastMovingBlockEntity> FAST_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<StickyMovingBlockEntity> STICKY_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<TranslocationMovingBlockEntity> TRANSLOCATION_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<SlipperyMovingBlockEntity> SLIPPERY_MOVING_BLOCK_ENTITY;

    static {
        BASIC_MOVING_BLOCK_ENTITY = register(
                "basic",
                BasicMovingBlockEntity::new,
                ModBlocks.BASIC_MOVING_BLOCK
        );
        LONG_MOVING_BLOCK_ENTITY = register(
                "long",
                LongMovingBlockEntity::new,
                ModBlocks.LONG_MOVING_BLOCK
        );
        SPEED_MOVING_BLOCK_ENTITY = register(
                "speed",
                SpeedMovingBlockEntity::new,
                ModBlocks.STRONG_MOVING_BLOCK
        );
        FAST_MOVING_BLOCK_ENTITY = register(
                "fast",
                FastMovingBlockEntity::new,
                ModBlocks.FAST_MOVING_BLOCK
        );
        STICKY_MOVING_BLOCK_ENTITY = register(
                "sticky",
                StickyMovingBlockEntity::new,
                ModBlocks.STICKY_MOVING_BLOCK
        );
        TRANSLOCATION_MOVING_BLOCK_ENTITY = register(
                "translocation",
                TranslocationMovingBlockEntity::new,
                ModBlocks.TRANSLOCATION_MOVING_BLOCK
        );
        SLIPPERY_MOVING_BLOCK_ENTITY = register(
                "slippery",
                SlipperyMovingBlockEntity::new,
                ModBlocks.SLIPPERY_MOVING_BLOCK
        );
    }

    public static <T extends BasicMovingBlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<T> blockEntityFactory,
            BasicMovingBlock extensionBlock
    ) {
        return Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id(name+"_piston_entity"),
                FabricBlockEntityTypeBuilder.create(
                        blockEntityFactory,
                        extensionBlock
                ).build(null)
        );
    }

    public static void order() {}
}
