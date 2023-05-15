package ca.fxco.pistonlib;

import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.renderers.BasicMovingBlockEntityRenderer;
import ca.fxco.pistonlib.renderers.MBEMovingBlockEntityRenderer;
import ca.fxco.pistonlib.renderers.MergeBlockEntityRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class PistonLibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Registering Piston Moving Block Entities
        BlockEntityRenderers.register(ModBlockEntities.BASIC_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.CONFIGURABLE_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.SPEED_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.FAST_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.MBE_MOVING_BLOCK_ENTITY, MBEMovingBlockEntityRenderer::new);

        // Registering Other Block Entities
        BlockEntityRenderers.register(ModBlockEntities.MERGE_BLOCK_ENTITY, MergeBlockEntityRenderer::new);

        // Registering Block Render Layers
        BlockRenderLayerMap renderLayers = BlockRenderLayerMap.INSTANCE;
        renderLayers.putBlock(ModBlocks.HALF_SLIME_BLOCK, RenderType.translucent());
        renderLayers.putBlock(ModBlocks.HALF_HONEY_BLOCK, RenderType.translucent());
        renderLayers.putBlock(ModBlocks.SLIMY_REDSTONE_BLOCK, RenderType.translucent());
        renderLayers.putBlock(ModBlocks.SLIPPERY_SLIME_BLOCK, RenderType.translucent());
        renderLayers.putBlock(ModBlocks.STICKY_CHAIN_BLOCK, RenderType.cutoutMipped());
    }
}
