package ca.fxco.configurablepistons.base;

import java.util.Map;
import java.util.function.Function;

import ca.fxco.configurablepistons.blocks.*;
import ca.fxco.configurablepistons.blocks.pistons.configurablePiston.ConfigurableMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.configurablePiston.ConfigurablePistonBaseBlock;
import ca.fxco.configurablepistons.blocks.pistons.configurablePiston.ConfigurablePistonHeadBlock;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import ca.fxco.configurablepistons.blocks.halfBlocks.HalfHoneyBlock;
import ca.fxco.configurablepistons.blocks.halfBlocks.HalfObsidianBlock;
import ca.fxco.configurablepistons.blocks.halfBlocks.HalfPoweredBlock;
import ca.fxco.configurablepistons.blocks.halfBlocks.HalfRedstoneLampBlock;
import ca.fxco.configurablepistons.blocks.halfBlocks.HalfSlimeBlock;
import ca.fxco.configurablepistons.blocks.pistons.FrontPoweredPistonBaseBlock;
import ca.fxco.configurablepistons.blocks.pistons.PushLimitPistonBaseBlock;
import ca.fxco.configurablepistons.blocks.pistons.StalePistonBaseBlock;
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

import static ca.fxco.configurablepistons.ConfigurablePistons.CUSTOM_CREATIVE_MODE_TAB;
import static ca.fxco.configurablepistons.ConfigurablePistons.id;
import static ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies.*;

public class ModBlocks {

    // Half Blocks
    public static final Block HALF_SLIME_BLOCK = register("half_slime", HalfSlimeBlock::new, Blocks.SLIME_BLOCK);
    public static final Block HALF_HONEY_BLOCK = register("half_honey", HalfHoneyBlock::new, Blocks.HONEY_BLOCK);
    public static final Block HALF_REDSTONE_BLOCK = register("half_redstone", HalfPoweredBlock::new, Blocks.REDSTONE_BLOCK);
    public static final Block HALF_OBSIDIAN_BLOCK = register("half_obsidian", HalfObsidianBlock::new, Blocks.OBSIDIAN);
    public static final Block HALF_REDSTONE_LAMP_BLOCK = register("half_redstone_lamp", HalfRedstoneLampBlock::new, Blocks.REDSTONE_LAMP);

    // Create Custom Blocks
    public static final Block DRAG_BLOCK = register("drag_block", new PullOnlyBlock(FabricBlockSettings.of(Material.METAL).strength(22.0f).hardness(18.0f)));
    public static final Block STICKYLESS_BLOCK = register("stickyless_block", new StickylessBlock(FabricBlockSettings.of(Material.AMETHYST).strength(64.0f).hardness(64.0f)));
    public static final Block STICKY_TOP_BLOCK = register("sticky_top_block", new StickySidesBlock(FabricBlockSettings.copyOf(Blocks.STONE), Map.of(Direction.UP, StickyType.STICKY)));
    public static final Block SLIMY_REDSTONE_BLOCK = register("slimy_redstone_block", SlimyPoweredBlock::new, Blocks.REDSTONE_BLOCK);
    public static final Block ALL_SIDED_OBSERVER = register("all_sided_observer", AllSidedObserverBlock::new, Blocks.OBSERVER);
    public static final Block GLUE_BLOCK = register("glue_block", GlueBlock::new, Blocks.END_STONE);
    public static final Block POWERED_STICKY_BLOCK = register("powered_sticky_block", PoweredStickyBlock::new, Blocks.OAK_PLANKS);
    public static final Block STICKY_CHAIN_BLOCK = register("sticky_chain", StickyChainBlock::new, Blocks.CHAIN);
    public static final Block AXIS_LOCKED_BLOCK = register("axis_locked_block", AxisLockedBlock::new, Blocks.DEEPSLATE_BRICKS);
    public static final Block MOVE_COUNTING_BLOCK = register("move_counting_block", MoveCountingBlock::new, Blocks.SCULK);

