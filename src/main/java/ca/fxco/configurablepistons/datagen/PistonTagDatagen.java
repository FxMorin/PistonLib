package ca.fxco.configurablepistons.datagen;

import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonArmBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.PistonType;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.data.client.BlockStateModelGenerator.createNorthDefaultRotationStates;

public class PistonTagDatagen implements DataGeneratorEntrypoint {

    public static List<Block> datagenBlockList = new ArrayList<>();

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(new PistonBlockTagProvider(fabricDataGenerator));
        fabricDataGenerator.addProvider(new PistonModelProvider(fabricDataGenerator));
    }

    public static class PistonBlockTagProvider extends FabricTagProvider.BlockTagProvider {

        public PistonBlockTagProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateTags() {
            ModBlocks.BASIC_PISTON.getName(); // Crucial, makes sure that mod blocks are loaded before doing datagen
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

    private static class PistonModelProvider extends FabricModelProvider {
        private PistonModelProvider(FabricDataGenerator generator) {
            super(generator);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator modelGenerator) {
            for (PistonFamily family : PistonFamilies.getFamilies()) {
                if (!family.shouldGenerateAutomatically()) continue;
                BasicPistonBlock basicPistonBlock = family.getPistonBlock();
                BasicPistonBlock basicStickyPistonBlock = family.getStickyPistonBlock();
                BasicPistonBlock baseBlock = basicPistonBlock != null ? basicPistonBlock : basicStickyPistonBlock;
                registerPiston(modelGenerator, basicPistonBlock, basicStickyPistonBlock, baseBlock);

                BasicPistonExtensionBlock basicPistonExtensionBlock = family.getExtensionBlock();
                if (basicPistonExtensionBlock != null) {
                    modelGenerator.registerBuiltinWithParticle(basicPistonExtensionBlock,
                            TextureMap.getSubId(baseBlock, "_side"));
                }
                BasicPistonHeadBlock basicPistonHeadBlock = family.getHeadBlock();
                if (basicPistonHeadBlock != null) {
                    registerPistonHead(modelGenerator, basicPistonHeadBlock, baseBlock);
                }
                // TODO: Add an actual piston arm to the game
                BasicPistonArmBlock basicPistonArmBlock = family.getArmBlock();
                if (basicPistonArmBlock != null) {}
            }
            for (Block block : datagenBlockList) {
                modelGenerator.registerSimpleCubeAll(block);
            }
            datagenBlockList.clear();
        }

        private void registerPiston(BlockStateModelGenerator modelGenerator, @Nullable Block basePiston,
                                    @Nullable Block stickyPiston, @Nullable Block base) {
            if (base == null || (basePiston == null && stickyPiston == null))
                throw new IllegalStateException("All pistons must have either a base or sticky block!");
            TextureMap baseTextureMap = new TextureMap()
                    .put(TextureKey.BOTTOM, TextureMap.getSubId(base, "_bottom"))
                    .put(TextureKey.SIDE, TextureMap.getSubId(base, "_side"));
            Identifier baseIdentifier = ModelIds.getBlockSubModelId(base, "_base");
            if (basePiston != null) {
                Identifier basePistonId = TextureMap.getSubId(base, "_top");
                TextureMap basePistonTextureMap = baseTextureMap.copyAndAdd(TextureKey.PLATFORM, basePistonId);
                modelGenerator.registerPiston(basePiston, baseIdentifier, basePistonTextureMap);
                Identifier basePistonInventoryId = Models.CUBE_BOTTOM_TOP.upload(basePiston, "_inventory",
                        baseTextureMap.copyAndAdd(TextureKey.TOP, basePistonId), modelGenerator.modelCollector);
                modelGenerator.registerParentedItemModel(basePiston, basePistonInventoryId);
            }
            if (stickyPiston != null) {
                Identifier stickyPistonId = TextureMap.getSubId(base, "_top_sticky");
                TextureMap stickyPistonTextureMap = baseTextureMap.copyAndAdd(TextureKey.PLATFORM, stickyPistonId);
                modelGenerator.registerPiston(stickyPiston, baseIdentifier, stickyPistonTextureMap);
                Identifier stickyPistonInventoryId = Models.CUBE_BOTTOM_TOP.upload(stickyPiston, "_inventory",
                        baseTextureMap.copyAndAdd(TextureKey.TOP, stickyPistonId), modelGenerator.modelCollector);
                modelGenerator.registerParentedItemModel(stickyPiston, stickyPistonInventoryId);
            }
        }

        private void registerPistonHead(BlockStateModelGenerator modelGen,
                                        BasicPistonHeadBlock head, BasicPistonBlock base) {
            TextureMap texMap = new TextureMap().put(TextureKey.UNSTICKY, TextureMap.getSubId(base, "_top"))
                    .put(TextureKey.SIDE, TextureMap.getSubId(base, "_side"));
            TextureMap texMap2 = texMap.copyAndAdd(TextureKey.PLATFORM, TextureMap.getSubId(base, "_top_sticky"));
            TextureMap texMap3 = texMap.copyAndAdd(TextureKey.PLATFORM, TextureMap.getSubId(base, "_top"));
            modelGen.blockStateCollector.accept(
                    VariantsBlockStateSupplier.create(head)
                            .coordinate(BlockStateVariantMap.create(Properties.SHORT, Properties.PISTON_TYPE)
                                    .register(false, PistonType.DEFAULT, BlockStateVariant.create()
                                            .put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD
                                                    .upload(
                                                            base,
                                                            "_head",
                                                            texMap3, modelGen.modelCollector
                                                    )))
                                    .register(false, PistonType.STICKY, BlockStateVariant.create()
                                            .put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD
                                                    .upload(
                                                            base,
                                                            "_head_sticky",
                                                            texMap2, modelGen.modelCollector
                                                    )))
                                    .register(true, PistonType.DEFAULT, BlockStateVariant.create()
                                            .put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD_SHORT
                                                    .upload(
                                                            base,
                                                            "_head_short",
                                                            texMap3, modelGen.modelCollector
                                                    )))
                                    .register(true, PistonType.STICKY, BlockStateVariant.create()
                                            .put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD_SHORT
                                                    .upload(
                                                            base,
                                                            "_head_short_sticky",
                                                            texMap2, modelGen.modelCollector
                                                    ))))
                            .coordinate(createNorthDefaultRotationStates()));
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {}
    }
}
