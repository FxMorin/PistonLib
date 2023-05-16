package ca.fxco.pistonlib.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.BooleanSupplier;

public class GametestUtil {

    public static void succeedAfterDelay(GameTestHelper helper, long tick, BooleanSupplier supplier, String failReason) {
        helper.runAfterDelay(tick, () -> {
            if (supplier.getAsBoolean()) {
                helper.succeed();
            } else {
                helper.fail(failReason);
            }
        });
    }
}
