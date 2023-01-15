package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.blocks.autoCraftingBlock.AutoCraftingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.configurablePiston.ConfigurableMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.fastPiston.FastMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.longPiston.LongMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.movableBlockEntities.MBEMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.slipperyPiston.SlipperyMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.speedPiston.SpeedMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.translocationPiston.TranslocationMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.veryStickyPiston.StickyMovingBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static ca.fxco.pistonlib.PistonLib.id;

public class ModBlockEntities {

    // Pistons
    public static final BlockEntityType<BasicMovingBlockEntity> BASIC_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<ConfigurableMovingBlockEntity> CONFIGURABLE_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<LongMovingBlockEntity> LONG_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<SpeedMovingBlockEntity> SPEED_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<FastMovingBlockEntity> FAST_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<StickyMovingBlockEntity> STICKY_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<TranslocationMovingBlockEntity> TRANSLOCATION_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<SlipperyMovingBlockEntity> SLIPPERY_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<MBEMovingBlockEntity> MBE_MOVING_BLOCK_ENTITY;

    // Other
    public static final BlockEntityType<MergeBlockEntity> MERGE_BLOCK_ENTITY;
    public static final BlockEntityType<AutoCraftingBlockEntity> AUTO_CRAFTING_BLOCK_ENTITY;

    static {
        // Pistons
        BASIC_MOVING_BLOCK_ENTITY = register(
                "basic",
                BasicMovingBlockEntity::new,
                ModBlocks.BASIC_MOVING_BLOCK
        );
        CONFIGURABLE_MOVING_BLOCK_ENTITY = register(
                "configurable",
                ConfigurableMovingBlockEntity::new,
                ModBlocks.CONFIGURABLE_MOVING_BLOCK
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
        MBE_MOVING_BLOCK_ENTITY = register(
                "mbe",
                MBEMovingBlockEntity::new,
                ModBlocks.MBE_MOVING_BLOCK
        );

        // Other
        MERGE_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id("merge_entity"),
                FabricBlockEntityTypeBuilder.create(MergeBlockEntity::new, ModBlocks.MERGE_BLOCK).build(null)
        );
        AUTO_CRAFTING_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id("auto_crafting_entity"),
                FabricBlockEntityTypeBuilder.create(AutoCraftingBlockEntity::new, ModBlocks.AUTO_CRAFTING_BLOCK).build(null)
        );
    }

    public static <T extends BasicMovingBlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<T> blockEntityFactory,
            BasicMovingBlock... movingBlock
    ) {
        return Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id(name+"_piston_entity"),
                FabricBlockEntityTypeBuilder.create(
                        blockEntityFactory,
                        movingBlock
                ).build(null)
        );
    }

    public static void order() {}
}
