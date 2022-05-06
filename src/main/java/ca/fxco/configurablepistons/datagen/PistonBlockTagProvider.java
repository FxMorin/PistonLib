package ca.fxco.configurablepistons.datagen;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;

public class PistonBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public PistonBlockTagProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateTags() {
        FabricTagBuilder<Block> dragonImmuneBuilder = tag(BlockTags.DRAGON_IMMUNE);
        FabricTagBuilder<Block> witherImmuneBuilder = tag(BlockTags.WITHER_IMMUNE);
        System.out.println("Amount of Piston Families: "+PistonFamilies.getFamilies().size());
        for (PistonFamily family : PistonFamilies.getFamilies()) {
            if (!family.shouldGenerateAutomatically()) continue;
            BasicPistonExtensionBlock basicPistonExtensionBlock = family.getExtensionBlock();
            if (basicPistonExtensionBlock != null) {
                dragonImmuneBuilder.add(basicPistonExtensionBlock);
                witherImmuneBuilder.add(basicPistonExtensionBlock);
            }
        }
    }

    public FabricTagBuilder<Block> tag(TagKey<Block> tag) {
        return getOrCreateTagBuilder(tag);
    }
}
