package ca.fxco.configurablepistons.base;

import ca.fxco.configurablepistons.blocks.pistons.FrontPoweredPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.PushLimitPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonArmBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.translocationPiston.TranslocationPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston.StickyPistonHeadBlock;
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
    public static final Block DRAG_BLOCK = registerBlock("drag_block", new PullOnlyBlock(
            FabricBlockSettings.of(Material.METAL).strength(22.0f).hardness(18.0f))
    );
    public static final Block STICKYLESS_BLOCK = registerBlock("stickyless_block",new StickylessBlock(
            FabricBlockSettings.of(Material.AMETHYST).strength(64.0f).hardness(64.0f))
    );
    public static final Block STICKY_TOP_BLOCK = registerBlock("sticky_top_block",new StickySidesBlock(
            FabricBlockSettings.copyOf(Blocks.STONE), Map.of(Direction.UP, StickyType.STICKY))
    );
    public static final Block SLIMY_REDSTONE_BLOCK = registerBlock("slimy_redstone_block",
            new RedstoneBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK))
    );
    public static final Block SLIPPERY_SLIME_BLOCK = registerBlock("slippery_slime_block.json",
            new SlipperyBlock(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK).nonOpaque()), false
    );

    // Piston Blocks should always be initialized in the following order:
    // Piston heads, Moving Pistons, base piston blocks

    // Basic Piston
    // Acts exactly like a normal vanilla piston
    public static final BasicPistonHeadBlock BASIC_PISTON_HEAD = register(
            PistonFamilies.BASIC, new BasicPistonHeadBlock()
    );
    public static final BasicPistonExtensionBlock BASIC_MOVING_PISTON = register(
            PistonFamilies.BASIC, new BasicPistonExtensionBlock()
    );
    public static final BasicPistonBlock BASIC_PISTON = register(
            PistonFamilies.BASIC, new BasicPistonBlock(false)
    );
    public static final BasicPistonBlock BASIC_STICKY_PISTON = register(
            PistonFamilies.BASIC, new BasicPistonBlock(true)
    );

    // Strong Piston
    // Can push 24 blocks, although it takes a lot longer to push (0.05x slower)
    public static final BasicPistonHeadBlock STRONG_PISTON_HEAD = register(
            PistonFamilies.STRONG, new BasicPistonHeadBlock()
    );
    public static final SpeedPistonExtensionBlock STRONG_MOVING_PISTON = register(
            PistonFamilies.STRONG, new SpeedPistonExtensionBlock(0.05F)
    );
    public static final BasicPistonBlock STRONG_PISTON = register(
            PistonFamilies.STRONG, new PushLimitPistonBlock(false,24)
    );
    public static final BasicPistonBlock STRONG_STICKY_PISTON = register(
            PistonFamilies.STRONG, new PushLimitPistonBlock(true,24)
    );

    // Fast Piston
    // Can only push 2 block, although it's very fast
    public static final BasicPistonHeadBlock FAST_PISTON_HEAD = register(
            PistonFamilies.FAST, new BasicPistonHeadBlock()
    );
    public static final FastPistonExtensionBlock FAST_MOVING_PISTON = register(
            PistonFamilies.FAST, new FastPistonExtensionBlock()
    );
    public static final BasicPistonBlock FAST_PISTON = register(
            PistonFamilies.FAST, new PushLimitPistonBlock(false,2)
    );
    public static final BasicPistonBlock FAST_STICKY_PISTON = register(
            PistonFamilies.FAST, new PushLimitPistonBlock(true,2)
    );

    // Very Sticky Piston
    // It's face acts like a slime block, it can be pulled while extended.
    // Doing so will pull the moving piston with it as it retracts
    public static final BasicPistonHeadBlock STICKY_PISTON_HEAD = register(
            PistonFamilies.STICKY, new StickyPistonHeadBlock()
    );
    public static final StickyPistonExtensionBlock STICKY_MOVING_PISTON = register(
            PistonFamilies.STICKY, new StickyPistonExtensionBlock()
    );
    public static final BasicPistonBlock VERY_STICKY_PISTON = register(
            PistonFamilies.STICKY, new VeryStickyPistonBlock()
    );


    // Front Powered Piston
    // Normal piston but can be powered through the front
    public static final BasicPistonHeadBlock FRONT_POWERED_PISTON_HEAD = register(
            PistonFamilies.FRONT_POWERED, new BasicPistonHeadBlock()
    );
    public static final BasicPistonBlock FRONT_POWERED_PISTON = register(
            PistonFamilies.FRONT_POWERED, new FrontPoweredPistonBlock(false)
    );
    public static final BasicPistonBlock FRONT_POWERED_STICKY_PISTON = register(
            PistonFamilies.FRONT_POWERED, new FrontPoweredPistonBlock(true)
    );


    // Translocation Piston
    // Normal piston but has 1.10 translocation
    public static final BasicPistonHeadBlock TRANSLOCATION_PISTON_HEAD = register(
            PistonFamilies.TRANSLOCATION, new BasicPistonHeadBlock()
    );
    public static final TranslocationPistonExtensionBlock TRANSLOCATION_MOVING_PISTON = register(
            PistonFamilies.TRANSLOCATION, new TranslocationPistonExtensionBlock()
    );
    public static final BasicPistonBlock TRANSLOCATION_PISTON = register(
            PistonFamilies.TRANSLOCATION, new BasicPistonBlock(false)
    );
    public static final BasicPistonBlock TRANSLOCATION_STICKY_PISTON = register(
            PistonFamilies.TRANSLOCATION, new BasicPistonBlock(true)
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

    public static <T extends Block> T register(PistonFamily family, T block) {
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
            } else if (block instanceof BasicPistonArmBlock basicPistonArmBlock) {
                Registry.register(Registry.BLOCK, id(familyId+"_piston_arm"), basicPistonArmBlock);
                family.arm(basicPistonArmBlock); // Add checks once we actually add this to the game
            } else {
                throw new IllegalStateException(
                        "This block cannot be initialized as part of a piston family! - Block: " +
                                block + " - Family: " + family.getId()
                );
            }
        }
        return block;
    }
}
