package ca.fxco.configurablepistons.datagen;

import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonArmBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.PistonType;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static net.minecraft.data.client.BlockStateModelGenerator.createNorthDefaultRotationStates;

class PistonModelProvider extends FabricModelProvider {
    PistonModelProvider(FabricDataGenerator generator) {
        super(generator);
    }

    private static final Model PISTON_EXTENDED = block("piston_extended", TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.INSIDE);

    private static Model block(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(new Identifier("minecraft", "block/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator modelGenerator) {
        for (PistonFamily family : PistonFamilies.getFamilies()) {
            if (!family.shouldGenerateAutomatically()) continue;
            BasicPistonBlock basicPistonBlock = family.getPistonBlock();
            BasicPistonBlock basicStickyPistonBlock = family.getStickyPistonBlock();
            Block baseBlock = family.hasCustomTextures() ? family.getBaseBlock() : Blocks.PISTON;
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
        System.out.println("Amount of Custom Blocks: "+DatagenInitializer.datagenBlockList.size());
        for (Block block : DatagenInitializer.datagenBlockList)
            modelGenerator.registerSimpleCubeAll(block);
        modelGenerator.registerSimpleState(ModBlocks.SLIPPERY_SLIME_BLOCK);
    }

    private void registerPiston(BlockStateModelGenerator modelGenerator, @Nullable Block basePiston,
                                @Nullable Block stickyPiston, @Nullable Block base) {
        if (base == null || (basePiston == null && stickyPiston == null))
            throw new IllegalStateException("All pistons must have either a base or sticky block!");
        TextureMap baseTextureMap = new TextureMap()
                .put(TextureKey.BOTTOM, TextureMap.getSubId(base, "_bottom"))
                .put(TextureKey.SIDE, TextureMap.getSubId(base, "_side"))
                .put(TextureKey.INSIDE, TextureMap.getSubId(base,"_inner"));
        Identifier basePistonId = TextureMap.getSubId(base, "_top");
        Identifier stickyPistonId = TextureMap.getSubId(base, "_top_sticky");
        Identifier baseIdentifier = base == Blocks.PISTON ? ModelIds.getBlockSubModelId(base, "_base") :
                PISTON_EXTENDED.upload(base, "_base", baseTextureMap,
                        modelGenerator.modelCollector);
        if (basePiston != null) {
            TextureMap basePistonTextureMap = baseTextureMap.copyAndAdd(TextureKey.PLATFORM, basePistonId);
            modelGenerator.registerPiston(basePiston, baseIdentifier, basePistonTextureMap);
            Identifier basePistonInventoryId = Models.CUBE_BOTTOM_TOP.upload(basePiston, "_inventory",
                    baseTextureMap.copyAndAdd(TextureKey.TOP, basePistonId), modelGenerator.modelCollector);
            modelGenerator.registerParentedItemModel(basePiston, basePistonInventoryId);
        }
        if (stickyPiston != null) {
            TextureMap stickyPistonTextureMap = baseTextureMap.copyAndAdd(TextureKey.PLATFORM, stickyPistonId);
            modelGenerator.registerPiston(stickyPiston, baseIdentifier, stickyPistonTextureMap);
            Identifier stickyPistonInventoryId = Models.CUBE_BOTTOM_TOP.upload(stickyPiston, "_inventory",
                    baseTextureMap.copyAndAdd(TextureKey.TOP, stickyPistonId), modelGenerator.modelCollector);
            modelGenerator.registerParentedItemModel(stickyPiston, stickyPistonInventoryId);
        }
    }

    private void registerPistonHead(BlockStateModelGenerator modelGen,
                                    BasicPistonHeadBlock head, Block base) {
        //System.out.println(base);
        TextureMap texMap = new TextureMap().put(TextureKey.UNSTICKY, TextureMap.getSubId(base, "_top"))
                .put(TextureKey.SIDE, TextureMap.getSubId(base, "_side"));
        TextureMap texMap2 = texMap.copyAndAdd(TextureKey.PLATFORM, TextureMap.getSubId(base, "_top_sticky"));
        TextureMap texMap3 = texMap.copyAndAdd(TextureKey.PLATFORM, TextureMap.getSubId(base, "_top"));
        if (base == Blocks.PISTON) {
            modelGen.blockStateCollector.accept(
                    VariantsBlockStateSupplier.create(head)
                            .coordinate(BlockStateVariantMap.create(Properties.SHORT, Properties.PISTON_TYPE)
                                    .register(false, PistonType.DEFAULT, BlockStateVariant.create()
                                            .put(VariantSettings.MODEL,
                                                    new Identifier("minecraft:block/piston_head")
                                            ))
                                    .register(false, PistonType.STICKY, BlockStateVariant.create()
                                            .put(VariantSettings.MODEL,
                                                    new Identifier("minecraft:block/piston_head_sticky")
                                            ))
                                    .register(true, PistonType.DEFAULT, BlockStateVariant.create()
                                            .put(VariantSettings.MODEL,
                                                    new Identifier("minecraft:block/piston_head_short")
                                            ))
                                    .register(true, PistonType.STICKY, BlockStateVariant.create()
                                            .put(VariantSettings.MODEL,
                                                    new Identifier("minecraft:block/piston_head_short_sticky")
                                            )))
                            .coordinate(createNorthDefaultRotationStates()));
        } else {
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
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {}
}
