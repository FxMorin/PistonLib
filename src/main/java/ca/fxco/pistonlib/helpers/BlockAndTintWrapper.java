package ca.fxco.pistonlib.helpers;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.minecraft.world.level.*;

@RequiredArgsConstructor
public class BlockAndTintWrapper implements BlockAndTintGetter {

    @Delegate(types=BlockAndTintGetter.class)
    private final BlockAndTintGetter blockAndTintGetter;
}
