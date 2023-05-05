package ca.fxco.pistonlib.base;

import java.util.Map;
import java.util.function.Function;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.blocks.*;
import ca.fxco.pistonlib.blocks.autoCraftingBlock.AutoCraftingBlock;
import ca.fxco.pistonlib.blocks.halfBlocks.HalfHoneyBlock;
import ca.fxco.pistonlib.blocks.halfBlocks.HalfObsidianBlock;
import ca.fxco.pistonlib.blocks.halfBlocks.HalfPoweredBlock;
import ca.fxco.pistonlib.blocks.halfBlocks.HalfRedstoneLampBlock;
import ca.fxco.pistonlib.blocks.halfBlocks.HalfSlimeBlock;
import ca.fxco.pistonlib.blocks.pistons.FrontPoweredPistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.StalePistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.VeryQuasiPistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonArmBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.pistonlib.blocks.pistons.configurablePiston.ConfigurableMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.configurablePiston.ConfigurablePistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.configurablePiston.ConfigurablePistonHeadBlock;
import ca.fxco.pistonlib.blocks.pistons.longPiston.LongPistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.longPiston.LongPistonHeadBlock;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlock;
import ca.fxco.pistonlib.blocks.pistons.movableBlockEntities.MBEMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.movableBlockEntities.MBEPistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.slipperyPiston.SlipperyMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.slipperyPiston.SlipperyPistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.slipperyPiston.SlipperyPistonHeadBlock;
import ca.fxco.pistonlib.blocks.pistons.veryStickyPiston.StickyPistonHeadBlock;
import ca.fxco.pistonlib.blocks.pistons.veryStickyPiston.VeryStickyPistonBaseBlock;
import ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock;
import ca.fxco.pistonlib.blocks.slipperyBlocks.SlipperyRedstoneBlock;
import ca.fxco.pistonlib.blocks.slipperyBlocks.SlipperySlimeBlock;
import ca.fxco.pistonlib.impl.toggle.ToggleableProperties;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.Material;

