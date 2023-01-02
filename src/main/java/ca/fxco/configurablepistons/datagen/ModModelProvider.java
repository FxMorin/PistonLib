package ca.fxco.configurablepistons.datagen;

import java.util.Optional;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.PistonType;
import net.minecraft.data.client.*;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;

public class ModModelProvider extends FabricModelProvider {
	public static final Logger LOGGER = ConfigurablePistons.LOGGER;

	public static final Model TEMPLATE_PISTON_ARM = new Model(Optional.of(ConfigurablePistons.id("block/template_piston_arm")), Optional.empty(), TextureKey.TEXTURE);
	public static final Model TEMPLATE_PISTON_ARM_SHORT = new Model(Optional.of(ConfigurablePistons.id("block/template_piston_arm_short")), Optional.empty(), TextureKey.TEXTURE);
	public static final Model TEMPLATE_MOVING_PISTON = new Model(Optional.of(ConfigurablePistons.id("block/template_moving_piston")), Optional.empty(), TextureKey.PARTICLE);
	public static final Model TEMPLATE_HALF_BLOCK = new Model(Optional.of(ConfigurablePistons.id("block/template_half_block")), Optional.empty(), TextureKey.TOP, TextureKey.SIDE);
	public static final Model PISTON_BASE = new Model(Optional.of(new Identifier("block/piston_extended")), Optional.empty(), TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.INSIDE);

