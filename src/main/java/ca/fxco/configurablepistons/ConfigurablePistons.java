package ca.fxco.configurablepistons;

import ca.fxco.configurablepistons.base.BasicPistonBlock;
import ca.fxco.configurablepistons.base.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.newBlocks.PullOnlyBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ConfigurablePistons implements ModInitializer {

    public static final String MOD_ID = "configurable_pistons";

    public static final boolean PISTON_PROGRESS_FIX = true;

    public static final Block DRAG_BLOCK;

    public static final BasicPistonBlock BASIC_PISTON;
    public static final BasicPistonBlock BASIC_STICKY_PISTON;
    public static final BasicPistonHeadBlock BASIC_PISTON_HEAD;

    // Tags
    public static final TagKey<Block> PISTONS = TagKey.of(Registry.BLOCK_KEY,
            new Identifier(MOD_ID, "pistons"));
    public static final TagKey<Block> UNPUSHABLE = TagKey.of(Registry.BLOCK_KEY,
            new Identifier(MOD_ID, "unpushable"));

    @Override
    public void onInitialize() {
        // Blocks
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "drag_block"), DRAG_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "basic_piston"), BASIC_PISTON);
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "basic_sticky_piston"), BASIC_STICKY_PISTON);
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "basic_piston_head"), BASIC_PISTON_HEAD);
        // Items
        Registry.register(Registry.ITEM,
                new Identifier(MOD_ID, "basic_piston"),
                new BlockItem(BASIC_PISTON, new Item.Settings().group(ItemGroup.REDSTONE))
        );
        Registry.register(Registry.ITEM,
                new Identifier(MOD_ID, "basic_sticky_piston"),
                new BlockItem(BASIC_STICKY_PISTON, new Item.Settings().group(ItemGroup.REDSTONE))
        );
        Registry.register(Registry.ITEM,
                new Identifier(MOD_ID, "drag_block"),
                new BlockItem(DRAG_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE))
        );
    }

    static {
        DRAG_BLOCK = new PullOnlyBlock(FabricBlockSettings.of(Material.METAL).strength(22.0f).hardness(18.0f));
        BASIC_PISTON = new BasicPistonBlock(false,FabricBlockSettings.of(Material.PISTON).strength(1.5F).solidBlock((a,b,c) -> false).suffocates((state, world, pos) -> !(Boolean)state.get(PistonBlock.EXTENDED)).blockVision((state, world, pos) -> !(Boolean)state.get(PistonBlock.EXTENDED)));
        BASIC_STICKY_PISTON = new BasicPistonBlock(true,FabricBlockSettings.of(Material.PISTON).strength(1.5F).solidBlock((a,b,c) -> false).suffocates((state, world, pos) -> !(Boolean)state.get(PistonBlock.EXTENDED)).blockVision((state, world, pos) -> !(Boolean)state.get(PistonBlock.EXTENDED)));
        BASIC_PISTON_HEAD = new BasicPistonHeadBlock(FabricBlockSettings.of(Material.PISTON).strength(1.5F).dropsNothing());
    }
}
