package ca.fxco.configurablepistons.base;

import ca.fxco.configurablepistons.blocks.pistons.FrontPoweredPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.PushLimitPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonArmBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.slipperyPiston.SlipperyPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.slipperyPiston.SlipperyPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.slipperyPiston.SlipperyPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.translocationPiston.TranslocationPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston.StickyPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.AbstractSlipperyBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.SlipperyRedstoneBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.SlipperySlimeBlock;
import ca.fxco.configurablepistons.datagen.DatagenInitializer;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.blocks.*;
import ca.fxco.configurablepistons.blocks.pistons.fastPiston.FastPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.speedPiston.SpeedPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston.StickyPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston.VeryStickyPistonBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.Map;

import static ca.fxco.configurablepistons.ConfigurablePistons.CUSTOM_CREATIVE_GROUP;
import static ca.fxco.configurablepistons.ConfigurablePistons.id;

public class ModBlocks {

    private static final boolean DATAGEN_ACTIVE = System.getProperty("fabric-api.datagen") != null;

    // Create Custom Blocks
    public static final Block DRAG_BLOCK = registerBlock("drag_block",
            new PullOnlyBlock(FabricBlockSettings.of(Material.METAL).strength(22.0f).hardness(18.0f))
    );
    public static final Block STICKYLESS_BLOCK = registerBlock("stickyless_block",
            new StickylessBlock(FabricBlockSettings.of(Material.AMETHYST).strength(64.0f).hardness(64.0f))
    );
    public static final Block STICKY_TOP_BLOCK = registerBlock("sticky_top_block",
            new StickySidesBlock(FabricBlockSettings.copyOf(Blocks.STONE), Map.of(Direction.UP, StickyType.STICKY))
    );
    public static final Block SLIMY_REDSTONE_BLOCK = registerBlock("slimy_redstone_block",
            new RedstoneBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK))
    );
    public static final Block SLIPPERY_SLIME_BLOCK = registerBlock("slippery_slime_block",
            new SlipperySlimeBlock(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK)), false
    );
    public static final Block SLIPPERY_REDSTONE_BLOCK = registerBlock("slippery_redstone_block",
            new SlipperyRedstoneBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK))
    );
    public static final Block SLIPPERY_STONE_BLOCK = registerBlock("slippery_stone_block",
            new AbstractSlipperyBlock(FabricBlockSettings.copyOf(Blocks.STONE))
    );
    public static final Block ALL_SIDED_OBSERVER_BLOCK = registerBlock("all_sided_observer_block",
            new AllSidedObserverBlock(FabricBlockSettings.copyOf(Blocks.OBSERVER))
    );
    public static final Block GLUE_BLOCK = registerBlock("glue_block",
            new GlueBlock(FabricBlockSettings.copyOf(Blocks.END_STONE))
    );
    public static final Block POWERED_STICKY_BLOCK = registerBlock("powered_sticky_block",
            new PoweredStickyBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS))
    );

    // Piston Blocks should always be initialized in the following order:
    // Piston heads, Moving Pistons, base piston blocks, Piston Arms

    // Basic Piston
    // Acts exactly like a normal vanilla piston
    public static final BasicPistonHeadBlock BASIC_PISTON_HEAD = registerPiston(
            PistonFamilies.BASIC, new BasicPistonHeadBlock()
    );
    public static final BasicPistonExtensionBlock BASIC_MOVING_PISTON = registerPiston(
            PistonFamilies.BASIC, new BasicPistonExtensionBlock()
    );
    public static final BasicPistonBlock BASIC_PISTON = registerPiston(
            PistonFamilies.BASIC, new BasicPistonBlock(false)
    );
    public static final BasicPistonBlock BASIC_STICKY_PISTON = registerPiston(
            PistonFamilies.BASIC, new BasicPistonBlock(true)
    );

    // Basic Long Piston
    // Can extend further than 1 block
    public static final LongPistonHeadBlock LONG_PISTON_HEAD = registerPiston(
            PistonFamilies.LONG, new LongPistonHeadBlock()
    );
    public static final LongPistonExtensionBlock LONG_MOVING_PISTON = registerPiston(
            PistonFamilies.LONG, new LongPistonExtensionBlock()
    );
    public static final LongPistonBlock LONG_PISTON = registerPiston(
            PistonFamilies.LONG, new LongPistonBlock(false)
    );
    public static final BasicPistonBlock LONG_STICKY_PISTON = registerPiston(
            PistonFamilies.LONG, new LongPistonBlock(true)
    );
    public static final LongPistonArmBlock LONG_PISTON_ARM = registerPiston(
            PistonFamilies.LONG, new LongPistonArmBlock()
    );

    // Strong Piston
    // Can push 24 blocks, although it takes a lot longer to push (0.05x slower)
    public static final BasicPistonHeadBlock STRONG_PISTON_HEAD = registerPiston(
            PistonFamilies.STRONG, new BasicPistonHeadBlock()
    );
    public static final SpeedPistonExtensionBlock STRONG_MOVING_PISTON = registerPiston(
            PistonFamilies.STRONG, new SpeedPistonExtensionBlock(0.05F)
    );
    public static final BasicPistonBlock STRONG_PISTON = registerPiston(
            PistonFamilies.STRONG, new PushLimitPistonBlock(false,24)
    );
    public static final BasicPistonBlock STRONG_STICKY_PISTON = registerPiston(
            PistonFamilies.STRONG, new PushLimitPistonBlock(true,24)
    );

    // Fast Piston
    // Can only push 2 block, although it's very fast
    public static final BasicPistonHeadBlock FAST_PISTON_HEAD = registerPiston(
            PistonFamilies.FAST, new BasicPistonHeadBlock()
    );
    public static final FastPistonExtensionBlock FAST_MOVING_PISTON = registerPiston(
            PistonFamilies.FAST, new FastPistonExtensionBlock()
    );
    public static final BasicPistonBlock FAST_PISTON = registerPiston(
            PistonFamilies.FAST, new PushLimitPistonBlock(false,8)
    );
    public static final BasicPistonBlock FAST_STICKY_PISTON = registerPiston(
            PistonFamilies.FAST, new PushLimitPistonBlock(true,8)
    );

    // Very Sticky Piston
    // It's face acts like a slime block, it can be pulled while extended.
    // Doing so will pull the moving piston with it as it retracts
    public static final BasicPistonHeadBlock STICKY_PISTON_HEAD = registerPiston(
            PistonFamilies.STICKY, new StickyPistonHeadBlock()
    );
    public static final StickyPistonExtensionBlock STICKY_MOVING_PISTON = registerPiston(
            PistonFamilies.STICKY, new StickyPistonExtensionBlock()
    );
    public static final BasicPistonBlock VERY_STICKY_PISTON = registerPiston(
            PistonFamilies.STICKY, new VeryStickyPistonBlock()
    );


    // Front Powered Piston
    // Normal piston but can be powered through the front
    public static final BasicPistonHeadBlock FRONT_POWERED_PISTON_HEAD = registerPiston(
            PistonFamilies.FRONT_POWERED, new BasicPistonHeadBlock()
    );
    public static final BasicPistonBlock FRONT_POWERED_PISTON = registerPiston(
            PistonFamilies.FRONT_POWERED, new FrontPoweredPistonBlock(false)
    );
    public static final BasicPistonBlock FRONT_POWERED_STICKY_PISTON = registerPiston(
            PistonFamilies.FRONT_POWERED, new FrontPoweredPistonBlock(true)
    );


    // Translocation Piston
    // Normal piston but has 1.10 translocation
    public static final BasicPistonHeadBlock TRANSLOCATION_PISTON_HEAD = registerPiston(
            PistonFamilies.TRANSLOCATION, new BasicPistonHeadBlock()
    );
    public static final TranslocationPistonExtensionBlock TRANSLOCATION_MOVING_PISTON = registerPiston(
            PistonFamilies.TRANSLOCATION, new TranslocationPistonExtensionBlock()
    );
    public static final BasicPistonBlock TRANSLOCATION_PISTON = registerPiston(
            PistonFamilies.TRANSLOCATION, new BasicPistonBlock(false)
    );
    public static final BasicPistonBlock TRANSLOCATION_STICKY_PISTON = registerPiston(
            PistonFamilies.TRANSLOCATION, new BasicPistonBlock(true)
    );

    // Slippery Piston
    // It's just a normal piston except its slippery
    public static final BasicPistonHeadBlock SLIPPERY_PISTON_HEAD = registerPiston(
            PistonFamilies.SLIPPERY, new SlipperyPistonHeadBlock()
    );
    public static final SlipperyPistonExtensionBlock SLIPPERY_MOVING_PISTON = registerPiston(
            PistonFamilies.SLIPPERY, new SlipperyPistonExtensionBlock()
    );
    public static final BasicPistonBlock SLIPPERY_PISTON = registerPiston(
            PistonFamilies.SLIPPERY, new SlipperyPistonBlock(false)
    );
    public static final BasicPistonBlock SLIPPERY_STICKY_PISTON = registerPiston(
            PistonFamilies.SLIPPERY, new SlipperyPistonBlock(true)
    );


    public static <T extends Block> T registerBlock(String blockId, T block) {
        return registerBlock(blockId, block, true);
    }

    public static <T extends Block> T registerBlock(String blockId, T block, boolean autoDatagen) {
        if (autoDatagen && DATAGEN_ACTIVE) DatagenInitializer.datagenBlockList.add(block);
        Identifier identifier = id(blockId);
        Registry.register(Registry.ITEM, identifier, new BlockItem(block, CUSTOM_CREATIVE_GROUP));
        return Registry.register(Registry.BLOCK, identifier, block);
    }

    public static <T extends Block> T registerPiston(PistonFamily family, T block) {
        if (family == null)
            throw new IllegalStateException("Valid Piston Family must be used! - " + Registry.BLOCK.getId(block));
        String familyId = family.getId();
        if (family.mustSetupHead()) { // FIRST BLOCK INITIALIZED SHOULD ALWAYS BE THE HEAD!!!
            if (block instanceof BasicPistonHeadBlock headBlock) {
                Registry.register(Registry.BLOCK, id(familyId+"_piston_head"), block);
                family.head(headBlock);
            } else {
                throw new IllegalStateException(
                        "First Piston Family block must be a basic piston head block! - Block: " +
                                block + " - Family: " + family.getId()
                );
            }
        } else {
            if (block instanceof BasicPistonBlock pistonBlock) {
                BasicPistonExtensionBlock extensionBlock = family.getExtensionBlock();
                pistonBlock.setExtensionBlock(extensionBlock != null ? extensionBlock : ModBlocks.BASIC_MOVING_PISTON);
                BasicPistonHeadBlock headBlock = family.getHeadBlock();
                pistonBlock.setHeadBlock(headBlock != null ? headBlock : ModBlocks.BASIC_PISTON_HEAD);
                Identifier identifier;
                if (pistonBlock.sticky) {
                    identifier = id(familyId+"_sticky_piston");
                    Registry.register(Registry.BLOCK, identifier, pistonBlock);
                    family.sticky(pistonBlock);
                } else {
                    identifier = id(familyId+"_piston");
                    Registry.register(Registry.BLOCK, identifier, pistonBlock);
                    family.piston(pistonBlock);
                }
                Registry.register(Registry.ITEM, identifier, new BlockItem(pistonBlock, CUSTOM_CREATIVE_GROUP));
            } else if (block instanceof BasicPistonExtensionBlock basicPistonExtensionBlock) {
                Registry.register(Registry.BLOCK, id(familyId+"_moving_piston"), basicPistonExtensionBlock);
                if (family.getPistonBlock() != null || family.getStickyPistonBlock() != null) {
                    throw new IllegalStateException(
                            "Extension blocks must always be initialized before base piston blocks! - Block: " +
                                    Registry.BLOCK.getId(basicPistonExtensionBlock) + " - Family: " + family.getId()
                    );
                }
                family.extension(basicPistonExtensionBlock);
            } else if (block instanceof LongPistonArmBlock longPistonArmBlock) {
                Registry.register(Registry.BLOCK, id(familyId+"_piston_arm"), longPistonArmBlock);
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
                throw new IllegalStateException(
                        "This block cannot be initialized as part of a piston family! - Block: " +
                                block + " - Family: " + family.getId()
                );
            }
        }
        return block;
    }

    public static void order() {}
}
