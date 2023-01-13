package ca.fxco.pistonlib.datagen;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamilies;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;

import static net.minecraft.data.models.BlockModelGenerators.createSimpleBlock;
import static net.minecraft.data.models.BlockModelGenerators.createSlab;

public class ModModelProvider extends FabricModelProvider {

    public static final Logger LOGGER = PistonLib.LOGGER;

	public static final ModelTemplate TEMPLATE_PISTON_ARM = new ModelTemplate(Optional.of(PistonLib.id("block/template_piston_arm")), Optional.empty(), TextureSlot.TEXTURE);
	public static final ModelTemplate TEMPLATE_PISTON_ARM_SHORT = new ModelTemplate(Optional.of(PistonLib.id("block/template_piston_arm_short")), Optional.empty(), TextureSlot.TEXTURE);
	public static final ModelTemplate TEMPLATE_MOVING_PISTON = new ModelTemplate(Optional.of(PistonLib.id("block/template_moving_piston")), Optional.empty(), TextureSlot.PARTICLE);
	public static final ModelTemplate TEMPLATE_HALF_BLOCK = new ModelTemplate(Optional.of(PistonLib.id("block/template_half_block")), Optional.empty(), TextureSlot.TOP, TextureSlot.SIDE);
	public static final ModelTemplate PISTON_BASE = new ModelTemplate(Optional.of(new ResourceLocation("block/piston_extended")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.INSIDE);

	public ModModelProvider(FabricDataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generator) {
		LOGGER.info("Generating blockstate definitions and models...");

		for(PistonFamily family : PistonFamilies.getFamilies()) {
			LOGGER.info("Generating blockstate definitions and models for piston family " + family.getId()+"...");
			registerPistonFamily(generator, family);
		}

		LOGGER.info("Finished generating blockstate definitions and models for pistons, generating for other blocks...");

		registerHalfBlock(generator, ModBlocks.HALF_OBSIDIAN_BLOCK, Blocks.OBSIDIAN);
		registerHalfBlock(generator, ModBlocks.HALF_REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);
		registerHalfBlockWithCustomModel(generator, ModBlocks.HALF_HONEY_BLOCK);
		registerHalfBlockWithCustomModel(generator, ModBlocks.HALF_SLIME_BLOCK);

		registerHalfBlockWithCustomStates(generator, ModBlocks.HALF_REDSTONE_LAMP_BLOCK,
			createLitFacingBlockState(
				ModelLocationUtils.getModelLocation(ModBlocks.HALF_REDSTONE_LAMP_BLOCK),
				ModelLocationUtils.getModelLocation(ModBlocks.HALF_REDSTONE_LAMP_BLOCK, "_on")));
		registerHalfBlockTextureMap(generator, ModBlocks.HALF_REDSTONE_LAMP_BLOCK, ModelLocationUtils.getModelLocation(Blocks.REDSTONE_LAMP));
		registerHalfBlockTextureMap(generator, ModBlocks.HALF_REDSTONE_LAMP_BLOCK, ModelLocationUtils.getModelLocation(Blocks.REDSTONE_LAMP, "_on"), "_on");

		generator.createRotatedPillarWithHorizontalVariant(ModBlocks.AXIS_LOCKED_BLOCK, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
        generator.createTrivialCube(ModBlocks.DRAG_BLOCK);
        generator.createTrivialCube(ModBlocks.STICKYLESS_BLOCK);
        generator.createTrivialCube(ModBlocks.GLUE_BLOCK);
        generator.createTrivialCube(ModBlocks.SLIPPERY_REDSTONE_BLOCK);
        generator.createTrivialCube(ModBlocks.SLIPPERY_STONE_BLOCK);
        generator.createTrivialCube(ModBlocks.MOVE_COUNTING_BLOCK);

		registerSlab(generator, Blocks.OBSIDIAN, ModBlocks.OBSIDIAN_SLAB_BLOCK);
		registerStair(generator, Blocks.OBSIDIAN, ModBlocks.OBSIDIAN_STAIR_BLOCK);

		generator.createTrivialBlock(ModBlocks.STICKY_TOP_BLOCK, new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.DEEPSLATE_BRICKS)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(ModBlocks.STICKY_TOP_BLOCK)), ModelTemplates.CUBE_TOP);

		generator.blockStateOutput.accept(createSimpleBlock(ModBlocks.SLIMY_REDSTONE_BLOCK, ModelLocationUtils.getModelLocation(ModBlocks.SLIMY_REDSTONE_BLOCK)));
		generator.blockStateOutput.accept(createSimpleBlock(ModBlocks.SLIPPERY_SLIME_BLOCK, ModelLocationUtils.getModelLocation(ModBlocks.SLIPPERY_SLIME_BLOCK)));

		generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.ALL_SIDED_OBSERVER).with(
			PropertyDispatch.property(BlockStateProperties.POWERED)
				.select(false, Variant.variant().with(
					VariantProperties.MODEL, TexturedModel.CUBE.create(ModBlocks.ALL_SIDED_OBSERVER, generator.modelOutput)))
				.select(true, Variant.variant().with(
					VariantProperties.MODEL, TexturedModel.CUBE.createWithSuffix(ModBlocks.ALL_SIDED_OBSERVER, "_on", generator.modelOutput)))
		));

		generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.POWERED_STICKY_BLOCK).with(
			PropertyDispatch.property(BlockStateProperties.POWERED)
				.select(false, Variant.variant().with(
					VariantProperties.MODEL, ModelLocationUtils.getModelLocation(ModBlocks.POWERED_STICKY_BLOCK)))
				.select(true, Variant.variant().with(
					VariantProperties.MODEL, ModelLocationUtils.getModelLocation(ModBlocks.POWERED_STICKY_BLOCK, "_on")))
		).with(BlockModelGenerators.createFacingDispatch()));

		generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.STICKY_CHAIN_BLOCK, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(ModBlocks.STICKY_CHAIN_BLOCK))).with(BlockModelGenerators.createRotatedPillar()));

		generator.createSimpleFlatItemModel(ModBlocks.STICKY_CHAIN_BLOCK.asItem());

		LOGGER.info("Finished generating blockstate definitions and models!");
	}

	@Override
	public void generateItemModels(ItemModelGenerators generator) {
	}

	public static void registerHalfBlockTextureMap(BlockModelGenerators generator, Block halfBlock, ResourceLocation baseTexture) {
        registerHalfBlockTextureMap(generator, halfBlock, baseTexture, null);
    }

    public static void registerHalfBlockTextureMap(BlockModelGenerators generator, Block halfBlock, ResourceLocation baseTexture, @Nullable String suffix) {
        TextureMapping halfBlockTextureMap = new TextureMapping().put(TextureSlot.SIDE, baseTexture).put(TextureSlot.TOP, baseTexture);
        if (suffix == null) {
            TEMPLATE_HALF_BLOCK.create(halfBlock, halfBlockTextureMap, generator.modelOutput);
        } else {
            TEMPLATE_HALF_BLOCK.createWithSuffix(halfBlock, suffix, halfBlockTextureMap, generator.modelOutput);
        }
    }

	public static void registerHalfBlockWithCustomModel(BlockModelGenerators generator, Block halfBlock) {
		registerHalfBlock(generator, halfBlock, null);
	}

	public static void registerHalfBlockWithCustomStates(BlockModelGenerators generator, Block halfBlock, PropertyDispatch customStates) {
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(halfBlock, Variant.variant()
                .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(halfBlock))).with(customStates)
        );
    }

	private static void registerSlab(BlockModelGenerators generator, Block baseBlock, Block block) {
		TextureMapping textureBase = TextureMapping.cube(baseBlock);
		ResourceLocation bottom = ModelTemplates.SLAB_BOTTOM.create(block, textureBase, generator.modelOutput);
		ResourceLocation top = ModelTemplates.SLAB_TOP.create(block, textureBase, generator.modelOutput);
		ResourceLocation _double = ModelTemplates.CUBE_COLUMN.createWithOverride(block, "_double", textureBase, generator.modelOutput);
		generator.blockStateOutput.accept(createSlab(block, bottom, top, _double));
		generator.delegateItemModel(block, bottom);
	}

	private static void registerStair(BlockModelGenerators generator, Block baseBlock, Block block) {
		TextureMapping textureBase = TextureMapping.cube(baseBlock);
		ResourceLocation inner = ModelTemplates.STAIRS_INNER.create(block, textureBase, generator.modelOutput);
		ResourceLocation flat = ModelTemplates.STAIRS_STRAIGHT.create(block, textureBase, generator.modelOutput);
		ResourceLocation outer = ModelTemplates.STAIRS_OUTER.create(block, textureBase, generator.modelOutput);
		generator.blockStateOutput.accept(BlockModelGenerators.createStairs(block, inner, flat, outer));
		generator.delegateItemModel(block, flat);
	}

	public static void registerHalfBlock(BlockModelGenerators generator, Block halfBlock, @Nullable Block base) {
		generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(halfBlock, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(halfBlock))).with(generator.createColumnWithFacing()));

		if (base != null) {
			ResourceLocation baseTextureId = TextureMapping.getBlockTexture(base);

			TextureMapping halfBlockTextureMap = new TextureMapping().put(TextureSlot.SIDE, baseTextureId).put(TextureSlot.TOP, baseTextureId);

			TEMPLATE_HALF_BLOCK.create(halfBlock, halfBlockTextureMap, generator.modelOutput);
		}
	}

	public static void registerPistonFamily(BlockModelGenerators generator, PistonFamily family) {
		boolean customTextures = family.hasCustomTextures();
		Block textureBaseBlock = customTextures ? family.getBaseBlock() : Blocks.PISTON;

		Block base = family.getBaseBlock();
		Block normalBase = family.getBaseBlock(PistonType.DEFAULT);
		Block stickyBase = family.getBaseBlock(PistonType.STICKY);
		Block pistonHead = family.getHeadBlock();
		Block pistonArm = family.getArmBlock();
		Block movingPiston = family.getMovingBlock();

		ResourceLocation sideTextureId = TextureMapping.getBlockTexture(textureBaseBlock, "_side");

		TextureMapping textureMap = new TextureMapping().put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(textureBaseBlock, "_bottom")).put(TextureSlot.SIDE, sideTextureId);

		TextureMapping baseTextureMap = textureMap.copyAndUpdate(TextureSlot.INSIDE, TextureMapping.getBlockTexture(textureBaseBlock, "_inner"));

		ResourceLocation baseModelId = PISTON_BASE.createWithSuffix(base, "_base", baseTextureMap, generator.modelOutput);

		ResourceLocation topRegularTextureId = TextureMapping.getBlockTexture(textureBaseBlock, "_top");
		ResourceLocation topStickyTextureId = TextureMapping.getBlockTexture(textureBaseBlock, "_top_sticky");

		if (normalBase != null) {
			TextureMapping regularTextureMap = textureMap.copyAndUpdate(TextureSlot.PLATFORM, topRegularTextureId);
			generator.createPistonVariant(normalBase, baseModelId, regularTextureMap);
			ResourceLocation regularInventoryModelId = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(normalBase, "_inventory", textureMap.copyAndUpdate(TextureSlot.TOP, topRegularTextureId), generator.modelOutput);
			if (normalBase.asItem() != Items.AIR) generator.delegateItemModel(normalBase, regularInventoryModelId);
		}

		if (stickyBase != null) {
			TextureMapping stickyTextureMap = textureMap.copyAndUpdate(TextureSlot.PLATFORM, topStickyTextureId);
			generator.createPistonVariant(stickyBase, baseModelId, stickyTextureMap);
			ResourceLocation stickyInventoryModelId = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(stickyBase, "_inventory", textureMap.copyAndUpdate(TextureSlot.TOP, topStickyTextureId), generator.modelOutput);
			if (stickyBase.asItem() != Items.AIR) generator.delegateItemModel(stickyBase, stickyInventoryModelId);
		}

		if (pistonHead != null) {
			TextureMapping baseHeadTextureMap = new TextureMapping().put(TextureSlot.UNSTICKY, topRegularTextureId).put(TextureSlot.SIDE, sideTextureId);
			TextureMapping regularHeadTextureMap = baseHeadTextureMap.copyAndUpdate(TextureSlot.PLATFORM, topRegularTextureId);
			TextureMapping stickyHeadTextureMap = baseHeadTextureMap.copyAndUpdate(TextureSlot.PLATFORM, topStickyTextureId);

			generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(pistonHead).with(
				PropertyDispatch.properties(BlockStateProperties.SHORT, BlockStateProperties.PISTON_TYPE)
					.select(false, PistonType.DEFAULT, Variant.variant().with(
						VariantProperties.MODEL, ModelTemplates.PISTON_HEAD.createWithSuffix(base, "_head", regularHeadTextureMap, generator.modelOutput)))
					.select(false, PistonType.STICKY, Variant.variant().with(
						VariantProperties.MODEL, ModelTemplates.PISTON_HEAD.createWithSuffix(base, "_head_sticky", stickyHeadTextureMap, generator.modelOutput)))
					.select(true, PistonType.DEFAULT, Variant.variant().with(
						VariantProperties.MODEL, ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(base, "_head_short", regularHeadTextureMap, generator.modelOutput)))
					.select(true, PistonType.STICKY, Variant.variant().with(
						VariantProperties.MODEL, ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(base, "_head_short_sticky", stickyHeadTextureMap, generator.modelOutput)))
			).with(BlockModelGenerators.createFacingDispatch()));
		}

		if (pistonArm != null) {
			TextureMapping armTextureMap = new TextureMapping().put(TextureSlot.TEXTURE, sideTextureId);

			generator.blockStateOutput.accept((MultiVariantGenerator.multiVariant(pistonArm).with(
				PropertyDispatch.property(BlockStateProperties.SHORT)
					.select(false, Variant.variant().with(
						VariantProperties.MODEL, TEMPLATE_PISTON_ARM.createWithSuffix(base, "_arm", armTextureMap, generator.modelOutput)))
					.select(true, Variant.variant().with(
						VariantProperties.MODEL, TEMPLATE_PISTON_ARM_SHORT.createWithSuffix(base, "_arm_short", armTextureMap, generator.modelOutput)))
			).with(BlockModelGenerators.createFacingDispatch())));
		}

		if (movingPiston != null) {
			TextureMapping movingPistonTextureMap = new TextureMapping().put(TextureSlot.PARTICLE, sideTextureId);

			generator.createTrivialBlock(movingPiston, movingPistonTextureMap, TEMPLATE_MOVING_PISTON);
		}
	}

	public static PropertyDispatch createLitFacingBlockState(ResourceLocation offModelId, ResourceLocation onModelId) {
		return PropertyDispatch
			.properties(BlockStateProperties.FACING, BlockStateProperties.LIT)
			.select(Direction.NORTH, false,
				Variant.variant()
					.with(VariantProperties.MODEL, offModelId)
					.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
			.select(Direction.SOUTH, false,
				Variant.variant()
					.with(VariantProperties.MODEL, offModelId)
					.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
					.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
			.select(Direction.EAST, false,
				Variant.variant()
					.with(VariantProperties.MODEL, offModelId)
					.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
					.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
			.select(Direction.WEST, false,
				Variant.variant()
					.with(VariantProperties.MODEL, offModelId)
					.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
					.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
			.select(Direction.DOWN, false,
				Variant.variant()
					.with(VariantProperties.MODEL, offModelId)
					.with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
			.select(Direction.UP, false,
				Variant.variant()
					.with(VariantProperties.MODEL, offModelId))
			.select(Direction.NORTH, true,
				Variant.variant()
					.with(VariantProperties.MODEL, onModelId)
					.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
			.select(Direction.SOUTH, true,
				Variant.variant()
					.with(VariantProperties.MODEL, onModelId)
					.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
					.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
			.select(Direction.EAST, true,
				Variant.variant()
					.with(VariantProperties.MODEL, onModelId)
					.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
					.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
			.select(Direction.WEST, true,
				Variant.variant()
					.with(VariantProperties.MODEL, onModelId)
					.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
					.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
			.select(Direction.DOWN, true,
				Variant.variant()
					.with(VariantProperties.MODEL, onModelId)
					.with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
			.select(Direction.UP, true,
				Variant.variant()
				    .with(VariantProperties.MODEL, onModelId));
	}
}
