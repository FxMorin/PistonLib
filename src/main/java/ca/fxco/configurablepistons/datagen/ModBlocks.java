package ca.fxco.configurablepistons.datagen;

import ca.fxco.configurablepistons.base.BasicPistonArmBlock;
import ca.fxco.configurablepistons.base.BasicPistonBlock;
import ca.fxco.configurablepistons.base.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.base.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.families.PistonFamilies;
import ca.fxco.configurablepistons.families.PistonFamily;
import ca.fxco.configurablepistons.helpers.StickyType;
import ca.fxco.configurablepistons.newBlocks.*;
import ca.fxco.configurablepistons.newBlocks.fastPiston.FastPistonExtensionBlock;
import ca.fxco.configurablepistons.newBlocks.speedPiston.SpeedPistonExtensionBlock;
import ca.fxco.configurablepistons.newBlocks.veryStickyPiston.StickyPistonExtensionBlock;
import ca.fxco.configurablepistons.newBlocks.veryStickyPiston.VeryStickyPistonBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static ca.fxco.configurablepistons.ConfigurablePistons.CUSTOM_CREATIVE_GROUP;
import static ca.fxco.configurablepistons.ConfigurablePistons.id;

public class ModBlocks {

    private static PistonFamily.Builder currentBuilder = null;

    // Create Custom Blocks
    public static final Block DRAG_BLOCK = registerBlock("drag_block", new PullOnlyBlock(FabricBlockSettings.of(Material.METAL).strength(22.0f).hardness(18.0f)));
    public static final Block STICKYLESS_BLOCK = registerBlock("stickyless_block",new StickylessBlock(FabricBlockSettings.of(Material.AMETHYST).strength(64.0f).hardness(64.0f)));
    public static final Block STICKY_TOP_BLOCK = registerBlock("sticky_top_block",new StickySidesBlock(FabricBlockSettings.copyOf(Blocks.STONE), Map.of(Direction.UP, StickyType.STICKY)));

    // Piston Blocks should always be initialized in the following order:
    // Piston heads, Moving Pistons, base piston blocks

    // Basic Piston
    // Acts exactly like a normal vanilla piston
    public static final BasicPistonHeadBlock BASIC_PISTON_HEAD = register("basic_piston_head", PistonFamilies.BASIC, new BasicPistonHeadBlock());
    public static final BasicPistonExtensionBlock BASIC_MOVING_PISTON = register("basic_moving_piston", PistonFamilies.BASIC, new BasicPistonExtensionBlock());
    public static final BasicPistonBlock BASIC_PISTON = register("basic_piston", PistonFamilies.BASIC, new BasicPistonBlock(false));
    public static final BasicPistonBlock BASIC_STICKY_PISTON = register("basic_sticky_piston", PistonFamilies.BASIC, new BasicPistonBlock(true), true);

    // Strong Piston
    // Can push 24 blocks, although it takes a lot longer to push (0.05x slower)
    public static final BasicPistonHeadBlock STRONG_PISTON_HEAD = register("strong_piston_head", PistonFamilies.STRONG, new BasicPistonHeadBlock());
    public static final SpeedPistonExtensionBlock STRONG_MOVING_PISTON = register("strong_moving_piston", PistonFamilies.STRONG, new SpeedPistonExtensionBlock(0.05F));
    public static final BasicPistonBlock STRONG_PISTON = register("strong_piston", PistonFamilies.STRONG, new PushLimitPistonBlock(false,24));
    public static final BasicPistonBlock STRONG_STICKY_PISTON = register("strong_sticky_piston", PistonFamilies.STRONG, new PushLimitPistonBlock(true,24), true);

    // Fast Piston
    // Can only push 2 block, although it's very fast
    public static final BasicPistonHeadBlock FAST_PISTON_HEAD = register("fast_piston_head", PistonFamilies.FAST, new BasicPistonHeadBlock());
    public static final FastPistonExtensionBlock FAST_MOVING_PISTON = register("fast_moving_piston", PistonFamilies.FAST, new FastPistonExtensionBlock());
    public static final BasicPistonBlock FAST_PISTON = register("fast_piston", PistonFamilies.FAST, new PushLimitPistonBlock(false,2));
    public static final BasicPistonBlock FAST_STICKY_PISTON = register("fast_sticky_piston", PistonFamilies.FAST, new PushLimitPistonBlock(true,2), true);

