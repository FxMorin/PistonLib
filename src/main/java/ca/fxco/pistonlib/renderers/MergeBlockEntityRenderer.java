package ca.fxco.pistonlib.renderers;

import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class MergeBlockEntityRenderer<T extends MergeBlockEntity> implements BlockEntityRenderer<T> {

    protected final BlockRenderDispatcher blockRenderer;

    public MergeBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.blockRenderer = ctx.getBlockRenderDispatcher();
    }

    @Override
    public void render(T mbe, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        Level level = mbe.getLevel();

        if (level == null) {
            return;
        }

        BlockPos pos = mbe.getBlockPos();

        this.renderBlock(pos, mbe.getInitialState(), stack, bufferSource, level, true, overlay);

        for (Map.Entry<Direction, MergeBlockEntity.MergeData> entry : mbe.getMergingBlocks().entrySet()) {
            Direction dir = entry.getKey();
            MergeBlockEntity.MergeData data = entry.getValue();
            float progress = data.getProgress();
            float lastProgress = data.getLastProgress();

            stack.pushPose();
            stack.translate(
                    mbe.getXOff(dir, partialTick, progress, lastProgress),
                    mbe.getYOff(dir, partialTick, progress, lastProgress),
                    mbe.getZOff(dir, partialTick, progress, lastProgress)
            );

            Direction moveDir = dir.getOpposite();
            BlockPos fromPos = pos.relative(moveDir);

            this.renderBlock(fromPos, data.getState(), stack, bufferSource, level, false, overlay);

            stack.popPose();
        }
    }

    protected void renderBlock(BlockPos pos, BlockState state, PoseStack stack, MultiBufferSource bufferSource, Level level,
                               boolean cull, int overlay) {
        RenderType type = ItemBlockRenderTypes.getMovingBlockRenderType(state);
        VertexConsumer consumer = bufferSource.getBuffer(type);

        this.blockRenderer.getModelRenderer().tesselateBlock(level, this.blockRenderer.getBlockModel(state), state, pos, stack,
            consumer, cull, RandomSource.create(), state.getSeed(pos), overlay);
    }

    

    @Override
    public int getViewDistance() {
        return 68;
    }
}
