package ca.fxco.pistonlib.gametest;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.config.ParsedValue;
import ca.fxco.pistonlib.gametest.expansion.Config;
import ca.fxco.pistonlib.gametest.expansion.GameTestConfig;
import ca.fxco.pistonlib.gametest.expansion.TestFunctionGenerator;
import ca.fxco.pistonlib.gametest.testSuites.MergingSuite;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.Pair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.level.block.Rotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

// Not currently being used!
public class TestGenerator {

    @GameTestGenerator
    public Collection<TestFunction> generateBatches() {
        List<TestFunction> simpleTestFunctions = new ArrayList<>();
        List<TestFunctionGenerator> testFunctionGenerators = new ArrayList<>();
        //addTogether(simpleTestFunctions, testFunctionGenerators, generateTestFunctions(BasicTestSuite.class, "basictestsuite"));
        addTogether(simpleTestFunctions, testFunctionGenerators, generateTestFunctions(MergingSuite.class, "mergingsuite"));
        //addTogether(simpleTestFunctions, testFunctionGenerators, generateTestFunctions(BasicTestSuite.class, "basictestsuite2"));

        List<TestGenerator.GameTestCalcBatch> gameTestCalcBatches = checkAllCombinations(testFunctionGenerators);
        int countBatch = 0;
        for (TestGenerator.GameTestCalcBatch calcBatch : gameTestCalcBatches) {
            String batchId = "" + countBatch;
            List<String> batchNames = new ArrayList<>();
            int x = 0;
            // TODO: Add a way to try all combinations of options, instead of one at a time
            for (String configName : calcBatch.getValues()) {
                ParsedValue<?> parsedValue = PistonLib.CONFIG_MANAGER.getParsedValues().get(configName);
                Object[] objs = parsedValue.getAllTestingValues();
                //System.out.println("testingValues: " + objs.length + " - " + objs);
                for (int i = 0; i < objs.length; i++) {
                    String currentBatchId = batchId + "-" + x + "-" + i;
                    batchNames.add(currentBatchId);
                    int finalI = i;
                    GameTestRegistry.BEFORE_BATCH_FUNCTIONS.put(currentBatchId, serverLevel -> {
                        Object obj = objs[finalI];
                        //System.out.println("Setting config value for: " + configName + " to: " + obj);
                        parsedValue.setValueObj(obj);
                    });
                    GameTestRegistry.AFTER_BATCH_FUNCTIONS.put(currentBatchId, serverLevel -> {
                        parsedValue.reset();
                        //System.out.println("Setting config value for: " + configName + " to default value");
                    });
                    for (TestFunctionGenerator generator : calcBatch.testFunctionGenerators) {
                        GameTestConfig gameTestConfig = generator.getGameTestConfig();
                        simpleTestFunctions.add(
                                generateTestFunctionWithCustomData(
                                        generator.getMethod(),
                                        gameTestHelper -> {
                                            if (gameTestConfig.customBlocks()) {
                                                Config.GameTestChanges changes = generator.getSpecialValues()
                                                        .getOrDefault(configName, Config.GameTestChanges.NONE);
                                                GameTestUtil.pistonLibGameTest(gameTestHelper, changes);
                                            }
                                            turnMethodIntoConsumer(generator.getMethod()).accept(gameTestHelper);
                                        },
                                        generator.getGameTestDataBuilder().batch(currentBatchId).build()
                                )
                        );
                    }
                }
                x++;
            }
            countBatch++;
            System.out.println(batchNames);
        }
        System.out.println("TestGenerator has generated: " + simpleTestFunctions.size() + " test functions as: " + countBatch + " batches");

        return simpleTestFunctions;
    }