    // Slippery Blocks
    // These blocks if they are not touching a solid surface
    public static final Block SLIPPERY_SLIME_BLOCK = register("slippery_slime_block", SlipperySlimeBlock::new, Blocks.SLIME_BLOCK);
    public static final Block SLIPPERY_REDSTONE_BLOCK = register("slippery_redstone_block", SlipperyRedstoneBlock::new, Blocks.REDSTONE_BLOCK);
    public static final Block SLIPPERY_STONE_BLOCK = register("slippery_stone_block", BaseSlipperyBlock::new, Blocks.STONE);

    // Piston Blocks should always be initialized in the following order:
    // Piston heads, Moving Pistons, base piston blocks, Piston Arms

    // Basic Piston
    // Acts exactly like a normal vanilla piston
    public static final BasicPistonHeadBlock BASIC_PISTON_HEAD = registerPiston(BASIC, new BasicPistonHeadBlock());
    public static final BasicMovingBlock BASIC_MOVING_BLOCK = registerPiston(BASIC, new BasicMovingBlock());
    public static final BasicPistonBaseBlock BASIC_PISTON = registerPiston(BASIC, new BasicPistonBaseBlock(PistonType.DEFAULT));
    public static final BasicPistonBaseBlock BASIC_STICKY_PISTON = registerPiston(BASIC, new BasicPistonBaseBlock(PistonType.STICKY));

    // Configurable Piston - Testing only
    // The one and only configurable piston. It can do mostly everything that the other pistons can do, allowing you
    // to very easily enable and disable features in your pistons
    public static final BasicPistonHeadBlock CONFIGURABLE_PISTON_HEAD;
    public static final ConfigurableMovingBlock CONFIGURABLE_MOVING_BLOCK;
    public static final BasicPistonBaseBlock CONFIGURABLE_PISTON;
    public static final BasicPistonBaseBlock CONFIGURABLE_STICKY_PISTON;

    // Basic Long Piston
    // Can extend further than 1 block
    public static final LongPistonHeadBlock LONG_PISTON_HEAD = registerPiston(LONG, new LongPistonHeadBlock());
    public static final LongMovingBlock LONG_MOVING_BLOCK = registerPiston(LONG, new LongMovingBlock());
    public static final LongPistonBaseBlock LONG_PISTON = registerPiston(LONG, new LongPistonBaseBlock(PistonType.DEFAULT), null);
    public static final LongPistonBaseBlock LONG_STICKY_PISTON = registerPiston(LONG, new LongPistonBaseBlock(PistonType.STICKY), null);
    public static final LongPistonArmBlock LONG_PISTON_ARM = registerPiston(LONG, new LongPistonArmBlock());

    // Stale Piston
    // A vanilla piston except it cannot be quasi-powered
    public static final BasicPistonHeadBlock STALE_PISTON_HEAD = registerPiston(STALE, new BasicPistonHeadBlock());
    public static final BasicPistonBaseBlock STALE_PISTON = registerPiston(STALE, new StalePistonBaseBlock(PistonType.DEFAULT));
    public static final BasicPistonBaseBlock STALE_STICKY_PISTON = registerPiston(STALE, new StalePistonBaseBlock(PistonType.STICKY));

    // Strong Piston
    // Can push 24 blocks, although it takes a lot longer to push (0.05x slower)
    public static final BasicPistonHeadBlock STRONG_PISTON_HEAD = registerPiston(STRONG, new BasicPistonHeadBlock());
    public static final SpeedMovingBlock STRONG_MOVING_BLOCK = registerPiston(STRONG, new SpeedMovingBlock(0.05F));
    public static final BasicPistonBaseBlock STRONG_PISTON = registerPiston(STRONG, new PushLimitPistonBaseBlock(PistonType.DEFAULT, 24));
    public static final BasicPistonBaseBlock STRONG_STICKY_PISTON = registerPiston(STRONG, new PushLimitPistonBaseBlock(PistonType.STICKY, 24));

