package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.blocks.autoCraftingBlock.AutoCraftingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.configurablePiston.ConfigurableMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.fastPiston.FastMovingBlockEntity;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.movableBlockEntities.MBEMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.speedPiston.SpeedMovingBlockEntity;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static ca.fxco.pistonlib.PistonLib.id;

public class ModBlockEntities {

    // Pistons
    public static final BlockEntityType<BasicMovingBlockEntity> BASIC_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<ConfigurableMovingBlockEntity> CONFIGURABLE_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<SpeedMovingBlockEntity> SPEED_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<FastMovingBlockEntity> FAST_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<MBEMovingBlockEntity> MBE_MOVING_BLOCK_ENTITY;

    // Other
    public static final BlockEntityType<MergeBlockEntity> MERGE_BLOCK_ENTITY;
    public static final BlockEntityType<AutoCraftingBlockEntity> AUTO_CRAFTING_BLOCK_ENTITY;

    static {
        // Pistons
        BASIC_MOVING_BLOCK_ENTITY = register(
                "basic",
                BasicMovingBlockEntity::new,
                BasicMovingBlockEntity::new,
                ModPistonFamilies.BASIC,
                ModPistonFamilies.LONG,
                ModPistonFamilies.VERY_STICKY,
                ModPistonFamilies.SLIPPERY,
                ModPistonFamilies.STALE,
                ModPistonFamilies.VERY_QUASI,
                ModPistonFamilies.FRONT_POWERED,
                ModPistonFamilies.SUPER
        );
        CONFIGURABLE_MOVING_BLOCK_ENTITY = register(
                "configurable",
                ConfigurableMovingBlockEntity::new,
                ConfigurableMovingBlockEntity::new,
                ModPistonFamilies.CONFIGURABLE
        );
        SPEED_MOVING_BLOCK_ENTITY = register(
                "speed",
                SpeedMovingBlockEntity::new,
                SpeedMovingBlockEntity::new,
                ModPistonFamilies.STRONG
        );
        FAST_MOVING_BLOCK_ENTITY = register(
                "fast",
                FastMovingBlockEntity::new,
                FastMovingBlockEntity::new,
                ModPistonFamilies.FAST
        );
        MBE_MOVING_BLOCK_ENTITY = register(
                "mbe",
                MBEMovingBlockEntity::new,
                MBEMovingBlockEntity::new,
                ModPistonFamilies.MBE
        );

        // Other
        MERGE_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id("merge"),
                FabricBlockEntityTypeBuilder.create(MergeBlockEntity::new, ModBlocks.MERGE_BLOCK).build(null)
        );
        AUTO_CRAFTING_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id("auto_crafting_block"),
                FabricBlockEntityTypeBuilder.create(AutoCraftingBlockEntity::new, ModBlocks.AUTO_CRAFTING_BLOCK).build(null)
        );
    }

    private static <T extends BasicMovingBlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<T> factory1,
            BasicMovingBlockEntity.Factory<T> factory2,
            PistonFamily... families
    ) {
        return register(id(name+"_moving_block"), factory1, factory2, families);
    }

    public static <T extends BasicMovingBlockEntity> BlockEntityType<T> register(
            ResourceLocation id,
            FabricBlockEntityTypeBuilder.Factory<T> factory1,
            BasicMovingBlockEntity.Factory<T> factory2,
            PistonFamily... families
    ) {
        BlockEntityType<T> type = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id,
                FabricBlockEntityTypeBuilder.create(
                        factory1,
                        Util.make(new Block[families.length], blocks -> {
                            for (int i = 0; i < families.length; i++) {
                                blocks[i] = families[i].getMoving();
                            }
                        })
                ).build(null)
        );

        for (PistonFamily family : families) {
            family.setMovingBlockEntity(type, factory2);
        }

        return type;
    }

    public static void bootstrap() { }

}
