package ca.fxco.pistonlib.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BlockAndTintWrapper implements BlockAndTintGetter {

    private final BlockAndTintGetter blockAndTintGetter;

    public BlockAndTintWrapper(BlockAndTintGetter blockAndTintGetter) {
        this.blockAndTintGetter = blockAndTintGetter;
    }

    @Override
    public float getShade(Direction direction, boolean bl) {
        return this.blockAndTintGetter.getShade(direction, bl);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.blockAndTintGetter.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return this.blockAndTintGetter.getBlockTint(blockPos, colorResolver);
    }

    @Override
    public int getBrightness(LightLayer lightLayer, BlockPos blockPos) {
        return this.blockAndTintGetter.getBrightness(lightLayer, blockPos);
    }

    @Override
    public int getRawBrightness(BlockPos blockPos, int i) {
        return this.blockAndTintGetter.getRawBrightness(blockPos, i);
    }

    @Override
    public boolean canSeeSky(BlockPos blockPos) {
        return this.blockAndTintGetter.canSeeSky(blockPos);
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        return this.blockAndTintGetter.getBlockEntity(blockPos);
    }

    @Override
    public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos blockPos, BlockEntityType<T> blockEntityType) {
        return this.blockAndTintGetter.getBlockEntity(blockPos, blockEntityType);
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        return this.blockAndTintGetter.getBlockState(blockPos);
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return this.blockAndTintGetter.getFluidState(blockPos);
    }

    @Override
    public int getLightEmission(BlockPos blockPos) {
        return this.blockAndTintGetter.getLightEmission(blockPos);
    }

    @Override
    public int getMaxLightLevel() {
        return this.blockAndTintGetter.getMaxLightLevel();
    }

    @Override
    public Stream<BlockState> getBlockStates(AABB aABB) {
        return this.blockAndTintGetter.getBlockStates(aABB);
    }

    @Override
    public BlockHitResult isBlockInLine(ClipBlockStateContext clipBlockStateContext) {
        return this.blockAndTintGetter.isBlockInLine(clipBlockStateContext);
    }

    @Override
    public BlockHitResult clip(ClipContext clipContext) {
        return this.blockAndTintGetter.clip(clipContext);
    }

    @Nullable
    @Override
    public BlockHitResult clipWithInteractionOverride(Vec3 vec3, Vec3 vec32, BlockPos blockPos, VoxelShape voxelShape, BlockState blockState) {
        return this.blockAndTintGetter.clipWithInteractionOverride(vec3, vec32, blockPos, voxelShape, blockState);
    }

    @Override
    public double getBlockFloorHeight(VoxelShape voxelShape, Supplier<VoxelShape> supplier) {
        return this.blockAndTintGetter.getBlockFloorHeight(voxelShape, supplier);
    }

    @Override
    public double getBlockFloorHeight(BlockPos blockPos) {
        return this.blockAndTintGetter.getBlockFloorHeight(blockPos);
    }

    @Override
    public int getHeight() {
        return this.blockAndTintGetter.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return this.blockAndTintGetter.getMinBuildHeight();
    }

    @Override
    public int getMaxBuildHeight() {
        return this.blockAndTintGetter.getMaxBuildHeight();
    }

    @Override
    public int getSectionsCount() {
        return this.blockAndTintGetter.getSectionsCount();
    }

    @Override
    public int getMinSection() {
        return this.blockAndTintGetter.getMinSection();
    }

    @Override
    public int getMaxSection() {
        return this.blockAndTintGetter.getMaxSection();
    }

    @Override
    public boolean isOutsideBuildHeight(BlockPos blockPos) {
        return this.blockAndTintGetter.isOutsideBuildHeight(blockPos);
    }

    @Override
    public boolean isOutsideBuildHeight(int i) {
        return this.blockAndTintGetter.isOutsideBuildHeight(i);
    }

    @Override
    public int getSectionIndex(int i) {
        return this.blockAndTintGetter.getSectionIndex(i);
    }

    @Override
    public int getSectionIndexFromSectionY(int i) {
        return this.blockAndTintGetter.getSectionIndexFromSectionY(i);
    }

    @Override
    public int getSectionYFromSectionIndex(int i) {
        return this.blockAndTintGetter.getSectionYFromSectionIndex(i);
    }
}