    // Fast Piston
    // Can only push 2 block, although it's very fast
    public static final BasicPistonHeadBlock FAST_PISTON_HEAD = registerPiston(FAST, new BasicPistonHeadBlock());
    public static final FastMovingBlock FAST_MOVING_BLOCK = registerPiston(FAST, new FastMovingBlock());
    public static final BasicPistonBaseBlock FAST_PISTON = registerPiston(FAST, new PushLimitPistonBaseBlock(PistonType.DEFAULT, 8));
    public static final BasicPistonBaseBlock FAST_STICKY_PISTON = registerPiston(FAST, new PushLimitPistonBaseBlock(PistonType.STICKY, 8));

    // Very Sticky Piston
    // It's face acts like a slime block, it can be pulled while extended.
    // Doing so will pull the moving piston with it as it retracts
    public static final BasicPistonHeadBlock STICKY_PISTON_HEAD = registerPiston(STICKY, new StickyPistonHeadBlock());
    public static final StickyMovingBlock STICKY_MOVING_BLOCK = registerPiston(STICKY, new StickyMovingBlock());
    public static final BasicPistonBaseBlock VERY_STICKY_PISTON = registerPiston(STICKY, new VeryStickyPistonBaseBlock());


    // Front Powered Piston
    // Normal piston but can be powered through the front
    public static final BasicPistonHeadBlock FRONT_POWERED_PISTON_HEAD = registerPiston(FRONT_POWERED, new BasicPistonHeadBlock());
    public static final BasicPistonBaseBlock FRONT_POWERED_PISTON = registerPiston(FRONT_POWERED, new FrontPoweredPistonBaseBlock(PistonType.DEFAULT));
    public static final BasicPistonBaseBlock FRONT_POWERED_STICKY_PISTON = registerPiston(FRONT_POWERED, new FrontPoweredPistonBaseBlock(PistonType.STICKY));


    // Translocation Piston
    // Normal piston but has 1.10 translocation
    public static final BasicPistonHeadBlock TRANSLOCATION_PISTON_HEAD = registerPiston(TRANSLOCATION, new BasicPistonHeadBlock());
    public static final TranslocationMovingBlock TRANSLOCATION_MOVING_BLOCK = registerPiston(TRANSLOCATION, new TranslocationMovingBlock());
    public static final BasicPistonBaseBlock TRANSLOCATION_PISTON = registerPiston(TRANSLOCATION, new BasicPistonBaseBlock(PistonType.DEFAULT));
    public static final BasicPistonBaseBlock TRANSLOCATION_STICKY_PISTON = registerPiston(TRANSLOCATION, new BasicPistonBaseBlock(PistonType.STICKY));

    // Slippery Piston
    // It's just a normal piston except its slippery
    public static final BasicPistonHeadBlock SLIPPERY_PISTON_HEAD = registerPiston(SLIPPERY, new SlipperyPistonHeadBlock());
    public static final SlipperyMovingBlock SLIPPERY_MOVING_BLOCK = registerPiston(SLIPPERY, new SlipperyMovingBlock());
    public static final BasicPistonBaseBlock SLIPPERY_PISTON = registerPiston(SLIPPERY, new SlipperyPistonBaseBlock(PistonType.DEFAULT));
    public static final BasicPistonBaseBlock SLIPPERY_STICKY_PISTON = registerPiston(SLIPPERY, new SlipperyPistonBaseBlock(PistonType.STICKY));

    // Super Piston
    // What's push limit? What is super sticky?
    public static final BasicPistonHeadBlock SUPER_PISTON_HEAD = registerPiston(SUPER, new BasicPistonHeadBlock());
    public static final BasicPistonBaseBlock SUPER_PISTON = registerPiston(SUPER, new PushLimitPistonBaseBlock(PistonType.DEFAULT, Integer.MAX_VALUE));
    public static final BasicPistonBaseBlock SUPER_STICKY_PISTON = registerPiston(SUPER, new PushLimitPistonBaseBlock(PistonType.STICKY, Integer.MAX_VALUE));

