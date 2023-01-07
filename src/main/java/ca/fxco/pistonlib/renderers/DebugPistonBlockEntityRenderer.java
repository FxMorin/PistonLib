package ca.fxco.pistonlib.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class DebugPistonBlockEntityRenderer<T extends BasicMovingBlockEntity> extends BasicPistonBlockEntityRenderer<T> {

    public DebugPistonBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(T mbe, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        super.render(mbe, partialTick, stack, bufferSource, light, overlay);

        Level level = mbe.getLevel();
        BlockPos pos = mbe.getBlockPos();
        BlockState state = Blocks.LIME_STAINED_GLASS.defaultBlockState();
        RenderType type = RenderType.translucentMovingBlock();
        VertexConsumer consumer = bufferSource.getBuffer(type);

        this.blockRenderer.renderBatched(state, pos, level, stack, consumer, false, RandomSource.create());
    }
}
