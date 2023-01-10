package ca.fxco.pistonlib.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import ca.fxco.pistonlib.blocks.pistons.movableBlockEntities.MBEMovingBlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(EnvType.CLIENT)
public class MBEMovingBlockEntityRenderer<T extends MBEMovingBlockEntity> extends BasicMovingBlockEntityRenderer<T> {

    protected final BlockEntityRenderDispatcher blockEntityRenderer;

    public MBEMovingBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);

        this.blockEntityRenderer = ctx.getBlockEntityRenderDispatcher();
    }

    @Override
    protected void renderMovingBlock(T mbe, Level level, BlockPos fromPos, BlockPos toPos, float partialTick, PoseStack stack,
                                     MultiBufferSource bufferSource, int light, int overlay) {
        super.renderMovingBlock(mbe, level, fromPos, toPos, partialTick, stack, bufferSource, light, overlay);

        BlockEntity blockEntity = mbe.getMovedBlockEntity();

        if (blockEntity != null) {
            this.renderBlockEntity(blockEntity, partialTick, stack, bufferSource);
        }
    }

    protected void renderBlockEntity(BlockEntity blockEntity, float partialTick, PoseStack stack, MultiBufferSource bufferSource) {
        this.blockEntityRenderer.render(blockEntity, partialTick, stack, bufferSource);
    }
}
