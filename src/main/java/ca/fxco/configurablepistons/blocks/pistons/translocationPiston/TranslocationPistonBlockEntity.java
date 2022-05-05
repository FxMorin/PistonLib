package ca.fxco.configurablepistons.blocks.pistons.translocationPiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.mixin.accessors.BlockEntityAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.List;

public class TranslocationPistonBlockEntity extends BasicPistonBlockEntity {
    public TranslocationPistonBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        ((BlockEntityAccessor)this).setType(ModBlockEntities.TRANSLOCATION_PISTON_BLOCK_ENTITY);
    }

    public TranslocationPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing,
                                  boolean extending, boolean source, BasicPistonExtensionBlock extensionBlock) {
        super(pos, state, pushedBlock, facing, extending, source, extensionBlock);
        ((BlockEntityAccessor)this).setType(ModBlockEntities.TRANSLOCATION_PISTON_BLOCK_ENTITY);
    }

    @Override
    public void pushEntities(World world, BlockPos pos, float f) {
        VoxelShape vs = this.getPushedBlock().getCollisionShape(world, pos);
        if (vs.isEmpty()) return;
        Box box = vs.getBoundingBox().offset(pos).expand(0.01D); // Cheating ;)
        List<Entity> list = world.getOtherEntities(null, box);
        if (list.isEmpty()) return;
        boolean isSlime = this.getPushedBlock().isOf(Blocks.SLIME_BLOCK);
        Direction dir = this.getMovementDirection();
        for (Entity entity : list) {
            if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
                if (isSlime) { //No player check, just like before
                    Vec3d vec3d = entity.getVelocity();
                    switch (dir.getAxis()) {
                        case X -> entity.setVelocity(dir.getOffsetX(), vec3d.y, vec3d.z);
                        case Y -> entity.setVelocity(vec3d.x, dir.getOffsetY(), vec3d.z);
                        case Z -> entity.setVelocity(vec3d.x, vec3d.y, dir.getOffsetZ());
                    }
                }
                double x = 0.0D, y = 0.0D, z = 0.0D;
                Box entityBox = entity.getBoundingBox();
                boolean positive = dir.getDirection() == Direction.AxisDirection.POSITIVE;
                switch (dir.getAxis()) {
                    case X -> x = (positive ? box.maxX - entityBox.minX : entityBox.maxX - box.minX) + 0.1D;
                    case Y -> y = (positive ? box.maxY - entityBox.minY : entityBox.maxY - box.minY) + 0.1D;
                    case Z -> z = (positive ? box.maxZ - entityBox.minZ : entityBox.maxZ - box.minZ) + 0.1D;
                }
                entity.move(MovementType.SELF, new Vec3d(
                        x * (double) dir.getOffsetX(),
                        y * (double) dir.getOffsetY(),
                        z * (double) dir.getOffsetZ()
                ));
            }
        }
    }
}
