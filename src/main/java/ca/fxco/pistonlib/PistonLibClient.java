package ca.fxco.pistonlib;

import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.network.PistonLibNetworkConstants;
import ca.fxco.pistonlib.pistonLogic.structureRunners.DecoupledStructureRunner;
import ca.fxco.pistonlib.pistonLogic.structureRunners.StructureRunner;
import ca.fxco.pistonlib.renderers.BasicMovingBlockEntityRenderer;
import ca.fxco.pistonlib.renderers.LongMovingBlockEntityRenderer;
import ca.fxco.pistonlib.renderers.MBEMovingBlockEntityRenderer;
import ca.fxco.pistonlib.renderers.MergeBlockEntityRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;

public class PistonLibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Registering Piston Moving Block Entities
        BlockEntityRenderers.register(ModBlockEntities.BASIC_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.CONFIGURABLE_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.LONG_MOVING_BLOCK_ENTITY, LongMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.SPEED_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.FAST_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.TRANSLOCATION_MOVING_BLOCK_ENTITY, BasicMovingBlockEntityRenderer::new);
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

        ClientPlayNetworking.registerGlobalReceiver(PistonLibNetworkConstants.PISTON_EVENT_PACKET_ID, (client, handler, buf, responseSender) -> {
            BasicPistonBaseBlock pistonBlock = (BasicPistonBaseBlock) buf.readById(BuiltInRegistries.BLOCK);
            BlockPos pos = buf.readBlockPos();
            Direction dir = Direction.values()[buf.readByte()];
            boolean extend = buf.readBoolean();
            client.execute(() -> {
                StructureRunner structureRunner = new DecoupledStructureRunner(pistonBlock.newStructureRunner());
                structureRunner.run(client.level, pos, dir, extend, pistonBlock::newStructureResolver);
            });
        });
    }
}