    //
    // Registration methods
    //

    static <T extends Block> T register(String blockId, Function<FabricBlockSettings, T> block, Block propertySource) {
        return register(blockId, block.apply(FabricBlockSettings.copyOf(propertySource)));
    }

    public static <T extends Block> T register(String name, T block) {
        ResourceLocation id = id(name);
        Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties()));
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    static <T extends Block> T registerPiston(PistonFamily family, T block) {
        return registerPiston(family, block, CUSTOM_CREATIVE_MODE_TAB);
    }

    public static <T extends Block> T registerPiston(PistonFamily family, T block, @Nullable CreativeModeTab creativeModeTab) {
        if (family == null) {
            throw new IllegalStateException("Valid Piston Family must be used! - " + BuiltInRegistries.BLOCK.getId(block));
        }

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
            if (block instanceof BasicPistonBaseBlock baseBlock) {
                BasicMovingBlock movingBlock = family.getMovingBlock();
                baseBlock.setMovingBlock(movingBlock != null ? movingBlock : BASIC_MOVING_BLOCK);
                BasicPistonHeadBlock headBlock = family.getHeadBlock();
                baseBlock.setHeadBlock(headBlock != null ? headBlock : ModBlocks.BASIC_PISTON_HEAD);
                ResourceLocation id = switch (baseBlock.type) {
                    case DEFAULT -> id(familyId + "_piston");
                    case STICKY -> id(familyId + "_sticky_piston");
                    default -> throw new IllegalStateException("unknown base type " + baseBlock.type);
                };
                Registry.register(BuiltInRegistries.BLOCK, id, baseBlock);
                family.base(baseBlock);
                if (creativeModeTab != null) {
                    Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(baseBlock, new Item.Properties()));
                }
            } else if (block instanceof BasicMovingBlock movingBlock) {
                Registry.register(BuiltInRegistries.BLOCK, id(familyId + "_moving_piston"), movingBlock);
                if (family.getBaseBlock() != null) {
                    throw new IllegalStateException(
                        "Moving blocks must always be initialized before base blocks! - Block: " +
                            BuiltInRegistries.BLOCK.getId(movingBlock) + " - Family: " + family.getId()
                    );
                }
                family.moving(movingBlock);
            } else if (block instanceof LongPistonArmBlock armBlock) {
                Registry.register(BuiltInRegistries.BLOCK, id(familyId + "_piston_arm"), armBlock);
                BasicPistonHeadBlock headBlock = family.getHeadBlock();
                if (headBlock instanceof LongPistonHeadBlock longHeadBlock) {
                    armBlock.setHeadBlock(longHeadBlock);
                } else {
                    throw new IllegalStateException(
                        "Pistons using the LongPistonArmBlock, must also use a LongPistonHeadBlock"
                    );
                }
                family.arm(armBlock);
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

    static {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            ConfigurablePistonBaseBlock.Settings settings = new ConfigurablePistonBaseBlock.Settings()
                    .canExtendOnRetracting(true)
                    .canRetractOnExtending(true)
                    .speed(0.05F);
            CONFIGURABLE_PISTON_HEAD = registerPiston(CONFIGURABLE, new ConfigurablePistonHeadBlock(settings));
            CONFIGURABLE_MOVING_BLOCK = registerPiston(CONFIGURABLE, new ConfigurableMovingBlock(settings));
            CONFIGURABLE_PISTON = registerPiston(CONFIGURABLE, new ConfigurablePistonBaseBlock(PistonType.DEFAULT, settings));
            CONFIGURABLE_STICKY_PISTON = registerPiston(CONFIGURABLE, new ConfigurablePistonBaseBlock(PistonType.STICKY, settings));
        } else {
            CONFIGURABLE_PISTON_HEAD = null;
            CONFIGURABLE_MOVING_BLOCK = null;
            CONFIGURABLE_PISTON = null;
            CONFIGURABLE_STICKY_PISTON = null;
        }
    }
}
