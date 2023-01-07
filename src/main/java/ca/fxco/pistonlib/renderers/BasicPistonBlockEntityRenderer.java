package ca.fxco.pistonlib.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

@Environment(EnvType.CLIENT)
public class BasicPistonBlockEntityRenderer<T extends BasicMovingBlockEntity> implements BlockEntityRenderer<T> {

    protected final BlockRenderDispatcher blockRenderer;

    public BasicPistonBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.blockRenderer = ctx.getBlockRenderDispatcher();
    }

    @Override
    public void render(T mbe, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        Level level = mbe.getLevel();

        if (level == null) {
            return;
        }

        BlockState state = mbe.getMovedState();

        if (state.isAir()) {
            return;
        }

        ModelBlockRenderer.enableCaching();
        stack.pushPose();
        stack.translate(mbe.getXOff(partialTick), mbe.getYOff(partialTick), mbe.getZOff(partialTick));

        Direction moveDir = mbe.getMovementDirection();
        BlockPos toPos = mbe.getBlockPos();
        BlockPos fromPos = toPos.relative(moveDir.getOpposite());

        if (mbe.isSourcePiston()) {
            renderMovingSource(mbe, level, fromPos, toPos, partialTick, stack, bufferSource, light, overlay);
        } else {
            renderMovingBlock(mbe, level, fromPos, toPos, partialTick, stack, bufferSource, light, overlay);
        }

        stack.popPose();
        stack.pushPose();

        if (mbe.isSourcePiston()) {
            renderStaticSource(mbe, level, fromPos, toPos, partialTick, stack, bufferSource, light, overlay);
        } else {
            renderStaticBlock(mbe, level, fromPos, toPos, partialTick, stack, bufferSource, light, overlay);
        }

        stack.popPose();
        ModelBlockRenderer.clearCache();
    }

    protected void renderMovingSource(T mbe, Level level, BlockPos fromPos, BlockPos toPos, float partialTick, PoseStack stack,
                                      MultiBufferSource bufferSource, int light, int overlay) {
        BlockState state = mbe.getMovedState();

        if (mbe.isExtending()) {
            if (state.getBlock() instanceof BasicPistonHeadBlock) {
                renderBlock(fromPos, state.setValue(BasicPistonHeadBlock.SHORT, mbe.getProgress(partialTick) <= 0.5F), stack,
                    bufferSource, level, false, overlay);
            }
        } else {
            if (state.getBlock() instanceof BasicPistonBaseBlock base) {
                PistonType type = base.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
                Direction facing = state.getValue(BasicPistonBaseBlock.FACING);

                BlockState headState = base.getHeadBlock().defaultBlockState()
                    .setValue(BasicPistonHeadBlock.TYPE, type)
                    .setValue(BasicPistonHeadBlock.FACING, facing)
                    .setValue(BasicPistonHeadBlock.SHORT, mbe.getProgress(partialTick) >= 0.5F);

                renderBlock(fromPos, headState, stack, bufferSource, level, false, overlay);
            }
        }
    }

    protected void renderStaticSource(T mbe, Level level, BlockPos fromPos, BlockPos toPos, float partialTick, PoseStack stack,
                                      MultiBufferSource bufferSource, int light, int overlay) {
        if (!mbe.isExtending()) {
            BlockState state = mbe.getMovedState();

            if (state.getBlock() instanceof BasicPistonBaseBlock) {
                renderBlock(fromPos, state.setValue(BasicPistonBaseBlock.EXTENDED, true), stack, bufferSource, level, true, overlay);
            }
        }
    }

    protected void renderMovingBlock(T mbe, Level level, BlockPos fromPos, BlockPos toPos, float partialTick, PoseStack stack,
                                     MultiBufferSource bufferSource, int light, int overlay) {
        BlockState state = mbe.getMovedState();

        if (state.getBlock() instanceof BasicPistonHeadBlock) {
            renderBlock(fromPos, state.setValue(BasicPistonHeadBlock.SHORT, mbe.getProgress(partialTick) <= 0.5F), stack, bufferSource,
                level, false, overlay);
        } else {
            renderBlock(fromPos, state, stack, bufferSource, level, false, overlay);
        }
    }

    protected void renderStaticBlock(T mbe, Level level, BlockPos fromPos, BlockPos toPos, float partialTick, PoseStack stack,
                                     MultiBufferSource bufferSource, int light, int overlay) {

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