import static ca.fxco.pistonlib.base.ModPistonFamilies.*;
import static ca.fxco.pistonlib.PistonLib.id;

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
    public static final Block SLIMY_REDSTONE_BLOCK = register("slimy_redstone_block", new SlimyPoweredBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK).noOcclusion()));
    public static final Block ALL_SIDED_OBSERVER = register("all_sided_observer", AllSidedObserverBlock::new, Blocks.OBSERVER);
    public static final Block GLUE_BLOCK = register("glue_block", GlueBlock::new, Blocks.END_STONE);
    public static final Block POWERED_STICKY_BLOCK = register("powered_sticky_block", PoweredStickyBlock::new, Blocks.OAK_PLANKS);
    public static final Block STICKY_CHAIN_BLOCK = register("sticky_chain", StickyChainBlock::new, Blocks.CHAIN);
    public static final Block AXIS_LOCKED_BLOCK = register("axis_locked_block", AxisLockedBlock::new, Blocks.DEEPSLATE_BRICKS);
    public static final Block MOVE_COUNTING_BLOCK = register("move_counting_block", MoveCountingBlock::new, Blocks.SCULK);
    public static final Block WEAK_REDSTONE_BLOCK = register("weak_redstone_block", WeakPoweredBlock::new, Blocks.REDSTONE_BLOCK);
    public static final Block QUASI_BLOCK = register("quasi_block", QuasiBlock::new, Blocks.REDSTONE_BLOCK);
    public static final Block ERASE_BLOCK = register("erase_block", EraseBlock::new, Blocks.REDSTONE_BLOCK);
    public static final Block HEAVY_BLOCK = register("heavy_block", new WeightBlock(FabricBlockSettings.of(Material.METAL), 2));

    // Slippery Blocks
    // These blocks if they are not touching a solid surface
    public static final Block SLIPPERY_SLIME_BLOCK = register("slippery_slime_block", SlipperySlimeBlock::new, Blocks.SLIME_BLOCK);
    public static final Block SLIPPERY_REDSTONE_BLOCK = register("slippery_redstone_block", SlipperyRedstoneBlock::new, Blocks.REDSTONE_BLOCK);
    public static final Block SLIPPERY_STONE_BLOCK = register("slippery_stone_block", BaseSlipperyBlock::new, Blocks.STONE);

    // Obsidian Blocks
    public static final Block OBSIDIAN_SLAB_BLOCK = register("obsidian_slab_block", ObsidianSlabBlock::new, Blocks.OBSIDIAN);
    public static final Block OBSIDIAN_STAIR_BLOCK = register("obsidian_stair_block", new StairBlock(Blocks.OBSIDIAN.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OBSIDIAN)));

    // Piston Blocks should always be initialized in the following order:
    // Piston heads, Moving Pistons, base piston blocks, Piston Arms

    // Basic Piston
    // Acts exactly like a normal vanilla piston
    public static final BasicPistonBaseBlock BASIC_PISTON = register("basic_piston", new BasicPistonBaseBlock(BASIC, PistonType.DEFAULT));
    public static final BasicPistonBaseBlock BASIC_STICKY_PISTON = register("basic_sticky_piston", new BasicPistonBaseBlock(BASIC, PistonType.STICKY));
    public static final BasicPistonHeadBlock BASIC_PISTON_HEAD = register("basic_piston_head", new BasicPistonHeadBlock(BASIC));
    public static final BasicMovingBlock BASIC_MOVING_BLOCK = register("basic_moving_block", new BasicMovingBlock(BASIC));

    // Configurable Piston - Testing only
    // The one and only configurable piston. It can do mostly everything that the other pistons can do, allowing you
    // to very easily enable and disable features in your pistons
    public static final BasicPistonBaseBlock CONFIGURABLE_PISTON = register("configurable_piston", new ConfigurablePistonBaseBlock(CONFIGURABLE, PistonType.DEFAULT));
    public static final BasicPistonBaseBlock CONFIGURABLE_STICKY_PISTON = register("configurable_sticky_piston", new ConfigurablePistonBaseBlock(CONFIGURABLE, PistonType.STICKY));
    public static final BasicPistonArmBlock CONFIGURABLE_PISTON_ARM = register("configurable_piston_arm", new BasicPistonArmBlock(CONFIGURABLE));
    public static final BasicPistonHeadBlock CONFIGURABLE_PISTON_HEAD = register("configurable_piston_head", new ConfigurablePistonHeadBlock(CONFIGURABLE));
    public static final ConfigurableMovingBlock CONFIGURABLE_MOVING_BLOCK = register("configurable_moving_block", new ConfigurableMovingBlock(CONFIGURABLE));

    // Basic Long Piston
    // Can extend further than 1 block
    public static final LongPistonBaseBlock LONG_PISTON = register("long_piston", new LongPistonBaseBlock(LONG, PistonType.DEFAULT));
    public static final LongPistonBaseBlock LONG_STICKY_PISTON = register("long_sticky_piston", new LongPistonBaseBlock(LONG, PistonType.STICKY));
    public static final BasicPistonArmBlock LONG_PISTON_ARM = register("long_piston_arm", new BasicPistonArmBlock(LONG));
    public static final LongPistonHeadBlock LONG_PISTON_HEAD = register("long_piston_head", new LongPistonHeadBlock(LONG));
    public static final BasicMovingBlock LONG_MOVING_BLOCK = register("long_moving_block", new BasicMovingBlock(LONG));

    // Stale Piston
    // A vanilla piston except it cannot be quasi-powered
    public static final BasicPistonBaseBlock STALE_PISTON = register("stale_piston", new StalePistonBaseBlock(STALE, PistonType.DEFAULT));
    public static final BasicPistonBaseBlock STALE_STICKY_PISTON = register("stale_sticky_piston", new StalePistonBaseBlock(STALE, PistonType.STICKY));
    public static final BasicPistonHeadBlock STALE_PISTON_HEAD = register("stale_piston_head", new BasicPistonHeadBlock(STALE));
    public static final BasicMovingBlock STALE_MOVING_BLOCK = register("stale_moving_block", new BasicMovingBlock(STALE));

    // Very Quasi Piston
    // A vanilla piston except it can be quasi-powered from 5 blocks up
    public static final BasicPistonBaseBlock VERY_QUASI_PISTON = register("very_quasi_piston", new VeryQuasiPistonBaseBlock(VERY_QUASI, PistonType.DEFAULT, 5));
    public static final BasicPistonBaseBlock VERY_QUASI_STICKY_PISTON = register("very_quasi_sticky_piston", new VeryQuasiPistonBaseBlock(VERY_QUASI, PistonType.STICKY, 5));
    public static final BasicPistonHeadBlock VERY_QUASI_PISTON_HEAD = register("very_quasi_piston_head", new BasicPistonHeadBlock(VERY_QUASI));
    public static final BasicMovingBlock VERY_QUASI_MOVING_BLOCK = register("very_quasi_moving_block", new BasicMovingBlock(VERY_QUASI));

    // Strong Piston
    // Can push 24 blocks, although it takes a lot longer to push (0.05x slower)
    public static final BasicPistonBaseBlock STRONG_PISTON = register("strong_piston", new BasicPistonBaseBlock(STRONG, PistonType.DEFAULT));
    public static final BasicPistonBaseBlock STRONG_STICKY_PISTON = register("strong_sticky_piston", new BasicPistonBaseBlock(STRONG, PistonType.STICKY));
    public static final BasicPistonHeadBlock STRONG_PISTON_HEAD = register("strong_piston_head", new BasicPistonHeadBlock(STRONG));
    public static final BasicMovingBlock STRONG_MOVING_BLOCK = register("strong_moving_block", new BasicMovingBlock(STRONG));

    // Fast Piston
    // Can only push 2 block, although it's very fast
    public static final BasicPistonBaseBlock FAST_PISTON = register("fast_piston", new BasicPistonBaseBlock(FAST, PistonType.DEFAULT));
    public static final BasicPistonBaseBlock FAST_STICKY_PISTON = register("fast_sticky_piston", new BasicPistonBaseBlock(FAST, PistonType.STICKY));
    public static final BasicPistonHeadBlock FAST_PISTON_HEAD = register("fast_piston_head", new BasicPistonHeadBlock(FAST));
    public static final BasicMovingBlock FAST_MOVING_BLOCK = register("fast_moving_block", new BasicMovingBlock(FAST));

    // Very Sticky Piston
    // It's face acts like a slime block, it can be pulled while extended.
    // Doing so will pull the moving piston with it as it retracts
    public static final BasicPistonBaseBlock VERY_STICKY_PISTON = register("very_sticky_piston", new VeryStickyPistonBaseBlock(VERY_STICKY));
    public static final BasicPistonHeadBlock STICKY_PISTON_HEAD = register("very_sticky_piston_head", new StickyPistonHeadBlock(VERY_STICKY));
    public static final BasicMovingBlock STICKY_MOVING_BLOCK = register("very_sticky_moving_block", new BasicMovingBlock(VERY_STICKY));


    // Front Powered Piston
    // Normal piston but can be powered through the front
    public static final BasicPistonBaseBlock FRONT_POWERED_PISTON = register("front_powered_piston", new FrontPoweredPistonBaseBlock(FRONT_POWERED, PistonType.DEFAULT));
    public static final BasicPistonBaseBlock FRONT_POWERED_STICKY_PISTON = register("front_powered_sticky_piston", new FrontPoweredPistonBaseBlock(FRONT_POWERED, PistonType.STICKY));
    public static final BasicPistonHeadBlock FRONT_POWERED_PISTON_HEAD = register("front_powered_piston_head", new BasicPistonHeadBlock(FRONT_POWERED));
    public static final BasicMovingBlock FRONT_POWERED_MOVING_BLOCK = register("front_powered_moving_block", new BasicMovingBlock(FRONT_POWERED));


    // Slippery Piston
    // It's just a normal piston except its slippery
    public static final BasicPistonBaseBlock SLIPPERY_PISTON = register("slippery_piston", new SlipperyPistonBaseBlock(SLIPPERY, PistonType.DEFAULT));
    public static final BasicPistonBaseBlock SLIPPERY_STICKY_PISTON = register("slippery_sticky_piston", new SlipperyPistonBaseBlock(SLIPPERY, PistonType.STICKY));
    public static final BasicPistonHeadBlock SLIPPERY_PISTON_HEAD = register("slippery_piston_head", new SlipperyPistonHeadBlock(SLIPPERY));
    public static final SlipperyMovingBlock SLIPPERY_MOVING_BLOCK = register("slippery_moving_block", new SlipperyMovingBlock(SLIPPERY));

    // Super Piston
    // What's push limit? What is super sticky?
    public static final BasicPistonBaseBlock SUPER_PISTON = register("super_piston", new BasicPistonBaseBlock(SUPER, PistonType.DEFAULT));
    public static final BasicPistonBaseBlock SUPER_STICKY_PISTON = register("super_sticky_piston", new BasicPistonBaseBlock(SUPER, PistonType.STICKY));
    public static final BasicPistonHeadBlock SUPER_PISTON_HEAD = register("super_piston_head", new BasicPistonHeadBlock(SUPER));
    public static final BasicMovingBlock SUPER_MOVING_BLOCK = register("super_moving_block", new BasicMovingBlock(SUPER));

    // MBE Piston
    // A piston that can move block entities
    public static final MBEPistonBaseBlock MBE_PISTON = register("mbe_piston", new MBEPistonBaseBlock(MBE, PistonType.DEFAULT));
    public static final MBEPistonBaseBlock MBE_STICKY_PISTON = register("mbe_sticky_piston", new MBEPistonBaseBlock(MBE, PistonType.STICKY));
    public static final BasicPistonHeadBlock MBE_PISTON_HEAD_BLOCK = register("mbe_piston_head", new BasicPistonHeadBlock(MBE));
    public static final MBEMovingBlock MBE_MOVING_BLOCK = register("mbe_moving_block", new MBEMovingBlock(MBE));

    public static final MergeBlock MERGE_BLOCK = register("merge_block", MergeBlock::new, Blocks.MOVING_PISTON);

    public static final AutoCraftingBlock AUTO_CRAFTING_BLOCK = register("auto_crafting_block", new AutoCraftingBlock(((ToggleableProperties<Block.Properties>)FabricBlockSettings.copyOf(Blocks.CRAFTING_TABLE)).setDisabled(() -> !PistonLibConfig.autoCraftingBlock)));

    private static <T extends Block> T register(String name, Function<FabricBlockSettings, T> block, Block propertySource) {
        return register(name, block.apply(FabricBlockSettings.copyOf(propertySource)));
    }

    private static <T extends Block> T register(String name, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, id(name), block);
    }

    public static void bootstrap() { }

}