    public static List<TestGenerator.GameTestCalcBatch> checkAllCombinations(List<TestFunctionGenerator> testFunctionGenerators) {
        List<GameTestCalcBatch> gameTestCalcBatches = new ArrayList<>();
        for (TestFunctionGenerator generator : testFunctionGenerators) {
            GameTestConfig gameTestConfig = generator.getGameTestConfig();
            boolean gotBatch = false;
            if (gameTestConfig.ignored()) {
                if (gameTestConfig.combined()) {
                    for (GameTestCalcBatch batch : new ArrayList<>(gameTestCalcBatches)) {
                        boolean failed = false;
                        for (String str : batch.getValues()) {
                            if (generator.getValues().contains(str)) {
                                failed = true;
                                break;
                            }
                        }
                        if (!failed && batch.canAcceptGenerator(generator)) {
                            batch.addGenerator(generator);
                            gotBatch = true;
                            break;
                        }
                    }
                } else {
                    for (GameTestCalcBatch batch : new ArrayList<>(gameTestCalcBatches)) {
                        int count = 0;
                        for (String str : batch.getValues()) {
                            if (generator.getValues().contains(str)) {
                                count++;
                                break;
                            }
                        }
                        if (count == generator.getValues().size() && batch.canAcceptGenerator(generator)) {
                            batch.addGenerator(generator);
                            gotBatch = true;
                            break;
                        }
                    }
                }
            } else {
                if (gameTestConfig.combined()) {
                    for (GameTestCalcBatch batch : new ArrayList<>(gameTestCalcBatches)) {
                        List<String> differences = new ArrayList<>(Sets.difference(Sets.newHashSet(batch.getValues()), Sets.newHashSet(generator.getValues())));
                        if (differences.size() == 0) {
                            if (batch.canAcceptGenerator(generator)) {
                                batch.addGenerator(generator);
                                gotBatch = true;
                                break;
                            }
                        }
                    }
                } else {
                    for (GameTestCalcBatch batch : new ArrayList<>(gameTestCalcBatches)) {
                        for (String str : generator.getValues()) {
                            if (batch.getValues().contains(str)) {
                                if (batch.canAcceptGenerator(generator)) {
                                    batch.addGenerator(generator);
                                    gotBatch = true;
                                }
                                break;
                            }
                        }
                        List<String> differences = new ArrayList<>(Sets.difference(Sets.newHashSet(batch.getValues()), Sets.newHashSet(generator.getValues())));
                        if (differences.size() == 0) {
                            if (batch.canAcceptGenerator(generator)) {
                                batch.addGenerator(generator);
                                gotBatch = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (!gotBatch) {
                gameTestCalcBatches.add(new GameTestCalcBatch(new ArrayList<>(generator.getValues())));
            }
        }
        return gameTestCalcBatches;
    }

    public static void addTogether(List<TestFunction> simpleTestFunctions,
                                   List<TestFunctionGenerator> testFunctionGenerators,
                                   Pair<List<TestFunction>, List<TestFunctionGenerator>> newTestFunctionPair) {
        simpleTestFunctions.addAll(newTestFunctionPair.first());
        testFunctionGenerators.addAll(newTestFunctionPair.second());
    }

    public static Pair<List<TestFunction>, List<TestFunctionGenerator>> generateTestFunctions(Class<?> clazz, String batch) {
        List<TestFunction> simpleTestFunctions = new ArrayList<>();
        List<TestFunctionGenerator> testFunctionGenerators = new ArrayList<>();
        Arrays.stream(clazz.getDeclaredMethods()).forEach(m -> {
            if (m.isAnnotationPresent(GameTest.class)) {
                GameTest gameTest = m.getAnnotation(GameTest.class);
                GameTestData.GameTestDataBuilder gameTestDataBuilder = GameTestData.builderFrom(gameTest);
                if (m.isAnnotationPresent(GameTestConfig.class)) {
                    GameTestConfig gameTestConfig = m.getAnnotation(GameTestConfig.class);
                    testFunctionGenerators.add(new TestFunctionGenerator(m, gameTestConfig, gameTestDataBuilder.batch(batch)));
                } else {
                    // If no GameTestConfig is available, only run it once with default config options. Part of the `simple` batch
                    simpleTestFunctions.add(generateTestFunctionWithCustomData(m, gameTestDataBuilder.batch("simple").build()));
                }
            }
        });
        return Pair.of(simpleTestFunctions, testFunctionGenerators);
    }

    private static TestFunction generateTestFunctionWithCustomData(Method method,
                                                                   Consumer<GameTestHelper> consumer,
                                                                   GameTestData gameTestData) {
        String declaredClassName = method.getDeclaringClass().getSimpleName().toLowerCase();
        String testId = declaredClassName + "." + method.getName().toLowerCase();
        return new TestFunction(
                gameTestData.batch,
                testId,
                gameTestData.template.isEmpty() ? testId : declaredClassName + "." + gameTestData.template,
                StructureUtils.getRotationForRotationSteps(gameTestData.rotationSteps),
                gameTestData.timeoutTicks,
                gameTestData.setupTicks,
                gameTestData.required,
                gameTestData.requiredSuccesses,
                gameTestData.attempts,
                consumer
        );
    }

    private static TestFunction generateTestFunctionWithCustomData(Method method, GameTestData gameTestData) {
        String string = method.getDeclaringClass().getSimpleName();
        String string2 = string.toLowerCase();
        String string3 = string2 + "." + method.getName().toLowerCase();
        String string4 = gameTestData.template.isEmpty() ? string3 : string2 + "." + gameTestData.template;
        String string5 = gameTestData.batch;
        Rotation rotation = StructureUtils.getRotationForRotationSteps(gameTestData.rotationSteps);
        return new TestFunction(string5, string3, string4, rotation, gameTestData.timeoutTicks, gameTestData.setupTicks, gameTestData.required, gameTestData.requiredSuccesses, gameTestData.attempts, turnMethodIntoConsumer(method));
    }

    public static void registerClassWithCustomBatch(Class<?> clazz, String batch) {
        Arrays.stream(clazz.getDeclaredMethods()).forEach(m -> {
            if (m.isAnnotationPresent(GameTest.class)) {
                GameTest gameTest = m.getAnnotation(GameTest.class);
                GameTestData gameTestData = GameTestData.builderFrom(gameTest).batch(batch).build();
                GameTestRegistry.getAllTestFunctions().add(generateTestFunctionWithCustomData(m, gameTestData));
            }
        });
    }

    public static TestFunction turnMethodIntoTestFunction(Method method) {
        GameTest gameTest = method.getAnnotation(GameTest.class);
        String string = method.getDeclaringClass().getSimpleName();
        String string2 = string.toLowerCase();
        String string3 = string2 + "." + method.getName().toLowerCase();
        String string4 = gameTest.template().isEmpty() ? string3 : string2 + "." + gameTest.template();
        String string5 = gameTest.batch();
        Rotation rotation = StructureUtils.getRotationForRotationSteps(gameTest.rotationSteps());
        return new TestFunction(string5, string3, string4, rotation, gameTest.timeoutTicks(), gameTest.setupTicks(), gameTest.required(), gameTest.requiredSuccesses(), gameTest.attempts(), turnMethodIntoConsumer(method));
    }

    private static Consumer<GameTestHelper> turnMethodIntoConsumer(Method method) {
        return (object) -> {
            try {
                Object object2 = method.getDeclaringClass().newInstance();
                method.invoke(object2, object);
            } catch (InvocationTargetException var3) {
                if (var3.getCause() instanceof RuntimeException) {
                    throw (RuntimeException)var3.getCause();
                } else {
                    throw new RuntimeException(var3.getCause());
                }
            } catch (ReflectiveOperationException var4) {
                throw new RuntimeException(var4);
            }
        };
    }

    @Getter
    @AllArgsConstructor
    public static class GameTestCalcBatch {

        private final List<String> values;
        private final List<TestFunctionGenerator> testFunctionGenerators = new ArrayList<>();

        public void addGenerator(TestFunctionGenerator generator) {
            Set<String> difference = Sets.difference(Sets.newHashSet(generator.getValues()), Sets.newHashSet(this.getValues()));
            this.values.addAll(difference);
            this.testFunctionGenerators.add(generator);
        }

        public boolean canAcceptGenerator(TestFunctionGenerator generator) {
            Set<String> difference = Sets.difference(Sets.newHashSet(generator.getValues()), Sets.newHashSet(this.getValues()));
            for (TestFunctionGenerator gen : testFunctionGenerators) {
                GameTestConfig gameTestConfig = gen.getGameTestConfig();
                if (gameTestConfig.ignored()) {
                    if (gameTestConfig.combined()) {
                        for (String val : gen.getValues()) {
                            if (difference.contains(val)) {
                                return false;
                            }
                        }
                    } else {
                        int count = 0;
                        for (String val : gen.getValues()) {
                            if (difference.contains(val)) {
                                count++;
                            }
                        }
                        if (count == gen.getValues().size()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }

    @Builder
    @AllArgsConstructor
    public static class GameTestData {
        @Builder.Default private int timeoutTicks = 100;
        @Builder.Default private String batch = "defaultBatch";
        @Builder.Default private int rotationSteps = 0;
        @Builder.Default private boolean required = true;
        @Builder.Default private String template = "";
        @Builder.Default private long setupTicks = 0L;
        @Builder.Default private int attempts = 1;
        @Builder.Default private int requiredSuccesses = 1;

        public static GameTestData from(GameTest gameTest) {
            return new GameTestData(gameTest.timeoutTicks(), gameTest.batch(), gameTest.rotationSteps(),
                    gameTest.required(), gameTest.template(), gameTest.setupTicks(),
                    gameTest.attempts(), gameTest.requiredSuccesses());
        }

        public static GameTestData.GameTestDataBuilder builderFrom(GameTest gameTest) {
            return GameTestData.builder()
                    .timeoutTicks(gameTest.timeoutTicks())
                    .batch(gameTest.batch())
                    .rotationSteps(gameTest.rotationSteps())
                    .required(gameTest.required())
                    .template(gameTest.template())
                    .setupTicks(gameTest.setupTicks())
                    .attempts(gameTest.attempts())
                    .requiredSuccesses(gameTest.requiredSuccesses());
        }
    }
}
