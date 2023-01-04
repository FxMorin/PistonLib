package ca.fxco.configurablepistons.base;

import ca.fxco.configurablepistons.blocks.*;
import ca.fxco.configurablepistons.blocks.halfBlocks.*;
import ca.fxco.configurablepistons.blocks.pistons.FrontPoweredPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.PushLimitPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.fastPiston.FastPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonArmBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.slipperyPiston.SlipperyPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.slipperyPiston.SlipperyPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.slipperyPiston.SlipperyPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.speedPiston.SpeedPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.translocationPiston.TranslocationPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston.StickyPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston.StickyPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston.VeryStickyPistonBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.SlipperyRedstoneBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.SlipperySlimeBlock;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

import static ca.fxco.configurablepistons.ConfigurablePistons.CUSTOM_CREATIVE_GROUP;
import static ca.fxco.configurablepistons.ConfigurablePistons.id;
import static ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies.*;

public class ModBlocks {

    // Half Blocks
    public static final Block HALF_SLIME_BLOCK = registerBlock("half_slime", HalfSlimeBlock::new,Blocks.SLIME_BLOCK);
    public static final Block HALF_HONEY_BLOCK = registerBlock("half_honey", HalfHoneyBlock::new, Blocks.HONEY_BLOCK);
    public static final Block HALF_REDSTONE_BLOCK = registerBlock("half_redstone", HalfRedstoneBlock::new, Blocks.REDSTONE_BLOCK);
    public static final Block HALF_OBSIDIAN_BLOCK = registerBlock("half_obsidian", HalfObsidianBlock::new, Blocks.OBSIDIAN);
    public static final Block HALF_REDSTONE_LAMP_BLOCK = registerBlock("half_redstone_lamp", HalfRedstoneLampBlock::new, Blocks.REDSTONE_LAMP);

    // Create Custom Blocks
    public static final Block DRAG_BLOCK = registerBlock("drag_block", new PullOnlyBlock(FabricBlockSettings.of(Material.METAL).strength(22.0f).hardness(18.0f)));
    public static final Block STICKYLESS_BLOCK = registerBlock("stickyless_block", new StickylessBlock(FabricBlockSettings.of(Material.AMETHYST).strength(64.0f).hardness(64.0f)));
    public static final Block STICKY_TOP_BLOCK = registerBlock("sticky_top_block", new StickySidesBlock(FabricBlockSettings.copyOf(Blocks.STONE), Map.of(Direction.UP, StickyType.STICKY)));
    public static final Block SLIMY_REDSTONE_BLOCK = registerBlock("slimy_redstone_block", SlimyRedstoneBlock::new, Blocks.REDSTONE_BLOCK);
    public static final Block ALL_SIDED_OBSERVER = registerBlock("all_sided_observer", AllSidedObserverBlock::new, Blocks.OBSERVER);
    public static final Block GLUE_BLOCK = registerBlock("glue_block", GlueBlock::new, Blocks.END_STONE);
    public static final Block POWERED_STICKY_BLOCK = registerBlock("powered_sticky_block", PoweredStickyBlock::new, Blocks.OAK_PLANKS);
    public static final Block STICKY_CHAIN_BLOCK = registerBlock("sticky_chain", StickyChainBlock::new, Blocks.CHAIN);

    // Slippery Blocks
    // These blocks if they are not touching a solid surface
    public static final Block SLIPPERY_SLIME_BLOCK = registerBlock("slippery_slime_block", SlipperySlimeBlock::new, Blocks.SLIME_BLOCK);
    public static final Block SLIPPERY_REDSTONE_BLOCK = registerBlock("slippery_redstone_block", SlipperyRedstoneBlock::new, Blocks.REDSTONE_BLOCK);
    public static final Block SLIPPERY_STONE_BLOCK = registerBlock("slippery_stone_block", BaseSlipperyBlock::new, Blocks.STONE);

    // Piston Blocks should always be initialized in the following order:
    // Piston heads, Moving Pistons, base piston blocks, Piston Arms

