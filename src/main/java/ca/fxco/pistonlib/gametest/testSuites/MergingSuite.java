package ca.fxco.pistonlib.gametest.testSuites;

import ca.fxco.api.gametestlib.gametest.GameTestLib;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@GameTestLib(value = {"indirectStickyApi", "pistonStructureGrouping", "tntDupingFix", "pistonPushingCacheFix"})
public class MergingSuite {

    // Can a piston merge 2 slabs directly in-front of it
    @GameTest(timeoutTicks = 4)
    public void directpush1(GameTestHelper helper) {}

    // Make sure the piston merges the first 2 slabs directly in-front of the piston, and not the last 2 slabs
    @GameTest(timeoutTicks = 4)
    public void directpush2(GameTestHelper helper) {}

    // Attempt to merge 2 slabs together from sticky blocks
    @GameTest(timeoutTicks = 4)
    public void singleslab1(GameTestHelper helper) {}

    // Attempt to merge 2 slabs together from sticky blocks
    @GameTestLib("locationalUpdateOrderFix")
    @GameTest(timeoutTicks = 4)
    public void singleslab2(GameTestHelper helper) {}

    // Make sure the Merging API still works //TODO: Fix push limit being counted incorrectly for merging blocks
    /*@GameTestConfig(value = {"indirectStickyApi", "pistonStructureGrouping", "tntDupingFix", "pistonPushingCacheFix"})
    @GameTest(timeoutTicks = 6) // Change timeout to: 3
    public void multiSlabs1(GameTestHelper helper) {}*/
}
