package ca.fxco.configurablepistons.datagen;

import ca.fxco.configurablepistons.base.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.families.PistonFamilies;
import ca.fxco.configurablepistons.families.PistonFamily;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;

public class PistonTagDatagen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(new PistonBlockTagProvider(fabricDataGenerator));
    }

    public static class PistonBlockTagProvider extends FabricTagProvider.BlockTagProvider {

        public PistonBlockTagProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateTags() {
            FabricTagBuilder<Block> dragonImmuneBuilder = tag(BlockTags.DRAGON_IMMUNE);
            FabricTagBuilder<Block> witherImmuneBuilder = tag(BlockTags.WITHER_IMMUNE);
            System.out.println("Amount of Piston Families: "+PistonFamilies.getFamilies().size());
            for (PistonFamily family : PistonFamilies.getFamilies()) {
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
}
