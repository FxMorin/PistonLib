package ca.fxco.configurablepistons.datagen;

import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;

public class PistonBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public PistonBlockTagProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateTags() {
        FabricTagBuilder<Block> dragonImmuneBuilder = getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE);
        FabricTagBuilder<Block> witherImmuneBuilder = getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE);
        FabricTagBuilder<Block> slipperyIgnoreBuilder = getOrCreateTagBuilder(ModTags.SLIPPERY_IGNORE_BLOCKS);
        FabricTagBuilder<Block> slipperyBuilder = getOrCreateTagBuilder(ModTags.SLIPPERY_BLOCKS);
        FabricTagBuilder<Block> movingPistonBuilder = getOrCreateTagBuilder(ModTags.MOVING_PISTONS);
        FabricTagBuilder<Block> stickyBlocksBuilder = getOrCreateTagBuilder(ModTags.STICKY_BLOCKS);
        FabricTagBuilder<Block> unpushableBuilder = getOrCreateTagBuilder(ModTags.UNPUSHABLE);
        FabricTagBuilder<Block> pistonsBuilder = getOrCreateTagBuilder(ModTags.PISTONS);
        System.out.println("Amount of Piston Families: "+PistonFamilies.getFamilies().size());
        for (PistonFamily family : PistonFamilies.getFamilies()) {
            if (!family.shouldGenerateAutomatically()) continue;
            BasicPistonBlock pistonBlock = family.getPistonBlock(); // Pistons - pistonBlock
            if (pistonBlock != null) pistonsBuilder.add(pistonBlock);
            BasicPistonBlock stickyPistonBlock = family.getStickyPistonBlock(); // Pistons - stickyPistonBlock
            if (stickyPistonBlock != null) pistonsBuilder.add(stickyPistonBlock);
            BasicPistonExtensionBlock movingPistonBlock = family.getExtensionBlock(); // Moving_Pistons
            if (movingPistonBlock != null) movingPistonBuilder.add(movingPistonBlock);
        }
        // Populate custom tags
        pistonsBuilder.add(Blocks.PISTON, Blocks.STICKY_PISTON);
        movingPistonBuilder.add(Blocks.MOVING_PISTON);
        stickyBlocksBuilder.add(Blocks.SLIME_BLOCK, Blocks.HONEY_BLOCK, ModBlocks.SLIMY_REDSTONE_BLOCK, ModBlocks.SLIPPERY_SLIME_BLOCK);
        unpushableBuilder.add(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.RESPAWN_ANCHOR);
        slipperyIgnoreBuilder.addTag(ModTags.MOVING_PISTONS).addTag(ModTags.PISTONS).add(Blocks.OBSERVER, Blocks.REDSTONE_BLOCK);
        slipperyBuilder.add(
                ModBlocks.SLIPPERY_SLIME_BLOCK, ModBlocks.SLIPPERY_REDSTONE_BLOCK, ModBlocks.SLIPPERY_STONE_BLOCK,
                ModBlocks.SLIPPERY_PISTON, ModBlocks.SLIPPERY_STICKY_PISTON, ModBlocks.SLIPPERY_PISTON_HEAD,
                ModBlocks.SLIPPERY_MOVING_PISTON
        );
        // Add tags to vanilla tags
        dragonImmuneBuilder.addTag(ModTags.MOVING_PISTONS);
        witherImmuneBuilder.addTag(ModTags.MOVING_PISTONS);
    }
}