	public ModModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator generator) {
		LOGGER.info("Generating blockstate definitions and models...");

		for(PistonFamily family : PistonFamilies.getFamilies()) {
			LOGGER.info("Generating blockstate definitions and models for piston family "+family.getId()+"...");
			registerPistonFamily(generator, family);
		}

		LOGGER.info("Finished generating blockstate definitions and models for pistons, generating for other blocks...");

		registerHalfBlock(generator, ModBlocks.HALF_OBSIDIAN_BLOCK, Blocks.OBSIDIAN);
		registerHalfBlock(generator, ModBlocks.HALF_REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);
		registerHalfBlockWithCustomModel(generator, ModBlocks.HALF_HONEY_BLOCK);
		registerHalfBlockWithCustomModel(generator, ModBlocks.HALF_SLIME_BLOCK);

		generator.registerSimpleCubeAll(ModBlocks.DRAG_BLOCK);
		generator.registerSimpleCubeAll(ModBlocks.STICKYLESS_BLOCK);
		generator.registerSimpleCubeAll(ModBlocks.GLUE_BLOCK);
		generator.registerSimpleCubeAll(ModBlocks.SLIPPERY_REDSTONE_BLOCK);
		generator.registerSimpleCubeAll(ModBlocks.SLIPPERY_STONE_BLOCK);

		generator.registerSingleton(ModBlocks.STICKY_TOP_BLOCK, new TextureMap().put(TextureKey.SIDE, TextureMap.getId(Blocks.DEEPSLATE_BRICKS)).put(TextureKey.TOP, TextureMap.getId(ModBlocks.STICKY_TOP_BLOCK)), Models.CUBE_TOP);

		generator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(ModBlocks.SLIMY_REDSTONE_BLOCK, ModelIds.getBlockModelId(ModBlocks.SLIMY_REDSTONE_BLOCK)));
		generator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(ModBlocks.SLIPPERY_SLIME_BLOCK, ModelIds.getBlockModelId(ModBlocks.SLIPPERY_SLIME_BLOCK)));

		generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.ALL_SIDED_OBSERVER).coordinate(
				BlockStateVariantMap.create(Properties.POWERED)
						.register(false, BlockStateVariant.create().put(
								VariantSettings.MODEL, TexturedModel.CUBE_ALL.upload(ModBlocks.ALL_SIDED_OBSERVER, generator.modelCollector)))
						.register(true, BlockStateVariant.create().put(
								VariantSettings.MODEL, TexturedModel.CUBE_ALL.upload(ModBlocks.ALL_SIDED_OBSERVER, "_on", generator.modelCollector)))
		));

		generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.POWERED_STICKY_BLOCK).coordinate(
				BlockStateVariantMap.create(Properties.POWERED)
						.register(false, BlockStateVariant.create().put(
								VariantSettings.MODEL, ModelIds.getBlockModelId(ModBlocks.POWERED_STICKY_BLOCK)))
						.register(true, BlockStateVariant.create().put(
								VariantSettings.MODEL, ModelIds.getBlockSubModelId(ModBlocks.POWERED_STICKY_BLOCK, "_on")))
		).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()));

		generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.STICKY_CHAIN_BLOCK, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(ModBlocks.STICKY_CHAIN_BLOCK))).coordinate(
				BlockStateModelGenerator.createAxisRotatedVariantMap()
		));

		generator.registerItemModel(ModBlocks.STICKY_CHAIN_BLOCK.asItem());

		LOGGER.info("Finished generating blockstate definitions and models!");
	}

	@Override
	public void generateItemModels(ItemModelGenerator generator) {
	}

	public static void registerHalfBlockWithCustomModel(BlockStateModelGenerator generator, Block halfBlock) {
		registerHalfBlock(generator, halfBlock, null);
	}

	public static void registerHalfBlock(BlockStateModelGenerator generator, Block halfBlock, @Nullable Block base) {
		generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(halfBlock, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(halfBlock))).coordinate(
				generator.createUpDefaultFacingVariantMap()
		));

		if (base != null) {
			Identifier baseTextureId = TextureMap.getId(base);

			TextureMap halfBlockTextureMap = new TextureMap().put(TextureKey.SIDE, baseTextureId).put(TextureKey.TOP, baseTextureId);

			TEMPLATE_HALF_BLOCK.upload(halfBlock, halfBlockTextureMap, generator.modelCollector);
		}
	}

	public static void registerPistonFamily(BlockStateModelGenerator generator, PistonFamily family) {
		boolean customTextures = family.hasCustomTextures();
		Block textureBaseBlock = customTextures ? family.getBaseBlock() : Blocks.PISTON;

		Block base = family.getBaseBlock();
		Block piston = family.getPistonBlock();
		Block stickyPiston = family.getStickyPistonBlock();
		Block pistonHead = family.getHeadBlock();
		Block pistonArm = family.getArmBlock();
		Block movingPiston = family.getExtensionBlock();

		Identifier sideTextureId = TextureMap.getSubId(textureBaseBlock, "_side");

		TextureMap textureMap = new TextureMap().put(TextureKey.BOTTOM, TextureMap.getSubId(textureBaseBlock, "_bottom")).put(TextureKey.SIDE, sideTextureId);

		TextureMap baseTextureMap = textureMap.copyAndAdd(TextureKey.INSIDE, TextureMap.getSubId(textureBaseBlock, "_inner"));

		Identifier baseModelId = PISTON_BASE.upload(base, "_base", baseTextureMap, generator.modelCollector);

		Identifier topRegularTextureId = TextureMap.getSubId(textureBaseBlock, "_top");
		Identifier topStickyTextureId = TextureMap.getSubId(textureBaseBlock, "_top_sticky");

		if (piston != null) {
			TextureMap regularTextureMap = textureMap.copyAndAdd(TextureKey.PLATFORM, topRegularTextureId);
			generator.registerPiston(piston, baseModelId, regularTextureMap);
			Identifier regularInventoryModelId = Models.CUBE_BOTTOM_TOP.upload(piston, "_inventory", textureMap.copyAndAdd(TextureKey.TOP, topRegularTextureId), generator.modelCollector);
			if (piston.asItem() != Items.AIR) generator.registerParentedItemModel(piston, regularInventoryModelId);
		}

		if (stickyPiston != null) {
			TextureMap stickyTextureMap = textureMap.copyAndAdd(TextureKey.PLATFORM, topStickyTextureId);
			generator.registerPiston(stickyPiston, baseModelId, stickyTextureMap);
			Identifier stickyInventoryModelId = Models.CUBE_BOTTOM_TOP.upload(stickyPiston, "_inventory", textureMap.copyAndAdd(TextureKey.TOP, topStickyTextureId), generator.modelCollector);
			if (stickyPiston.asItem() != Items.AIR) generator.registerParentedItemModel(stickyPiston, stickyInventoryModelId);
		}

		if (pistonHead != null) {
			TextureMap baseHeadTextureMap = new TextureMap().put(TextureKey.UNSTICKY, topRegularTextureId).put(TextureKey.SIDE, sideTextureId);
			TextureMap regularHeadTextureMap = baseHeadTextureMap.copyAndAdd(TextureKey.PLATFORM, topRegularTextureId);
			TextureMap stickyHeadTextureMap = baseHeadTextureMap.copyAndAdd(TextureKey.PLATFORM, topStickyTextureId);

			generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(pistonHead).coordinate(
					BlockStateVariantMap.create(Properties.SHORT, Properties.PISTON_TYPE)
							.register(false, PistonType.DEFAULT, BlockStateVariant.create().put(
									VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD.upload(base, "_head", regularHeadTextureMap, generator.modelCollector)))
							.register(false, PistonType.STICKY, BlockStateVariant.create().put(
									VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD.upload(base, "_head_sticky", stickyHeadTextureMap, generator.modelCollector)))
							.register(true, PistonType.DEFAULT, BlockStateVariant.create().put(
									VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD_SHORT.upload(base, "_head_short", regularHeadTextureMap, generator.modelCollector)))
							.register(true, PistonType.STICKY, BlockStateVariant.create().put(
									VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD_SHORT.upload(base, "_head_short_sticky", stickyHeadTextureMap, generator.modelCollector)))
			).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()));
		}

		if (pistonArm != null) {
			TextureMap armTextureMap = new TextureMap().put(TextureKey.TEXTURE, sideTextureId);

			generator.blockStateCollector.accept((VariantsBlockStateSupplier.create(pistonArm).coordinate(
					BlockStateVariantMap.create(Properties.SHORT)
							.register(false, BlockStateVariant.create().put(
									VariantSettings.MODEL, TEMPLATE_PISTON_ARM.upload(base, "_arm", armTextureMap, generator.modelCollector)))
							.register(true, BlockStateVariant.create().put(
									VariantSettings.MODEL, TEMPLATE_PISTON_ARM_SHORT.upload(base, "_arm_short", armTextureMap, generator.modelCollector)))
			).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates())));
		}

		if (movingPiston != null) {
			TextureMap movingPistonTextureMap = new TextureMap().put(TextureKey.PARTICLE, sideTextureId);

			generator.registerSingleton(movingPiston, movingPistonTextureMap, TEMPLATE_MOVING_PISTON);
		}
	}
}
