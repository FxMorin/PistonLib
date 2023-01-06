package ca.fxco.configurablepistons.base;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import ca.fxco.configurablepistons.blocks.AllSidedObserverBlock;
import ca.fxco.configurablepistons.blocks.GlueBlock;
import ca.fxco.configurablepistons.blocks.PoweredStickyBlock;
import ca.fxco.configurablepistons.blocks.PullOnlyBlock;
import ca.fxco.configurablepistons.blocks.SlimyPoweredBlock;
import ca.fxco.configurablepistons.blocks.StickyChainBlock;
import ca.fxco.configurablepistons.blocks.StickySidesBlock;
import ca.fxco.configurablepistons.blocks.StickylessBlock;
import ca.fxco.configurablepistons.blocks.halfBlocks.HalfHoneyBlock;
import ca.fxco.configurablepistons.blocks.halfBlocks.HalfObsidianBlock;
import ca.fxco.configurablepistons.blocks.halfBlocks.HalfPoweredBlock;
import ca.fxco.configurablepistons.blocks.halfBlocks.HalfSlimeBlock;
import ca.fxco.configurablepistons.blocks.pistons.FrontPoweredPistonBaseBlock;
import ca.fxco.configurablepistons.blocks.pistons.PushLimitPistonBaseBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.fastPiston.FastMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonArmBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonBaseBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.slipperyPiston.SlipperyPistonBaseBlock;
import ca.fxco.configurablepistons.blocks.pistons.slipperyPiston.SlipperyMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.slipperyPiston.SlipperyPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.speedPiston.SpeedMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.translocationPiston.TranslocationMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston.StickyMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston.StickyPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston.VeryStickyPistonBaseBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.SlipperyRedstoneBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.SlipperySlimeBlock;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.Material;

import static ca.fxco.configurablepistons.ConfigurablePistons.CUSTOM_CREATIVE_GROUP;
import static ca.fxco.configurablepistons.ConfigurablePistons.id;

public class ModBlocks {

    private static final boolean DATAGEN_ACTIVE = System.getProperty("fabric-api.datagen") != null;

