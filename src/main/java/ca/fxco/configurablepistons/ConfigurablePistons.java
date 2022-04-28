package ca.fxco.configurablepistons;

import ca.fxco.configurablepistons.base.BasicPistonBlock;
import ca.fxco.configurablepistons.base.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.base.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.base.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.newBlocks.PullOnlyBlock;
import ca.fxco.configurablepistons.newBlocks.PushLimitPistonBlock;
import ca.fxco.configurablepistons.newBlocks.fastPiston.FastPistonBlockEntity;
import ca.fxco.configurablepistons.newBlocks.fastPiston.FastPistonExtensionBlock;
import ca.fxco.configurablepistons.newBlocks.speedPiston.SpeedPistonBlockEntity;
import ca.fxco.configurablepistons.newBlocks.speedPiston.SpeedPistonExtensionBlock;
import ca.fxco.configurablepistons.renderers.BasicPistonBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ConfigurablePistons implements ModInitializer, ClientModInitializer {

    //TODO: OMG Stop the jank
    // Move the client initializer out of here ffs
    // Move block & item intializer out of this class
    // Stop hurting yourself by writing initialization like that
    // Stop reinitializing the freckin predicates
    // Generate the model json files automatically, currently all using the basic model
    // Implement custom textures
    // Stop Bad

    public static final String MOD_ID = "configurable-pistons";

    public static final boolean PISTON_PROGRESS_FIX = true;

    public static final Block DRAG_BLOCK;

    public static final BasicPistonBlock BASIC_PISTON;
    public static final BasicPistonBlock BASIC_STICKY_PISTON;
    public static final BasicPistonHeadBlock BASIC_PISTON_HEAD;
    public static final BasicPistonExtensionBlock BASIC_MOVING_PISTON;

    public static final BasicPistonBlock STRONG_PISTON;
    public static final BasicPistonBlock STRONG_STICKY_PISTON;
    public static final BasicPistonHeadBlock STRONG_PISTON_HEAD;
    public static final SpeedPistonExtensionBlock STRONG_MOVING_PISTON;

    public static final BasicPistonBlock FAST_PISTON;
    public static final BasicPistonBlock FAST_STICKY_PISTON;
    public static final BasicPistonHeadBlock FAST_PISTON_HEAD;
    public static final FastPistonExtensionBlock FAST_MOVING_PISTON;

    public static BlockEntityType<BasicPistonBlockEntity> BASIC_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<SpeedPistonBlockEntity> SPEED_PISTON_BLOCK_ENTITY;
    public static BlockEntityType<FastPistonBlockEntity> FAST_PISTON_BLOCK_ENTITY;

    public static Identifier id(String str) {
        return new Identifier(MOD_ID, str);
    }

    @Override
    public void onInitialize() {
        // Blocks
        Registry.register(Registry.BLOCK, id("drag_block"), DRAG_BLOCK);

        //TODO: Either generate these using the families or make a quick and simple method
        Registry.register(Registry.BLOCK, id("basic_piston"), BASIC_PISTON);
        Registry.register(Registry.BLOCK, id("basic_sticky_piston"), BASIC_STICKY_PISTON);
        Registry.register(Registry.BLOCK, id("basic_piston_head"), BASIC_PISTON_HEAD);
        Registry.register(Registry.BLOCK, id("basic_moving_piston"), BASIC_MOVING_PISTON);

        Registry.register(Registry.BLOCK, id("strong_piston"), STRONG_PISTON);
        Registry.register(Registry.BLOCK, id("strong_sticky_piston"), STRONG_STICKY_PISTON);
        Registry.register(Registry.BLOCK, id("strong_piston_head"), STRONG_PISTON_HEAD);
        Registry.register(Registry.BLOCK, id("strong_moving_piston"), STRONG_MOVING_PISTON);

        Registry.register(Registry.BLOCK, id("fast_piston"), FAST_PISTON);
        Registry.register(Registry.BLOCK, id("fast_sticky_piston"), FAST_STICKY_PISTON);
        Registry.register(Registry.BLOCK, id("fast_piston_head"), FAST_PISTON_HEAD);
        Registry.register(Registry.BLOCK, id("fast_moving_piston"), FAST_MOVING_PISTON);
        // Items
        // TODO: Make your own creative tab / item settings group
        Registry.register(Registry.ITEM, id("drag_block"), new BlockItem(DRAG_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE)));

        Registry.register(Registry.ITEM, id("basic_piston"), new BlockItem(BASIC_PISTON, new Item.Settings().group(ItemGroup.REDSTONE)));
        Registry.register(Registry.ITEM, id("basic_sticky_piston"), new BlockItem(BASIC_STICKY_PISTON, new Item.Settings().group(ItemGroup.REDSTONE)));

        Registry.register(Registry.ITEM, id("strong_piston"), new BlockItem(STRONG_PISTON, new Item.Settings().group(ItemGroup.REDSTONE)));
        Registry.register(Registry.ITEM, id("strong_sticky_piston"), new BlockItem(STRONG_STICKY_PISTON, new Item.Settings().group(ItemGroup.REDSTONE)));

        Registry.register(Registry.ITEM, id("fast_piston"), new BlockItem(FAST_PISTON, new Item.Settings().group(ItemGroup.REDSTONE)));
        Registry.register(Registry.ITEM, id("fast_sticky_piston"), new BlockItem(FAST_STICKY_PISTON, new Item.Settings().group(ItemGroup.REDSTONE)));
        // Block Entities
        BASIC_PISTON_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("basic_piston_entity"), FabricBlockEntityTypeBuilder.create(BasicPistonBlockEntity::new, BASIC_MOVING_PISTON).build(null));
        SPEED_PISTON_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("speed_piston_entity"), FabricBlockEntityTypeBuilder.create(SpeedPistonBlockEntity::new, STRONG_MOVING_PISTON).build(null));
        FAST_PISTON_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("fast_piston_entity"), FabricBlockEntityTypeBuilder.create(FastPistonBlockEntity::new, FAST_MOVING_PISTON).build(null));
    }

    @Override
    public void onInitializeClient() {
        // TODO: Stop using the deprecated method!
        BlockEntityRendererRegistry.INSTANCE.register(BASIC_PISTON_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(SPEED_PISTON_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(FAST_PISTON_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
    }

    static {
        // Piston Blocks should always be initialized in the following order:
        // Moving Pistons, Piston heads, base piston blocks

        // Basic Piston
        // Acts exactly like a normal vanilla piston
        BASIC_MOVING_PISTON = new BasicPistonExtensionBlock();
        BASIC_PISTON_HEAD = new BasicPistonHeadBlock();
        BASIC_PISTON = new BasicPistonBlock(false);
        BASIC_STICKY_PISTON = new BasicPistonBlock(true);

        // Strong Piston
        // Can push 24 blocks, although it takes a lot longer to push (0.05x slower)
        STRONG_MOVING_PISTON = new SpeedPistonExtensionBlock(0.05F);
        STRONG_PISTON_HEAD = new BasicPistonHeadBlock();
        STRONG_PISTON = new PushLimitPistonBlock(false,24, STRONG_MOVING_PISTON, STRONG_PISTON_HEAD);
        STRONG_STICKY_PISTON = new PushLimitPistonBlock(true,24, STRONG_MOVING_PISTON, STRONG_PISTON_HEAD);

        // Fast Piston
        // Can only push 2 block, although it's very fast
        FAST_MOVING_PISTON = new FastPistonExtensionBlock();
        FAST_PISTON_HEAD = new BasicPistonHeadBlock();
        FAST_PISTON = new PushLimitPistonBlock(false,2, FAST_MOVING_PISTON, FAST_PISTON_HEAD);
        FAST_STICKY_PISTON = new PushLimitPistonBlock(true,2, FAST_MOVING_PISTON, FAST_PISTON_HEAD);

        // Create Custom Blocks
        DRAG_BLOCK = new PullOnlyBlock(FabricBlockSettings.of(Material.METAL).strength(22.0f).hardness(18.0f));
    }
}
