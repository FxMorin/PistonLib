package ca.fxco.configurablepistons.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongMovingBlockEntity;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

@Environment(EnvType.CLIENT)
public class LongMovingBlockEntityRenderer<T extends LongMovingBlockEntity> extends BasicMovingBlockEntityRenderer<T> {

    public LongMovingBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void renderMovingSource(T mbe, Level level, BlockPos fromPos, BlockPos toPos, float partialTick, PoseStack stack,
                                      MultiBufferSource bufferSource, int light, int overlay) {
        BlockState state = mbe.getMovedState();

        if (mbe.isExtending()) {
            if (state.getBlock() instanceof BasicPistonHeadBlock) {
                this.renderBlock(fromPos, state.setValue(BasicPistonHeadBlock.SHORT, mbe.getProgress(partialTick) <= 0.5F), stack,
                    bufferSource, level, false, overlay);
            }
        } else {
            if (state.getBlock() instanceof BasicPistonBaseBlock base) {
                PistonType type = base.type;
                Direction facing = state.getValue(BasicPistonBaseBlock.FACING);

                BlockState renderState;

                if (mbe.isArm()) {
                    renderState = PistonFamilies.LONG.getArmBlock().defaultBlockState()
                        .setValue(BasicPistonHeadBlock.FACING, facing);
                } else {
                    renderState = base.getHeadBlock().defaultBlockState()
                        .setValue(BasicPistonHeadBlock.TYPE, type)
                        .setValue(BasicPistonHeadBlock.FACING, facing)
                        .setValue(BasicPistonHeadBlock.SHORT, mbe.getProgress(partialTick) >= 0.5F);
                }

                this.renderBlock(fromPos, renderState, stack, bufferSource, level, false, overlay);
            }
        }
    }
}