    // Basic Piston
    // Acts exactly like a normal vanilla piston
    public static final BasicPistonHeadBlock BASIC_PISTON_HEAD = registerPiston(BASIC, new BasicPistonHeadBlock());
    public static final BasicPistonExtensionBlock BASIC_MOVING_PISTON = registerPiston(BASIC, new BasicPistonExtensionBlock());
    public static final BasicPistonBlock BASIC_PISTON = registerPiston(BASIC, new BasicPistonBlock(false));
    public static final BasicPistonBlock BASIC_STICKY_PISTON = registerPiston(BASIC, new BasicPistonBlock(true));

    // Basic Long Piston
    // Can extend further than 1 block
    public static final LongPistonHeadBlock LONG_PISTON_HEAD = registerPiston(LONG, new LongPistonHeadBlock());
    public static final LongPistonExtensionBlock LONG_MOVING_PISTON = registerPiston(LONG, new LongPistonExtensionBlock());
    public static final LongPistonBlock LONG_PISTON = registerPiston(LONG, new LongPistonBlock(false), null);
    public static final BasicPistonBlock LONG_STICKY_PISTON = registerPiston(LONG, new LongPistonBlock(true), null);
    public static final LongPistonArmBlock LONG_PISTON_ARM = registerPiston(LONG, new LongPistonArmBlock());

    // Strong Piston
    // Can push 24 blocks, although it takes a lot longer to push (0.05x slower)
    public static final BasicPistonHeadBlock STRONG_PISTON_HEAD = registerPiston(STRONG, new BasicPistonHeadBlock());
    public static final SpeedPistonExtensionBlock STRONG_MOVING_PISTON = registerPiston(STRONG, new SpeedPistonExtensionBlock(0.05F));
    public static final BasicPistonBlock STRONG_PISTON = registerPiston(STRONG, new PushLimitPistonBlock(false,24));
    public static final BasicPistonBlock STRONG_STICKY_PISTON = registerPiston(STRONG, new PushLimitPistonBlock(true,24));

    // Fast Piston
    // Can only push 2 block, although it's very fast
    public static final BasicPistonHeadBlock FAST_PISTON_HEAD = registerPiston(FAST, new BasicPistonHeadBlock());
    public static final FastPistonExtensionBlock FAST_MOVING_PISTON = registerPiston(FAST, new FastPistonExtensionBlock());
    public static final BasicPistonBlock FAST_PISTON = registerPiston(FAST, new PushLimitPistonBlock(false,8));
    public static final BasicPistonBlock FAST_STICKY_PISTON = registerPiston(FAST, new PushLimitPistonBlock(true,8));

    // Very Sticky Piston
    // It's face acts like a slime block, it can be pulled while extended.
    // Doing so will pull the moving piston with it as it retracts
    public static final BasicPistonHeadBlock STICKY_PISTON_HEAD = registerPiston(STICKY, new StickyPistonHeadBlock());
    public static final StickyPistonExtensionBlock STICKY_MOVING_PISTON = registerPiston(STICKY, new StickyPistonExtensionBlock());
    public static final BasicPistonBlock VERY_STICKY_PISTON = registerPiston(STICKY, new VeryStickyPistonBlock());


    // Front Powered Piston
    // Normal piston but can be powered through the front
    public static final BasicPistonHeadBlock FRONT_POWERED_PISTON_HEAD = registerPiston(FRONT_POWERED, new BasicPistonHeadBlock());
    public static final BasicPistonBlock FRONT_POWERED_PISTON = registerPiston(FRONT_POWERED, new FrontPoweredPistonBlock(false));
    public static final BasicPistonBlock FRONT_POWERED_STICKY_PISTON = registerPiston(FRONT_POWERED, new FrontPoweredPistonBlock(true));


    // Translocation Piston
    // Normal piston but has 1.10 translocation
    public static final BasicPistonHeadBlock TRANSLOCATION_PISTON_HEAD = registerPiston(TRANSLOCATION, new BasicPistonHeadBlock());
    public static final TranslocationPistonExtensionBlock TRANSLOCATION_MOVING_PISTON = registerPiston(TRANSLOCATION, new TranslocationPistonExtensionBlock());
    public static final BasicPistonBlock TRANSLOCATION_PISTON = registerPiston(TRANSLOCATION, new BasicPistonBlock(false));
    public static final BasicPistonBlock TRANSLOCATION_STICKY_PISTON = registerPiston(TRANSLOCATION, new BasicPistonBlock(true));

