package ca.fxco.pistonlib.datagen;

import java.util.Map;
import java.util.Optional;

import ca.fxco.pistonlib.base.ModItems;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.base.ModRegistries;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;

import static net.minecraft.data.models.BlockModelGenerators.*;

public class ModModelProvider extends FabricModelProvider {

    public static final Logger LOGGER = PistonLib.LOGGER;

	public static final ModelTemplate TEMPLATE_PISTON_ARM = new ModelTemplate(Optional.of(PistonLib.id("block/template_piston_arm")), Optional.empty(), TextureSlot.TEXTURE);
	public static final ModelTemplate TEMPLATE_PISTON_ARM_SHORT = new ModelTemplate(Optional.of(PistonLib.id("block/template_piston_arm_short")), Optional.empty(), TextureSlot.TEXTURE);
	public static final ModelTemplate TEMPLATE_PARTICLE_ONLY = new ModelTemplate(Optional.of(PistonLib.id("block/template_empty")), Optional.empty(), TextureSlot.PARTICLE);
	public static final ModelTemplate TEMPLATE_HALF_BLOCK = new ModelTemplate(Optional.of(PistonLib.id("block/template_half_block")), Optional.empty(), TextureSlot.TOP, TextureSlot.SIDE);
	public static final ModelTemplate PISTON_BASE = new ModelTemplate(Optional.of(new ResourceLocation("block/piston_extended")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.INSIDE);

	public ModModelProvider(FabricDataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generator) {
		LOGGER.info("Generating blockstate definitions and models...");

		for (Map.Entry<ResourceKey<PistonFamily>, PistonFamily> entry : ModRegistries.PISTON_FAMILY.entrySet()) {
            ResourceKey<PistonFamily> key = entry.getKey();
            PistonFamily family = entry.getValue();

            LOGGER.info("Generating blockstate definitions and models for piston family " + key.location()+"...");

            registerPistonFamily(generator, family);
        }

		LOGGER.info("Finished generating blockstate definitions and models for pistons, generating for other blocks...");

		registerHalfBlock(generator, ModBlocks.HALF_OBSIDIAN_BLOCK, Blocks.OBSIDIAN);
		registerHalfBlock(generator, ModBlocks.HALF_REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);
		registerBlockWithCustomModel(generator, ModBlocks.HALF_HONEY_BLOCK);
		registerBlockWithCustomModel(generator, ModBlocks.HALF_SLIME_BLOCK);

		registerBlockWithCustomStates(generator, ModBlocks.HALF_REDSTONE_LAMP_BLOCK,
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
		generator.createTrivialCube(ModBlocks.WEAK_REDSTONE_BLOCK);
		generator.createTrivialCube(ModBlocks.AUTO_CRAFTING_BLOCK);
		generator.createTrivialCube(ModBlocks.QUASI_BLOCK);
		generator.createTrivialCube(ModBlocks.ERASE_BLOCK);
		generator.createTrivialCube(ModBlocks.HEAVY_BLOCK);

		registerSlab(generator, Blocks.OBSIDIAN, ModBlocks.OBSIDIAN_SLAB_BLOCK);
		registerStair(generator, Blocks.OBSIDIAN, ModBlocks.OBSIDIAN_STAIR_BLOCK);

		generator.createTrivialBlock(ModBlocks.STICKY_TOP_BLOCK, new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.DEEPSLATE_BRICKS)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(ModBlocks.STICKY_TOP_BLOCK)), ModelTemplates.CUBE_TOP);

		generator.blockStateOutput.accept(createSimpleBlock(ModBlocks.SLIMY_REDSTONE_BLOCK, ModelLocationUtils.getModelLocation(ModBlocks.SLIMY_REDSTONE_BLOCK)));
		generator.blockStateOutput.accept(createSimpleBlock(ModBlocks.SLIPPERY_SLIME_BLOCK, ModelLocationUtils.getModelLocation(ModBlocks.SLIPPERY_SLIME_BLOCK)));

		registerPoweredBlock(generator, ModBlocks.ALL_SIDED_OBSERVER);

		generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.POWERED_STICKY_BLOCK).with(
				PropertyDispatch.property(BlockStateProperties.POWERED)
						.select(false, Variant.variant().with(
								VariantProperties.MODEL, ModelLocationUtils.getModelLocation(ModBlocks.POWERED_STICKY_BLOCK)))
						.select(true, Variant.variant().with(
								VariantProperties.MODEL, ModelLocationUtils.getModelLocation(ModBlocks.POWERED_STICKY_BLOCK, "_on")))
		).with(BlockModelGenerators.createFacingDispatch()));

		generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.STICKY_CHAIN_BLOCK, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(ModBlocks.STICKY_CHAIN_BLOCK))).with(BlockModelGenerators.createRotatedPillar()));
		generator.createSimpleFlatItemModel(ModBlocks.STICKY_CHAIN_BLOCK.asItem());

		TextureMapping particleOnlyTextureMap = new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.PISTON, "_side")); //TODO
		generator.createTrivialBlock(ModBlocks.MERGE_BLOCK, particleOnlyTextureMap, TEMPLATE_PARTICLE_ONLY);

		LOGGER.info("Finished generating blockstate definitions and models!");
	}

	@Override
	public void generateItemModels(ItemModelGenerators generator) {
		generator.generateFlatItem(ModItems.PISTON_WAND, ModelTemplates.FLAT_ITEM);
	}

	public static void registerCubeTextureMap(BlockModelGenerators generator, Block block,
											  ResourceLocation baseTexture, @Nullable String suffix) {
		TextureMapping halfBlockTextureMap = new TextureMapping().put(TextureSlot.ALL, baseTexture);
		if (suffix == null) {
			ModelTemplates.CUBE_ALL.create(block, halfBlockTextureMap, generator.modelOutput);
		} else {
			ModelTemplates.CUBE_ALL.createWithSuffix(block, suffix, halfBlockTextureMap, generator.modelOutput);
		}
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

	public static void registerBlockWithCustomModel(BlockModelGenerators generator, Block halfBlock) {
		registerHalfBlock(generator, halfBlock, null);
	}

	public static void registerBlockWithCustomStates(BlockModelGenerators generator, Block halfBlock, PropertyDispatch customStates) {
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
		Block textureBaseBlock = customTextures ? family.getBase() : Blocks.PISTON;

		Block base = family.getBase();
		Block normalBase = family.getBase(PistonType.DEFAULT);
		Block stickyBase = family.getBase(PistonType.STICKY);
		Block arm = family.getArm();
		Block head = family.getHead();
		Block moving = family.getMoving();

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

		if (head != null) {
			TextureMapping baseHeadTextureMap = new TextureMapping().put(TextureSlot.UNSTICKY, topRegularTextureId).put(TextureSlot.SIDE, sideTextureId);
			TextureMapping regularHeadTextureMap = baseHeadTextureMap.copyAndUpdate(TextureSlot.PLATFORM, topRegularTextureId);
			TextureMapping stickyHeadTextureMap = baseHeadTextureMap.copyAndUpdate(TextureSlot.PLATFORM, topStickyTextureId);

			generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(head).with(
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

		if (arm != null) {
			TextureMapping armTextureMap = new TextureMapping().put(TextureSlot.TEXTURE, sideTextureId);

			generator.blockStateOutput.accept((MultiVariantGenerator.multiVariant(arm).with(
					PropertyDispatch.property(BlockStateProperties.SHORT)
							.select(false, Variant.variant().with(
									VariantProperties.MODEL, TEMPLATE_PISTON_ARM.createWithSuffix(base, "_arm", armTextureMap, generator.modelOutput)))
							.select(true, Variant.variant().with(
									VariantProperties.MODEL, TEMPLATE_PISTON_ARM_SHORT.createWithSuffix(base, "_arm_short", armTextureMap, generator.modelOutput)))
			).with(BlockModelGenerators.createFacingDispatch())));
		}

		if (moving != null) {
			TextureMapping movingPistonTextureMap = new TextureMapping().put(TextureSlot.PARTICLE, sideTextureId);

			generator.createTrivialBlock(moving, movingPistonTextureMap, TEMPLATE_PARTICLE_ONLY);
		}
	}

	public static void registerPoweredBlock(BlockModelGenerators generator, Block block) {
		ResourceLocation powerOff = ModelLocationUtils.getModelLocation(block);
		ResourceLocation powerOn = ModelLocationUtils.getModelLocation(block, "_on");
		registerBlockWithCustomStates(generator, block,
				PropertyDispatch.property(BlockStateProperties.POWERED)
						.select(false, Variant.variant().with(VariantProperties.MODEL, powerOff))
						.select(true, Variant.variant().with(VariantProperties.MODEL, powerOn)));
		registerCubeTextureMap(generator, block, powerOff, null);
		registerCubeTextureMap(generator, block, powerOn, "_on");
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
