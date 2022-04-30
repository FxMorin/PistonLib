package ca.fxco.configurablepistons.renderers;

import ca.fxco.configurablepistons.base.BasicPistonBlock;
import ca.fxco.configurablepistons.base.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.base.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.datagen.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@Environment(value=EnvType.CLIENT)
public class BasicPistonBlockEntityRenderer<T extends BasicPistonBlockEntity> implements BlockEntityRenderer<T> {
    private final BlockRenderManager manager;

    public BasicPistonBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.manager = ctx.getRenderManager();
    }

    @Override
    public void render(T pistonBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        World world = pistonBlockEntity.getWorld();
        if (world == null) return;
        BlockPos blockPos = pistonBlockEntity.getPos().offset(pistonBlockEntity.getMovementDirection().getOpposite());
        BlockState blockState = pistonBlockEntity.getPushedBlock();
        if (blockState.isAir()) return;
        BlockModelRenderer.enableBrightnessCache();
        matrixStack.push();
        matrixStack.translate(pistonBlockEntity.getRenderOffsetX(f), pistonBlockEntity.getRenderOffsetY(f), pistonBlockEntity.getRenderOffsetZ(f));
        if (blockState.getBlock() instanceof BasicPistonHeadBlock && pistonBlockEntity.getProgress(f) <= 4.0f) {
            blockState = blockState.with(BasicPistonHeadBlock.SHORT, pistonBlockEntity.getProgress(f) <= 0.5f);
            this.renderModel(blockPos, blockState, matrixStack, vertexConsumerProvider, world, false, j);
        } else if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) { // TODO: Fix this hacky mess, need a call to piston family here
            System.out.println(blockState);
            PistonType pistonType = blockState.getBlock().getName().toString().toLowerCase().contains("sticky") ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockState2 = ModBlocks.BASIC_PISTON_HEAD.getDefaultState()
                    .with(BasicPistonHeadBlock.TYPE, pistonType)
                    .with(BasicPistonHeadBlock.FACING, blockState.get(BasicPistonBlock.FACING));
            blockState2 = blockState2.with(BasicPistonHeadBlock.SHORT, pistonBlockEntity.getProgress(f) >= 0.5f);
            this.renderModel(blockPos, blockState2, matrixStack, vertexConsumerProvider, world, false, j);
            BlockPos blockPos2 = blockPos.offset(pistonBlockEntity.getMovementDirection());
            matrixStack.pop();
            matrixStack.push();
            blockState = blockState.with(BasicPistonBlock.EXTENDED, true);
            this.renderModel(blockPos2, blockState, matrixStack, vertexConsumerProvider, world, true, j);
        } else {
            this.renderModel(blockPos, blockState, matrixStack, vertexConsumerProvider, world, false, j);
        }
        matrixStack.pop();
        BlockModelRenderer.disableBrightnessCache();
    }

    private void renderModel(BlockPos pos, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, boolean cull, int overlay) {
        RenderLayer renderLayer = RenderLayers.getMovingBlockLayer(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        this.manager.getModelRenderer().render(world, this.manager.getModel(state), state, pos, matrices, vertexConsumer, cull, new Random(), state.getRenderingSeed(pos), overlay);
    }

    @Override
    public int getRenderDistance() {
        return 68;
    }
}
