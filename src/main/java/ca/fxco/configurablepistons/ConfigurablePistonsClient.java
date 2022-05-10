package ca.fxco.configurablepistons;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.renderers.BasicPistonBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

public class ConfigurablePistonsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        register(ModBlockEntities.BASIC_PISTON_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        register(ModBlockEntities.SPEED_PISTON_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        register(ModBlockEntities.FAST_PISTON_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        register(ModBlockEntities.STICKY_PISTON_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SLIPPERY_SLIME_BLOCK, RenderLayer.getTranslucent());
    }

    public static <T extends BlockEntity> void register(BlockEntityType<T> blockEntityType,
                                                        BlockEntityRendererFactory<T> blockEntityRendererFactory) {
        BlockEntityRendererRegistry.register(blockEntityType, blockEntityRendererFactory);
    }
}
