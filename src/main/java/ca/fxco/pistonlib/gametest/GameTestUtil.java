package ca.fxco.pistonlib.gametest;

import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.blocks.gametest.CheckStateBlockEntity;
import ca.fxco.pistonlib.blocks.gametest.PulseStateBlockEntity;
import ca.fxco.pistonlib.gametest.extension.GameTestGroupConditions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class GameTestUtil {

    /**
     * By using this method as the first line in a gametest, you will be able to use gametest blocks within those tests
     */
    public static void pistonLibGameTest(GameTestHelper helper) {
        if (helper.getTick() != 0) { // Only run searching logic on the first tick
            return;
        }
        GameTestGroupConditions groupConditions = new GameTestGroupConditions();
        helper.forEveryBlockInStructure(blockPos -> {
            BlockState state = helper.getBlockState(blockPos);
            Block block = state.getBlock();
            if (block == ModBlocks.PULSE_STATE_BLOCK) {
                BlockEntity blockEntity = helper.getBlockEntity(blockPos);
                if (blockEntity instanceof PulseStateBlockEntity pulseStateBe) {
                    helper.setBlock(blockPos, pulseStateBe.getFirstBlockState());
                    int delay = pulseStateBe.getDelay();
                    int duration = pulseStateBe.getDuration();
                    BlockPos blockPos2 = blockPos.immutable();
                    if (delay > 0) {
                        helper.runAfterDelay(delay, () ->
                                helper.setBlock(blockPos2, pulseStateBe.getPulseBlockState())
                        );
                    }
                    if (duration > 0) {
                        helper.runAfterDelay(delay + duration, () ->
                                helper.setBlock(blockPos2, pulseStateBe.getLastBlockState())
                        );
                    }
                }
            } else if (block == ModBlocks.CHECK_STATE_BLOCK) {
                BlockEntity blockEntity = helper.getBlockEntity(blockPos);
                if (blockEntity instanceof CheckStateBlockEntity checkStateBe) {
                    BlockPos checkPos = blockPos.relative(checkStateBe.getDirection());
                    groupConditions.addCondition(checkStateBe, checkPos);
                }
            } else if (block == ModBlocks.TEST_TRIGGER_BLOCK) {
                groupConditions.addTestTrigger(blockPos);
            } else if (block == ModBlocks.GAMETEST_REDSTONE_BLOCK) {
                helper.setBlock(blockPos, state.cycle(BlockStateProperties.POWERED));
            }
        });
        if (!groupConditions.getTestConditions().isEmpty()) {
            helper.onEachTick(() -> {
                groupConditions.runTick(helper);
                if (groupConditions.isSuccess()) {
                    helper.succeed();
                }
            });
        }
    }

    public static void succeedAfterDelay(GameTestHelper helper, long tick, BooleanSupplier supplier, String failReason) {
        helper.runAfterDelay(tick, () -> {
            if (supplier.getAsBoolean()) {
                helper.succeed();
            } else {
                helper.fail(failReason);
            }
        });
    }

    public static void setBlock(GameTestHelper helper, int i, int j, int k, Block block, int flags) {
        setBlock(helper, new BlockPos(i, j, k), block, flags);
    }

    public static void setBlock(GameTestHelper helper, int i, int j, int k, BlockState blockState, int flags) {
        setBlock(helper, new BlockPos(i, j, k), blockState, flags);
    }

    public static void setBlock(GameTestHelper helper, BlockPos blockPos, Block block, int flags) {
        setBlock(helper, blockPos, block.defaultBlockState(), flags);
    }

    public static void setBlock(GameTestHelper helper, BlockPos blockPos, BlockState blockState, int flags) {
        helper.getLevel().setBlock(helper.absolutePos(blockPos), blockState, flags);
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

    public static List<TestFunction> generateFunctionsFromClassWithCustomBatch(Class<?> clazz, String batch) {
        List<TestFunction> testFunctions = new ArrayList<>();
        Arrays.stream(clazz.getDeclaredMethods()).forEach(m -> {
            if (m.isAnnotationPresent(GameTest.class)) {
                GameTest gameTest = m.getAnnotation(GameTest.class);
                GameTestData gameTestData = GameTestData.builderFrom(gameTest).batch(batch).build();
                testFunctions.add(generateTestFunctionWithCustomData(m, gameTestData));
            }
        });
        return testFunctions;
    }

    public static TestFunction generateTestFunctionWithCustomData(Method method, GameTestData gameTestData) {
        String string = method.getDeclaringClass().getSimpleName();
        String string2 = string.toLowerCase();
        String string3 = string2 + "." + method.getName().toLowerCase();
        String string4 = gameTestData.template.isEmpty() ? string3 : string2 + "." + gameTestData.template;
        String string5 = gameTestData.batch;
        Rotation rotation = StructureUtils.getRotationForRotationSteps(gameTestData.rotationSteps);
        return new TestFunction(string5, string3, string4, rotation, gameTestData.timeoutTicks, gameTestData.setupTicks, gameTestData.required, gameTestData.requiredSuccesses, gameTestData.attempts, turnMethodIntoConsumer(method));
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
