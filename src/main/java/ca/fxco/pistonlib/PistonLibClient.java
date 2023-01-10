package ca.fxco.pistonlib;

import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.renderers.BasicMovingBlockEntityRenderer;
import ca.fxco.pistonlib.renderers.LongMovingBlockEntityRenderer;
import ca.fxco.pistonlib.renderers.MBEMovingBlockEntityRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class PistonLibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(ModBlockEntities.BASIC_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.CONFIGURABLE_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.LONG_MOVING_BLOCK_ENTITY, LongMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.SPEED_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.FAST_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.STICKY_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.TRANSLOCATION_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.SLIPPERY_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.MBE_MOVING_BLOCK_ENTITY, MBEMovingBlockEntityRenderer::new);

        BlockRenderLayerMap renderLayers = BlockRenderLayerMap.INSTANCE;
        renderLayers.putBlock(ModBlocks.HALF_SLIME_BLOCK, RenderType.translucent());
        renderLayers.putBlock(ModBlocks.HALF_HONEY_BLOCK, RenderType.translucent());
        renderLayers.putBlock(ModBlocks.SLIMY_REDSTONE_BLOCK, RenderType.translucent());
        renderLayers.putBlock(ModBlocks.SLIPPERY_SLIME_BLOCK, RenderType.translucent());
        renderLayers.putBlock(ModBlocks.STICKY_CHAIN_BLOCK, RenderType.cutoutMipped());
    }
}
