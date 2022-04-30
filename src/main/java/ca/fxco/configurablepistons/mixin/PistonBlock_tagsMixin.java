package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.helpers.ConfigurablePistonHandler;
import ca.fxco.configurablepistons.helpers.PistonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Map;

import static net.minecraft.block.Block.dropStacks;
import static net.minecraft.state.property.Properties.FACING;

@Mixin(PistonBlock.class)
public class PistonBlock_tagsMixin {

    /*
     * In this mixin we basically change all the !state.isOf(PISTON) and regular piston to instead check against the
     * PISTONS TagKey, so that all pistons work the same with each other (Including vanilla)
     */


    @Shadow
    @Final
    private boolean sticky;


    @Redirect(
            method = "onSyncedBlockEvent(Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/PistonBlock;isMovable(Lnet/minecraft/block/BlockState;" +
                            "Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;" +
                            "Lnet/minecraft/util/math/Direction;ZLnet/minecraft/util/math/Direction;)Z"
            )
    )
    private boolean modifyIsMovable(BlockState state, World world, BlockPos pos,
                                    Direction direction, boolean canBreak, Direction pistonDir) {
        return PistonUtils.isMovable(state, world, pos, direction, canBreak, pistonDir);
    }


    @Redirect(
            method = "onSyncedBlockEvent(Lnet/minecraft/block/BlockState;" +
                "Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",
                    ordinal = 1
            )
    )
    public boolean ifItsAPiston(BlockState state, Block block) {
        return state.isIn(ModTags.PISTONS);
    }


    @Redirect(
            method = "onSyncedBlockEvent(Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",
                    ordinal = 2
            )
    )
    public boolean skipThis(BlockState state, Block block) {
        return false;
    }


    // Had to replace it completely due to mixin limitations
    @Redirect(
            require = 1,
            method = "onSyncedBlockEvent(Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/PistonBlock;move(Lnet/minecraft/world/World;" +
                            "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Z"
            )
    )
    private boolean customMove(PistonBlock block, World world, BlockPos pos, Direction dir, boolean retract) {
        BlockPos blockPos = pos.offset(dir);
        if (!retract && world.getBlockState(blockPos).isOf(Blocks.PISTON_HEAD))
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NO_REDRAW | Block.FORCE_STATE);
        ConfigurablePistonHandler pistonHandler = new ConfigurablePistonHandler(world, pos, dir, retract);
        if (!pistonHandler.calculatePullPush(!retract)) return false;
        Map<BlockPos, BlockState> map = Maps.newHashMap();
        List<BlockPos> list = pistonHandler.getMovedBlocks();
        List<BlockState> list2 = Lists.newArrayList();
        for (BlockPos value : list) {
            BlockState blockState = world.getBlockState(value);
            list2.add(blockState);
            map.put(value, blockState);
        }
        List<BlockPos> list3 = pistonHandler.getBrokenBlocks();
        BlockState[] blockStates = new BlockState[list.size() + list3.size()];
        Direction direction = retract ? dir : dir.getOpposite();
        int j = 0, k;
        BlockPos blockPos3;
        BlockState blockState2;
        for(k = list3.size() - 1; k >= 0; --k) {
            blockPos3 = list3.get(k);
            blockState2 = world.getBlockState(blockPos3);
            BlockEntity blockEntity = blockState2.hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
            dropStacks(blockState2, world, blockPos3, blockEntity);
            world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
            if (!blockState2.isIn(BlockTags.FIRE)) world.addBlockBreakParticles(blockPos3, blockState2);
            blockStates[j++] = blockState2;
        }
        for(k = list.size() - 1; k >= 0; --k) {
            blockPos3 = list.get(k);
            blockState2 = world.getBlockState(blockPos3);
            blockPos3 = blockPos3.offset(direction);
            map.remove(blockPos3);
            BlockState blockState3 = Blocks.MOVING_PISTON.getDefaultState().with(FACING, dir);
            world.setBlockState(blockPos3, blockState3, Block.NO_REDRAW | Block.MOVED);
            world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(blockPos3, blockState3, list2.get(k), dir, retract, false));
            blockStates[j++] = blockState2;
        }
        if (retract) {
            PistonType pistonType = this.sticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockState4 = Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, dir).with(PistonHeadBlock.TYPE, pistonType);
            blockState2 = Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, dir).with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
            map.remove(blockPos);
            world.setBlockState(blockPos, blockState2, Block.NO_REDRAW | Block.MOVED);
            world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(blockPos, blockState2, blockState4, dir, true, true));
        }
        BlockState blockState5 = Blocks.AIR.getDefaultState();
        for (BlockPos blockPos4 : map.keySet())
            world.setBlockState(blockPos4, blockState5, Block.NOTIFY_LISTENERS | Block.FORCE_STATE | Block.MOVED);
        BlockPos blockPos5;
        for (Map.Entry<BlockPos, BlockState> entry : map.entrySet()) {
            blockPos5 = entry.getKey();
            entry.getValue().prepare(world, blockPos5, 2);
            blockState5.updateNeighbors(world, blockPos5, Block.NOTIFY_LISTENERS);
            blockState5.prepare(world, blockPos5, 2);
        }
        j = 0;
        for(k = list3.size() - 1; k >= 0; --k) {
            blockState2 = blockStates[j++];
            blockPos5 = list3.get(k);
            blockState2.prepare(world, blockPos5, 2);
            world.updateNeighborsAlways(blockPos5, blockState2.getBlock());
        }
        for(k = list.size() - 1; k >= 0; --k) world.updateNeighborsAlways(list.get(k), blockStates[j++].getBlock());
        if (retract) world.updateNeighborsAlways(blockPos, Blocks.PISTON_HEAD);
        return true;
    }
}
