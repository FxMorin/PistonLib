package ca.fxco.pistonlib.mixin.entity;

import ca.fxco.pistonlib.helpers.BlockPosUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class Entity_pushIntoMixin {

    @Shadow
    private Vec3 collide(Vec3 vec3) { return null; }

    @Redirect(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)" +
                            "Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 checkPushInto(Entity instance, Vec3 vec3, MoverType moverType) {
        if (moverType == MoverType.PISTON && !instance.getLevel().isClientSide) {
            // This looks kinda scary, although it only checks max 25 blocks.
            // Honestly it's not very expensive compared to a lot of the other movement logic which runs every tick
            AABB aabb = instance.getBoundingBox();
            if (aabb.getXsize() <= 4.0 && aabb.getYsize() <= 4.0 && aabb.getZsize() <= 4.0) {
                BlockPos min = new BlockPos(aabb.minX + 1.0E-7, aabb.minY + 1.0E-7, aabb.minZ + 1.0E-7);
                BlockPos max = new BlockPos(aabb.maxX - 1.0E-7, aabb.maxY - 1.0E-7, aabb.maxZ - 1.0E-7);
                aabb = aabb.move(vec3);
                BlockPos blockPos = new BlockPos(aabb.minX + 1.0E-7, aabb.minY + 1.0E-7, aabb.minZ + 1.0E-7);
                BlockPos blockPos2 = new BlockPos(aabb.maxX - 1.0E-7, aabb.maxY - 1.0E-7, aabb.maxZ - 1.0E-7);
                for (BlockPos pos : BlockPos.betweenClosed(blockPos, blockPos2)) {
                    if (BlockPosUtils.isNotWithin(pos, min, max)) {
                        BlockState state = instance.getLevel().getBlockState(pos);
                        state.pl$onPushEntityInto(instance.getLevel(), pos, instance);
                    }
                }
            }
        }
        return this.collide(vec3);
    }
}