    // Half Blocks
    public static final Block HALF_SLIME_BLOCK = register("half_slime",
            new HalfSlimeBlock(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK))
    );
    public static final Block HALF_HONEY_BLOCK = register("half_honey",
            new HalfHoneyBlock(FabricBlockSettings.copyOf(Blocks.HONEY_BLOCK))
    );
    public static final Block HALF_REDSTONE_BLOCK = register("half_redstone",
            new HalfPoweredBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK))
    );
    public static final Block HALF_OBSIDIAN_BLOCK = register("half_obsidian",
            new HalfObsidianBlock(FabricBlockSettings.copyOf(Blocks.OBSIDIAN))
    );

    // Create Custom Blocks
    public static final Block DRAG_BLOCK = register("drag_block",
            new PullOnlyBlock(FabricBlockSettings.of(Material.METAL).strength(22.0f).hardness(18.0f))
    );
    public static final Block STICKYLESS_BLOCK = register("stickyless_block",
            new StickylessBlock(FabricBlockSettings.of(Material.AMETHYST).strength(64.0f).hardness(64.0f))
    );
    public static final Block STICKY_TOP_BLOCK = register("sticky_top_block",
            new StickySidesBlock(FabricBlockSettings.copyOf(Blocks.STONE), Map.of(Direction.UP, StickyType.STICKY))
    );
    public static final Block SLIMY_REDSTONE_BLOCK = register("slimy_redstone_block",
            new SlimyPoweredBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK))
    );
    public static final Block ALL_SIDED_OBSERVER = register("all_sided_observer",
            new AllSidedObserverBlock(FabricBlockSettings.copyOf(Blocks.OBSERVER))
    );
    public static final Block GLUE_BLOCK = register("glue_block",
            new GlueBlock(FabricBlockSettings.copyOf(Blocks.END_STONE))
    );
    public static final Block POWERED_STICKY_BLOCK = register("powered_sticky_block",
            new PoweredStickyBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS))
    );
    public static final Block STICKY_CHAIN_BLOCK = register("sticky_chain",
            new StickyChainBlock(FabricBlockSettings.copyOf(Blocks.CHAIN))
    );

    // Slippery Blocks
    // These blocks if they are not touching a solid surface
    public static final Block SLIPPERY_SLIME_BLOCK = register("slippery_slime_block",
            new SlipperySlimeBlock(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK)), false
    );
    public static final Block SLIPPERY_REDSTONE_BLOCK = register("slippery_redstone_block",
            new SlipperyRedstoneBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK))
    );
    public static final Block SLIPPERY_STONE_BLOCK = register("slippery_stone_block",
            new BaseSlipperyBlock(FabricBlockSettings.copyOf(Blocks.STONE))
    );

    // Piston Blocks should always be initialized in the following order:
    // Piston heads, Moving Pistons, base piston blocks, Piston Arms

    // Basic Piston
    // Acts exactly like a normal vanilla piston
    public static final BasicPistonHeadBlock BASIC_PISTON_HEAD = registerPiston(
            PistonFamilies.BASIC, new BasicPistonHeadBlock()
    );
    public static final BasicMovingBlock BASIC_MOVING_BLOCK = registerPiston(
            PistonFamilies.BASIC, new BasicMovingBlock()
    );
    public static final BasicPistonBaseBlock BASIC_PISTON = registerPiston(
            PistonFamilies.BASIC, new BasicPistonBaseBlock(false)
    );
    public static final BasicPistonBaseBlock BASIC_STICKY_PISTON = registerPiston(
            PistonFamilies.BASIC, new BasicPistonBaseBlock(true)
    );

    // Basic Long Piston
    // Can extend further than 1 block
    public static final LongPistonHeadBlock LONG_PISTON_HEAD = registerPiston(
            PistonFamilies.LONG, new LongPistonHeadBlock()
    );
    public static final LongMovingBlock LONG_MOVING_BLOCK = registerPiston(
            PistonFamilies.LONG, new LongMovingBlock()
    );
    public static final LongPistonBaseBlock LONG_PISTON = registerPiston(
            PistonFamilies.LONG, new LongPistonBaseBlock(false), null
    );
    public static final BasicPistonBaseBlock LONG_STICKY_PISTON = registerPiston(
            PistonFamilies.LONG, new LongPistonBaseBlock(true), null
    );
    public static final LongPistonArmBlock LONG_PISTON_ARM = registerPiston(
            PistonFamilies.LONG, new LongPistonArmBlock()
    );

    // Strong Piston
    // Can push 24 blocks, although it takes a lot longer to push (0.05x slower)
    public static final BasicPistonHeadBlock STRONG_PISTON_HEAD = registerPiston(
            PistonFamilies.STRONG, new BasicPistonHeadBlock()
    );
    public static final SpeedMovingBlock STRONG_MOVING_BLOCK = registerPiston(
            PistonFamilies.STRONG, new SpeedMovingBlock(0.05F)
    );
    public static final BasicPistonBaseBlock STRONG_PISTON = registerPiston(
            PistonFamilies.STRONG, new PushLimitPistonBaseBlock(false, 24)
    );
    public static final BasicPistonBaseBlock STRONG_STICKY_PISTON = registerPiston(
            PistonFamilies.STRONG, new PushLimitPistonBaseBlock(true, 24)
    );

    // Fast Piston
    // Can only push 2 block, although it's very fast
    public static final BasicPistonHeadBlock FAST_PISTON_HEAD = registerPiston(
            PistonFamilies.FAST, new BasicPistonHeadBlock()
    );
    public static final FastMovingBlock FAST_MOVING_BLOCK = registerPiston(
            PistonFamilies.FAST, new FastMovingBlock()
    );
    public static final BasicPistonBaseBlock FAST_PISTON = registerPiston(
            PistonFamilies.FAST, new PushLimitPistonBaseBlock(false, 8)
    );
    public static final BasicPistonBaseBlock FAST_STICKY_PISTON = registerPiston(
            PistonFamilies.FAST, new PushLimitPistonBaseBlock(true, 8)
    );

    // Very Sticky Piston
    // It's face acts like a slime block, it can be pulled while extended.
    // Doing so will pull the moving piston with it as it retracts
    public static final BasicPistonHeadBlock STICKY_PISTON_HEAD = registerPiston(
            PistonFamilies.STICKY, new StickyPistonHeadBlock()
    );
    public static final StickyMovingBlock STICKY_MOVING_BLOCK = registerPiston(
            PistonFamilies.STICKY, new StickyMovingBlock()
    );
    public static final BasicPistonBaseBlock VERY_STICKY_PISTON = registerPiston(
            PistonFamilies.STICKY, new VeryStickyPistonBaseBlock()
    );


    // Front Powered Piston
    // Normal piston but can be powered through the front
    public static final BasicPistonHeadBlock FRONT_POWERED_PISTON_HEAD = registerPiston(
            PistonFamilies.FRONT_POWERED, new BasicPistonHeadBlock()
    );
    public static final BasicPistonBaseBlock FRONT_POWERED_PISTON = registerPiston(
            PistonFamilies.FRONT_POWERED, new FrontPoweredPistonBaseBlock(false)
    );
    public static final BasicPistonBaseBlock FRONT_POWERED_STICKY_PISTON = registerPiston(
            PistonFamilies.FRONT_POWERED, new FrontPoweredPistonBaseBlock(true)
    );


    // Translocation Piston
    // Normal piston but has 1.10 translocation
    public static final BasicPistonHeadBlock TRANSLOCATION_PISTON_HEAD = registerPiston(
            PistonFamilies.TRANSLOCATION, new BasicPistonHeadBlock()
    );
    public static final TranslocationMovingBlock TRANSLOCATION_MOVING_BLOCK = registerPiston(
            PistonFamilies.TRANSLOCATION, new TranslocationMovingBlock()
    );
    public static final BasicPistonBaseBlock TRANSLOCATION_PISTON = registerPiston(
            PistonFamilies.TRANSLOCATION, new BasicPistonBaseBlock(false)
    );
    public static final BasicPistonBaseBlock TRANSLOCATION_STICKY_PISTON = registerPiston(
            PistonFamilies.TRANSLOCATION, new BasicPistonBaseBlock(true)
    );

    // Slippery Piston
    // It's just a normal piston except its slippery
    public static final BasicPistonHeadBlock SLIPPERY_PISTON_HEAD = registerPiston(
            PistonFamilies.SLIPPERY, new SlipperyPistonHeadBlock()
    );
    public static final SlipperyMovingBlock SLIPPERY_MOVING_BLOCK = registerPiston(
            PistonFamilies.SLIPPERY, new SlipperyMovingBlock()
    );
    public static final BasicPistonBaseBlock SLIPPERY_PISTON = registerPiston(
            PistonFamilies.SLIPPERY, new SlipperyPistonBaseBlock(false)
    );
    public static final BasicPistonBaseBlock SLIPPERY_STICKY_PISTON = registerPiston(
            PistonFamilies.SLIPPERY, new SlipperyPistonBaseBlock(true)
    );

    // Super Piston
    // What's push limit? What is super sticky?
    public static final BasicPistonHeadBlock SUPER_PISTON_HEAD = registerPiston(
            PistonFamilies.SUPER, new BasicPistonHeadBlock()
    );
    public static final BasicPistonBaseBlock SUPER_PISTON = registerPiston(
            PistonFamilies.SUPER, new PushLimitPistonBaseBlock(false, Integer.MAX_VALUE)
    );
    public static final BasicPistonBaseBlock SUPER_STICKY_PISTON = registerPiston(
            PistonFamilies.SUPER, new PushLimitPistonBaseBlock(true, Integer.MAX_VALUE)
    );


    static <T extends Block> T register(String blockId, T block) {
        return register(blockId, block, true);
    }

    public static <T extends Block> T register(String blockId, T block, boolean autoDatagen) {
        //if (autoDatagen && DATAGEN_ACTIVE) DatagenInitializer.datagenBlockList.add(block);
        ResourceLocation identifier = id(blockId);
        Registry.register(BuiltInRegistries.ITEM, identifier, new BlockItem(block, new Item.Properties()));
        return Registry.register(BuiltInRegistries.BLOCK, identifier, block);
    }

    static <T extends Block> T registerPiston(PistonFamily family, T block) {
        return registerPiston(family, block, CUSTOM_CREATIVE_GROUP);
    }

    public static <T extends Block> T registerPiston(PistonFamily family, T block, @Nullable CreativeModeTab creativeGroup) {
        if (family == null)
            throw new IllegalStateException("Valid Piston Family must be used! - " + BuiltInRegistries.BLOCK.getId(block));
        String familyId = family.getId();
        if (family.mustSetupHead()) { // FIRST BLOCK INITIALIZED SHOULD ALWAYS BE THE HEAD!!!
            if (block instanceof BasicPistonHeadBlock headBlock) {
                Registry.register(BuiltInRegistries.BLOCK, id(familyId + "_piston_head"), block);
                family.head(headBlock);
            } else {
                throw new IllegalStateException(
                        "First Piston Family block must be a basic piston head block! - Block: " +
                                block + " - Family: " + family.getId()
                );
            }
        } else {
            if (block instanceof BasicPistonBaseBlock pistonBlock) {
                BasicMovingBlock extensionBlock = family.getMovingBlock();
                pistonBlock.setMovingBlock(extensionBlock != null ? extensionBlock : ModBlocks.BASIC_MOVING_BLOCK);
                BasicPistonHeadBlock headBlock = family.getHeadBlock();
                pistonBlock.setHeadBlock(headBlock != null ? headBlock : ModBlocks.BASIC_PISTON_HEAD);
                ResourceLocation identifier;
                if (pistonBlock.isSticky) {
                    identifier = id(familyId + "_sticky_piston");
                    Registry.register(BuiltInRegistries.BLOCK, identifier, pistonBlock);
                    family.base(PistonType.STICKY, pistonBlock);
                } else {
                    identifier = id(familyId + "_piston");
                    Registry.register(BuiltInRegistries.BLOCK, identifier, pistonBlock);
                    family.base(PistonType.DEFAULT, pistonBlock);
                }
                if (creativeGroup != null) {
                    Registry.register(BuiltInRegistries.ITEM, identifier, new BlockItem(pistonBlock, new Item.Properties()));
                }
            } else if (block instanceof BasicMovingBlock basicPistonExtensionBlock) {
                Registry.register(BuiltInRegistries.BLOCK, id(familyId + "_moving_piston"), basicPistonExtensionBlock);
                if (family.getBaseBlock(PistonType.DEFAULT) != null || family.getBaseBlock(PistonType.STICKY) != null) {
                    throw new IllegalStateException(
                            "Extension blocks must always be initialized before base piston blocks! - Block: " +
                                    BuiltInRegistries.BLOCK.getId(basicPistonExtensionBlock) + " - Family: " + family.getId()
                    );
                }
                family.moving(basicPistonExtensionBlock);
            } else if (block instanceof LongPistonArmBlock longPistonArmBlock) {
                Registry.register(BuiltInRegistries.BLOCK, id(familyId + "_piston_arm"), longPistonArmBlock);
                BasicPistonHeadBlock headBlock = family.getHeadBlock();
                if (headBlock instanceof LongPistonHeadBlock longHeadBlock) {
                    longPistonArmBlock.setHeadBlock(longHeadBlock);
                } else {
                    throw new IllegalStateException(
                            "Pistons using the LongPistonArmBlock, must also use a LongPistonHeadBlock"
                    );
                }
                family.arm(longPistonArmBlock);
            } else {
                if (!family.hasCustomBlockLogic(block)) {
                    throw new IllegalStateException(
                            "This block cannot be initialized as part of a piston family! - Block: " +
                                    block + " - Family: " + family.getId()
                    );
                }
            }
        }
        return block;
    }

    public static void order() {}
}
