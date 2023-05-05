package ca.fxco.pistonlib.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

/**
 * It's just a BlockPos that can be instanceof checked against to see if call came from the ticking API
 */
public class FakeBlockPos extends BlockPos {

    public FakeBlockPos(int i, int j, int k) {
        super(i, j, k);
    }

    public FakeBlockPos(double d, double e, double f) {
        super(d, e, f);
    }

    public FakeBlockPos(Vec3 vec3) {
        super(vec3);
    }

    public FakeBlockPos(Position position) {
        super(position);
    }

    public FakeBlockPos(Vec3i vec3i) {
        super(vec3i);
    }

    public static FakeBlockPos of(BlockPos blockPos) {
        return new FakeBlockPos(blockPos);
    }
}
