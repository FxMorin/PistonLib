package ca.fxco.configurablepistons.blocks.pistons.configurablePiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.blocks.pistons.speedPiston.SpeedPistonBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.List;

public class ConfigurablePistonBlockEntity extends SpeedPistonBlockEntity {

    protected boolean translocation;

    public ConfigurablePistonBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public ConfigurablePistonBlockEntity(BlockPos pos, BlockState state, ConfigurablePistonExtensionBlock extensionBlock) {
        this(pos, state, extensionBlock, ModBlockEntities.CONFIGURABLE_PISTON_BLOCK_ENTITY);
    }

    public ConfigurablePistonBlockEntity(BlockPos pos, BlockState state,
                                         ConfigurablePistonExtensionBlock extensionBlock, BlockEntityType<?> type) {
        super(pos, state, extensionBlock, type);
    }

    public ConfigurablePistonBlockEntity(float speed, boolean translocation, BlockPos pos, BlockState state,
                                         BlockState pushedBlock, Direction facing, boolean extending, boolean source) {
        this(speed, translocation, pos, state, pushedBlock, facing, extending, source,
                ModBlocks.CONFIGURABLE_MOVING_PISTON);
    }
    public ConfigurablePistonBlockEntity(float speed, boolean translocation, BlockPos pos, BlockState state,
                                         BlockState pushedBlock, Direction facing, boolean extending, boolean source,
                                         ConfigurablePistonExtensionBlock extensionBlock) {
        this(speed, translocation, pos, state, pushedBlock, facing, extending, source,
                extensionBlock, ModBlockEntities.CONFIGURABLE_PISTON_BLOCK_ENTITY);
    }

    public ConfigurablePistonBlockEntity(float speed, boolean translocation, BlockPos pos, BlockState state,
                                         BlockState pushedBlock, Direction facing, boolean extending, boolean source,
                                         ConfigurablePistonExtensionBlock extensionBlock, BlockEntityType<?> type) {
        super(speed, pos, state, pushedBlock, facing, extending, source, extensionBlock, type);
        this.translocation = translocation;
    }

    @Override
    public void pushEntities(World world, BlockPos pos, float f) {
        if (translocation) {
            VoxelShape vs = this.getPushedBlock().getCollisionShape(world, pos);
            if (vs.isEmpty()) return;
            Box box = vs.getBoundingBox().offset(pos).expand(0.01D); // Cheating ;)
            List<Entity> list = world.getOtherEntities(null, box);
            if (list.isEmpty()) return;
            boolean isSlime = this.getPushedBlock().isOf(Blocks.SLIME_BLOCK);
            Direction dir = this.getMovementDirection();
            for (Entity entity : list) {
                if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
                    if (isSlime && this.speed < 0.1F) { //No player check, just like before
                        Vec3d vec3d = entity.getVelocity();
                        double x = vec3d.x, y = vec3d.y, z = vec3d.z;
                        switch (dir.getAxis()) {
                            case X -> x = dir.getOffsetX() * this.speed;
                            case Y -> y = dir.getOffsetY() * this.speed;
                            case Z -> z = dir.getOffsetZ() * this.speed;
                        }
                        entity.setVelocity(x, y, z);
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
                            x * (double) dir.getOffsetX() * speed,
                            y * (double) dir.getOffsetY() * speed,
                            z * (double) dir.getOffsetZ() * speed
                    ));
                }
            }
        } else {
            super.pushEntities(world, pos, f); // Just speed
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.translocation = nbt.getBoolean("translocation");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean("translocation", translocation);
    }
}