    // Very Sticky Piston
    // It's face acts like a slime block, it can be pulled while extended.
    // Doing so will pull the moving piston with it as it retracts
    public static final BasicPistonHeadBlock STICKY_PISTON_HEAD = register("sticky_piston_head", PistonFamilies.STICKY, new BasicPistonHeadBlock());
    public static final StickyPistonExtensionBlock STICKY_MOVING_PISTON = register("sticky_moving_piston", PistonFamilies.STICKY, new StickyPistonExtensionBlock());
    public static final BasicPistonBlock VERY_STICKY_PISTON = register("very_sticky_piston", PistonFamilies.STICKY, new VeryStickyPistonBlock(), true);


    // Front Powered Piston
    // Normal piston but can be powered through the front
    public static final BasicPistonHeadBlock FRONT_POWERED_PISTON_HEAD = register("front_powered_piston_head", PistonFamilies.FRONT_POWERED, new BasicPistonHeadBlock());
    public static final BasicPistonBlock FRONT_POWERED_PISTON = register("front_powered_piston", PistonFamilies.FRONT_POWERED, new FrontPoweredPistonBlock(false));
    public static final BasicPistonBlock FRONT_POWERED_STICKY_PISTON = register("front_powered_sticky_piston", PistonFamilies.FRONT_POWERED, new FrontPoweredPistonBlock(true), true);


    public static <T extends Block> T registerBlock(String blockId, T block) {
        Identifier identifier = id(blockId);
        Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(CUSTOM_CREATIVE_GROUP)));
        return Registry.register(Registry.BLOCK, identifier, block);
    }

    public static <T extends Block> T register(String blockId, @Nullable PistonFamily family, T block) {
        return register(blockId, family, block, false);
    }

    public static <T extends Block> T register(String blockId, @Nullable PistonFamily family, T block, boolean last) {
        Identifier identifier = id(blockId);
        Registry.register(Registry.BLOCK, identifier, block); // Add block to registry
        if (family == null && currentBuilder == null) { // FIRST BLOCK INITIALIZED SHOULD ALWAYS BE THE HEAD!!!
            if (block instanceof BasicPistonHeadBlock pistonHeadBlock) {
                currentBuilder = PistonFamilies.register(pistonHeadBlock);
            } else {
                throw new IllegalStateException("First Piston Block should be a basic piston head! - " + Registry.BLOCK.getId(block));
            }
        } else {
            if (block instanceof BasicPistonBlock basicPistonBlock) {
                Registry.register(Registry.ITEM, identifier, new BlockItem(basicPistonBlock, new Item.Settings().group(CUSTOM_CREATIVE_GROUP)));
                PistonFamily pistonFamily = currentBuilder.getFamily();
                BasicPistonExtensionBlock extensionBlock = pistonFamily.getExtensionBlock();
                basicPistonBlock.setExtensionBlock(extensionBlock != null ? extensionBlock : ModBlocks.BASIC_MOVING_PISTON);
                BasicPistonHeadBlock headBlock = pistonFamily.getHeadBlock();
                basicPistonBlock.setHeadBlock(headBlock != null ? headBlock : ModBlocks.BASIC_PISTON_HEAD);
                if (basicPistonBlock.sticky) {
                    currentBuilder.sticky(basicPistonBlock);
                } else {
                    currentBuilder.piston(basicPistonBlock);
                }
            } else if (block instanceof BasicPistonExtensionBlock basicPistonExtensionBlock) {
                currentBuilder.extension(basicPistonExtensionBlock);
            } else if (block instanceof BasicPistonArmBlock basicPistonArmBlock) {
                currentBuilder.arm(basicPistonArmBlock);
            }
        }
        if (last) currentBuilder = null;
        return block;
    }
}
