package ca.fxco.pistonlib.helpers;

import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.AllArgsConstructor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.apache.commons.lang3.SerializationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PistonLibBehaviorManager {

    private static final Map<BlockState, PistonMoveBehavior> blockStateBehavior = new HashMap<>();

    private static final Logger LOGGER = LogManager.getLogger("PistonLib Behavior Manager");

    private static boolean dirty;

    public static boolean canChangeOverride(BlockState state) {
        return ((ConfigurablePistonBehavior)state).canChangePistonMoveBehaviorOverride();
    }

    public static PistonMoveBehavior getOverride(BlockState state) {
        return blockStateBehavior.getOrDefault(state, PistonMoveBehavior.DEFAULT);
    }

    public static void setOverride(BlockState state, PistonMoveBehavior override) {
        if (canChangeOverride(state)) {
            blockStateBehavior.put(state, override);
        }
    }

    public static void resetOverride(BlockState state) {
        setOverride(state, PistonMoveBehavior.DEFAULT);
    }

    public static void resetOverrides() {
        for (Block block : BuiltInRegistries.BLOCK) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                resetOverride(state);
            }
        }
    }

    /*private static void initDefaultOverrides() {
        for (Block block : BuiltInRegistries.BLOCK) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                setDefaultOverride(state, PistonMoveBehavior.NONE);
            }
        }

        setDefaultOverrides(Blocks.MOVING_PISTON, PistonMoveBehavior.BLOCK);

        // Blocks that are immovable due to having block entities, but should
        // also be immovable for other reasons (e.g. containing obsidian).
        setDefaultOverrides(Blocks.ENCHANTING_TABLE, PistonMoveBehavior.BLOCK);
        setDefaultOverrides(Blocks.BEACON, PistonMoveBehavior.BLOCK);
        setDefaultOverrides(Blocks.ENDER_CHEST, PistonMoveBehavior.BLOCK);
        setDefaultOverrides(Blocks.SPAWNER, PistonMoveBehavior.BLOCK);
    }*/

    public static void load() {
        LOGGER.info("Loading PistonLib move behavior overrides...");

        //initDefaultOverrides();
        for (Block block : BuiltInRegistries.BLOCK) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                setOverride(state, PistonMoveBehavior.DEFAULT);
            }
        }
        Config.load();
        resetOverrides();
        dirty = false;
    }

    public static void save(boolean quietly) {
        if (dirty) {
            if (!quietly) {
                LOGGER.info("Saving PistonLib move behavior overrides...");
            }

            Config.save();
            dirty = false;
        }
    }

    /**
     * A wrapper of {@link net.minecraft.world.level.material.PushReaction PushReaction}
     * that includes {@code none}, to be used in the `/pistonmovebehavior` command.
     */
    @AllArgsConstructor
    public enum PistonMoveBehavior {

        DEFAULT  (0, "default"  , null), // Use vanilla behavior, no overrides
        NORMAL   (1, "normal"   , PushReaction.NORMAL),
        DESTROY  (2, "destroy"  , PushReaction.DESTROY),
        BLOCK    (3, "block"    , PushReaction.BLOCK),
        IGNORE   (4, "ignore"   , PushReaction.IGNORE),
        PUSH_ONLY(5, "push_only", PushReaction.PUSH_ONLY);
        // TODO: Somehow add pull_only support

        public static final PistonMoveBehavior[] ALL;
        private static final Map<String, PistonMoveBehavior> BY_NAME;
        private static final Map<PushReaction, PistonMoveBehavior> BY_PUSH_REACTION;

        static {

            PistonMoveBehavior[] values = values();

            ALL = new PistonMoveBehavior[values.length];
            BY_NAME = new HashMap<>();
            BY_PUSH_REACTION = new HashMap<>();

            for (PistonMoveBehavior behavior : values) {
                ALL[behavior.index] = behavior;
                BY_NAME.put(behavior.name, behavior);

                if (behavior.pushReaction != null) {
                    BY_PUSH_REACTION.put(behavior.pushReaction, behavior);
                }
            }
        }

        private final int index;
        private final String name;
        private final PushReaction pushReaction;

        public int getIndex() {
            return index;
        }

        public static PistonMoveBehavior fromIndex(int index) {
            return (index < 0 || index >= ALL.length) ? null : ALL[index];
        }

        public String getName() {
            return name;
        }

        public static PistonMoveBehavior fromName(String name) {
            return name == null ? null : BY_NAME.get(name);
        }

        public PushReaction getPushReaction() {
            return pushReaction;
        }

        public static PistonMoveBehavior fromPushReaction(PushReaction pushReaction) {
            return BY_PUSH_REACTION.get(pushReaction);
        }

        public boolean isPresent() {
            return pushReaction != null;
        }

        public boolean is(PushReaction pushReaction) {
            return pushReaction != null && this.pushReaction == pushReaction;
        }
    }

    public static class Config {

        public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("pistonlib_behavior_overrides.toml");
        private static final TomlWriter tomlWriter = new TomlWriter();

        public static void load() {
            if (!Files.exists(CONFIG_PATH)) {
                return;
            }
            if (!Files.isRegularFile(CONFIG_PATH) || !Files.isReadable(CONFIG_PATH)) {
                LOGGER.warn("PistonLib behavior overrides config is not readable!");
                return;
            }

            try {
                Map<String, Object> configData = new Toml().read(CONFIG_PATH.toFile()).toMap();
                for (Map.Entry<String, Object> entry : configData.entrySet()) {
                    loadOverrides(entry.getKey(), (Map<String, String>) entry.getValue());
                }
            } catch (IllegalStateException e) {
                throw new SerializationException(e);
            }
        }

        private static void loadOverrides(String blockString, Map<String, String> stateOverrides) {
            Block block = BlockUtils.blockFromString(blockString);

            if (block == null) {
                LOGGER.info("Ignoring PistonLib behavior overrides for unknown block: " + blockString);
                return;
            }
            /*if (!(rawOverrides instanceof String[] strList)) {
                LOGGER.info("PistonLib behavior overrides for `" + blockString + "` provided in an invalid format");
                return;
            }*/

            for (Map.Entry<String, String> entry : stateOverrides.entrySet()) {
                loadOverride(block, entry.getKey(), entry.getValue());
            }
        }

        private static void loadOverride(Block block, String blockStateString, String behaviorString) {
            BlockState state = BlockUtils.blockStateFromString(block, blockStateString);

            if (state == null) {
                LOGGER.info("ignoring piston move behavior overrides for unknown block state " + blockStateString + " of block " + block);
                return;
            }
            if (!canChangeOverride(state)) {
                LOGGER.info("ignoring piston move behavior override for block state " + blockStateString + " of block " + block + ": not allowed to change overrides");
                return;
            }
            /*if (!rawJson.isJsonPrimitive()) {
                LOGGER.info("piston move behavior overrides for block state " + blockStateString + " of block " + block + " provided in an invalid format");
                return;
            }*/

            PistonMoveBehavior override = PistonMoveBehavior.fromName(behaviorString);

            if (override == null) {
                LOGGER.info("Unknown PistonLib behavior `" + behaviorString + "` given for block state `" + blockStateString + "` of block `" + block + "`");
                return;
            }

            setOverride(state, override);
        }

        public static void save() {
            if (Files.exists(CONFIG_PATH) && !Files.isWritable(CONFIG_PATH)) {
                LOGGER.warn("unable to write piston move behavior overrides config!");
                return;
            }

            Map<String, Map<String, String>> serializedValues = new HashMap<>();

            BuiltInRegistries.BLOCK.forEach(block -> {
                saveOverrides(block, serializedValues);
            });

            if (serializedValues.size() == 0) {
                return; // nothing to save
            }

            try {
                Files.createDirectories(CONFIG_PATH.getParent());
                //Map<String, Object> savedValues = new HashMap<>();
                //for (Map.Entry<String, Map<String, String>> entry : serializedValues.entrySet()) {
                //    savedValues.put(entry.getKey(), entry.getValue().getValue());
                //}
                //tomlWriter.write(savedValues, configPath.toFile());
                tomlWriter.write(serializedValues, CONFIG_PATH.toFile());
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }

        private static void saveOverrides(Block block, Map<String, Map<String, String>> serializedValues) {
            Map<String, String> overrides = new HashMap<>();

            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                saveOverride(state, overrides);
            }

            if (overrides.size() > 0) {
                serializedValues.put(BlockUtils.blockAsString(block), overrides);
            }
        }

        private static void saveOverride(BlockState state, Map<String, String> overrides) {
            if (!canChangeOverride(state)) {
                return;
            }

            PistonMoveBehavior override = getOverride(state);

            if (!override.isPresent() || override == PistonMoveBehavior.DEFAULT) {
                return;
            }

            String blockStateString = BlockUtils.propertiesAsString(state);
            String overrideString = override.getName();

            overrides.put(blockStateString, overrideString);
        }
    }
}
