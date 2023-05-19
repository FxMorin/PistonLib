package ca.fxco.pistonlib.gametest;

import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;

import java.util.Collection;
import java.util.List;

// Not currently being used!
public class TestGenerator {

    @GameTestGenerator
    public Collection<TestFunction> generateBatches() {
        List<TestFunction> testFunctions = GameTestUtil.generateFunctionsFromClassWithCustomBatch(BasicTestSuite.class, "basictestsuite");
        testFunctions.addAll(GameTestUtil.generateFunctionsFromClassWithCustomBatch(BasicTestSuite.class, "basictestsuite2"));
        return testFunctions;
    }
}
