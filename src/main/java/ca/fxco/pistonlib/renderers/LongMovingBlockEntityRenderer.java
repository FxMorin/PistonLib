package ca.fxco.pistonlib.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.pistonlib.blocks.pistons.longPiston.LongMovingBlockEntity;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

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
                PistonFamily family = mbe.getFamily();
                Direction facing = state.getValue(BasicPistonBaseBlock.FACING);

                BlockState renderState;

                if (mbe.isArm()) {
                    renderState = family.getArm().defaultBlockState()
                        .setValue(BasicPistonHeadBlock.FACING, facing);
                } else {
                    renderState = family.getHead().defaultBlockState()
                        .setValue(BasicPistonHeadBlock.FACING, facing)
                        .setValue(BasicPistonHeadBlock.SHORT, mbe.getProgress(partialTick) >= 0.5F);
                }

                this.renderBlock(fromPos, renderState, stack, bufferSource, level, false, overlay);
            }
        }
    }
}
