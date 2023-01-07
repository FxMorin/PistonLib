package ca.fxco.configurablepistons.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.pistonLogic.PistonUtils;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurablePistonStructureResolver;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlock_tagsMixin {

    /*
     * In this mixin we basically change all the !state.isOf(PISTON) and regular piston to instead check against the
     * PISTONS TagKey, so that all pistons work the same with each other (Including vanilla)
     */


    @Shadow
    @Final
    private boolean isSticky;

    @Redirect(
        method = "triggerEvent(Lnet/minecraft/world/level/block/state/BlockState;" +
                 "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;isPushable(Lnet/minecraft/world/level/block/state/BlockState;" +
                     "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;" +
                     "Lnet/minecraft/core/Direction;ZLnet/minecraft/core/Direction;)Z"
        )
    )
    private boolean modifyIsMovable(BlockState state, Level level, BlockPos pos,
                                    Direction moveDir, boolean allowDestroy, Direction pistonFacing) {
        return PistonUtils.isMovable(state, level, pos, moveDir, allowDestroy, pistonFacing);
    }

    @Redirect(
        method = "triggerEvent(Lnet/minecraft/world/level/block/state/BlockState;" +
                 "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
            ordinal = 1
        )
    )
    public boolean allPistons(BlockState state, Block block) {
        return state.is(ModTags.PISTONS);
    }

    @Redirect(
        method = "triggerEvent(Lnet/minecraft/world/level/block/state/BlockState;" +
                 "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
            ordinal = 2
       )
    )
    public boolean skipIsPistonCheck(BlockState state, Block block) {
        return false;
    }

    // Had to replace it completely due to mixin limitations
    @Redirect(
        require = 1,
        method = "triggerEvent(Lnet/minecraft/world/level/block/state/BlockState;" +
                 "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;moveBlocks(Lnet/minecraft/world/level/Level;" +
                     "Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Z"
        )
    )
    private boolean customMoveBlock(PistonBaseBlock base, Level world, BlockPos pos, Direction facing, boolean extend) {
        BlockPos blockPos = pos.relative(facing);
        if (!extend && world.getBlockState(blockPos).is(Blocks.PISTON_HEAD))
            world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_IMMEDIATE | Block.UPDATE_KNOWN_SHAPE);
        ConfigurablePistonStructureResolver pistonHandler = new ConfigurablePistonStructureResolver(world, pos, facing, extend);
        if (!pistonHandler.resolve(!extend)) return false;
        Map<BlockPos, BlockState> map = new HashMap<>();
        List<BlockPos> list = pistonHandler.getToMove();
        List<BlockState> list2 = new ArrayList<>();
        for (BlockPos value : list) {
            BlockState state = world.getBlockState(value);
            list2.add(state);
            map.put(value, state);
        }
        List<BlockPos> list3 = pistonHandler.getToDestroy();
        BlockState[] blockStates = new BlockState[list.size() + list3.size()];
        Direction direction = extend ? facing : facing.getOpposite();
        int j = 0, k;
        BlockPos pos3;
        BlockState state2;
        for(k = list3.size() - 1; k >= 0; --k) {
            pos3 = list3.get(k);
            state2 = world.getBlockState(pos3);
            BlockEntity blockEntity = state2.hasBlockEntity() ? world.getBlockEntity(pos3) : null;
            Block.dropResources(state2, world, pos3, blockEntity);
            world.setBlock(pos3, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            if (!state2.is(BlockTags.FIRE)) world.addDestroyBlockEffect(pos3, state2);
            blockStates[j++] = state2;
        }
        for(k = list.size() - 1; k >= 0; --k) {
            pos3 = list.get(k);
            state2 = world.getBlockState(pos3);
            pos3 = pos3.relative(direction);
            map.remove(pos3);
            BlockState state3 = Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, facing);
            world.setBlock(pos3, state3, Block.UPDATE_IMMEDIATE | Block.UPDATE_MOVE_BY_PISTON);
            world.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(
                    pos3, state3, list2.get(k), facing, extend, false));
            blockStates[j++] = state2;
        }
        if (extend) {
            PistonType pistonType = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState state4 = Blocks.PISTON_HEAD.defaultBlockState()
                    .setValue(PistonHeadBlock.FACING, facing)
                    .setValue(PistonHeadBlock.TYPE, pistonType);
            state2 = Blocks.MOVING_PISTON.defaultBlockState()
                    .setValue(MovingPistonBlock.FACING, facing)
                    .setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            map.remove(blockPos);
            world.setBlock(blockPos, state2, Block.UPDATE_IMMEDIATE | Block.UPDATE_MOVE_BY_PISTON);
            world.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(
                    blockPos, state2, state4, facing, true, true));
        }
        BlockState blockState5 = Blocks.AIR.defaultBlockState();
        for (BlockPos blockPos4 : map.keySet())
            world.setBlock(blockPos4, blockState5, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_MOVE_BY_PISTON);
        BlockPos blockPos5;
        for (Map.Entry<BlockPos, BlockState> entry : map.entrySet()) {
            blockPos5 = entry.getKey();
            entry.getValue().updateIndirectNeighbourShapes(world, blockPos5, 2);
            blockState5.updateNeighbourShapes(world, blockPos5, Block.UPDATE_CLIENTS);
            blockState5.updateIndirectNeighbourShapes(world, blockPos5, 2);
        }
        j = 0;
        for(k = list3.size() - 1; k >= 0; --k) {
            state2 = blockStates[j++];
            blockPos5 = list3.get(k);
            state2.updateIndirectNeighbourShapes(world, blockPos5, 2);
            world.updateNeighborsAt(blockPos5, state2.getBlock());
        }
        for(k = list.size() - 1; k >= 0; --k) world.updateNeighborsAt(list.get(k), blockStates[j++].getBlock());
        if (extend) world.updateNeighborsAt(blockPos, Blocks.PISTON_HEAD);
        return true;
    }
}