    // Slippery Piston
    // It's just a normal piston except its slippery
    public static final BasicPistonHeadBlock SLIPPERY_PISTON_HEAD = registerPiston(SLIPPERY, new SlipperyPistonHeadBlock());
    public static final SlipperyPistonExtensionBlock SLIPPERY_MOVING_PISTON = registerPiston(SLIPPERY, new SlipperyPistonExtensionBlock());
    public static final BasicPistonBlock SLIPPERY_PISTON = registerPiston(SLIPPERY, new SlipperyPistonBlock(false));
    public static final BasicPistonBlock SLIPPERY_STICKY_PISTON = registerPiston(SLIPPERY, new SlipperyPistonBlock(true));

    // Super Piston
    // What's push limit? What is super sticky?
    public static final BasicPistonHeadBlock SUPER_PISTON_HEAD = registerPiston(SUPER, new BasicPistonHeadBlock());
    public static final BasicPistonBlock SUPER_PISTON = registerPiston(SUPER, new PushLimitPistonBlock(false, Integer.MAX_VALUE));
    public static final BasicPistonBlock SUPER_STICKY_PISTON = registerPiston(SUPER, new PushLimitPistonBlock(true, Integer.MAX_VALUE));

    //
    // Registration methods
    //

    static <T extends Block> T registerBlock(String blockId, Function<FabricBlockSettings, T> blockSupplier, Block copySettingsFrom) {
        return registerBlock(blockId, blockSupplier.apply(FabricBlockSettings.copyOf(copySettingsFrom)));
    }

    public static <T extends Block> T registerBlock(String blockId, T block) {
        Identifier identifier = id(blockId);
        Registry.register(Registries.ITEM, identifier, new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, identifier, block);
    }

    static <T extends Block> T registerPiston(PistonFamily family, T block) {
        return registerPiston(family, block, CUSTOM_CREATIVE_GROUP);
    }

    public static <T extends Block> T registerPiston(PistonFamily family, T block, @Nullable ItemGroup creativeGroup) {
        if (family == null)
            throw new IllegalStateException("Valid Piston Family must be used! - " + Registries.BLOCK.getId(block));
        String familyId = family.getId();
        if (family.mustSetupHead()) { // FIRST BLOCK INITIALIZED SHOULD ALWAYS BE THE HEAD!!!
            if (block instanceof BasicPistonHeadBlock headBlock) {
                Registry.register(Registries.BLOCK, id(familyId + "_piston_head"), block);
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
                    identifier = id(familyId + "_sticky_piston");
                    Registry.register(Registries.BLOCK, identifier, pistonBlock);
                    family.sticky(pistonBlock);
                } else {
                    identifier = id(familyId + "_piston");
                    Registry.register(Registries.BLOCK, identifier, pistonBlock);
                    family.piston(pistonBlock);
                }
                if (creativeGroup != null) {
                    Registry.register(Registries.ITEM, identifier, new BlockItem(pistonBlock, new Item.Settings()));
                }
            } else if (block instanceof BasicPistonExtensionBlock basicPistonExtensionBlock) {
                Registry.register(Registries.BLOCK, id(familyId + "_moving_piston"), basicPistonExtensionBlock);
                if (family.getPistonBlock() != null || family.getStickyPistonBlock() != null) {
                    throw new IllegalStateException(
                            "Extension blocks must always be initialized before base piston blocks! - Block: " +
                                    Registries.BLOCK.getId(basicPistonExtensionBlock) + " - Family: " + family.getId()
                    );
                }
                family.extension(basicPistonExtensionBlock);
            } else if (block instanceof LongPistonArmBlock longPistonArmBlock) {
                Registry.register(Registries.BLOCK, id(familyId + "_piston_arm"), longPistonArmBlock);
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
