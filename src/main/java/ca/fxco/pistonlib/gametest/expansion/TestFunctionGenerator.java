package ca.fxco.pistonlib.gametest.expansion;

import ca.fxco.pistonlib.gametest.TestGenerator;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@Getter
public class TestFunctionGenerator {

    private final Method method;
    private final SortedSet<String> values;
    private final RunState runState;
    private final TestGenerator.GameTestData.GameTestDataBuilder gameTestDataBuilder;

    public TestFunctionGenerator(Method method, GameTestConfig gameTestConfig,
                                 TestGenerator.GameTestData.GameTestDataBuilder gameTestDataBuilder) {
        this.method = method;
        this.runState = gameTestConfig.runState();
        this.gameTestDataBuilder = gameTestDataBuilder;

        this.values = new TreeSet<>(List.of(gameTestConfig.value()));
        /*for (Config config : gameTestConfig.configs()) { // TODO: maybe? Not sure how viable or needed this is
            for (Class<? extends Config.ConfigOptionTest> testClass : config.testClass()) {
                Config.ConfigOptionTest optionTest = Utils.createInstance(testClass);
                optionTest.shouldPass(getthissomehow, config.value(), this.values.contains(config.value()));
            }
        }*/
    }
}
