package ca.fxco.pistonlib.config;

import ca.fxco.api.pistonlib.config.ConfigValue;
import ca.fxco.api.pistonlib.config.Observer;
import ca.fxco.api.pistonlib.config.Parser;
import ca.fxco.api.pistonlib.config.TypeConverter;
import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.helpers.Utils;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.SerializationException;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Config Options should never be changed async. They should be changed near the end of the tick, highly recommended
 * that you do this during the MinecraftServer tickables using minecraftServer.addTickable() or during the network
 * tick. Such as during a packet
 */
public class ConfigManager {

    private final Path configPath;
    private final TomlWriter tomlWriter;
    private final List<TypeConverter> typeConverters = new ArrayList<>();

    private final Map<String, ParsedValue<?>> parsedValues = new HashMap<>();

    // TODO: Add a way to change config values in-game (with listeners to update the config file)

    public ConfigManager(String modId) {
        this.configPath = FabricLoader.getInstance().getConfigDir().resolve(modId + ".toml");
        this.tomlWriter = new TomlWriter();

        loadConfigClass(PistonLibConfig.class);

        Map<String, Object> loadedValues = loadValuesFromConf();
        if (loadedValues != null) {
            for (Map.Entry<String, Object> entry : loadedValues.entrySet()) {
                if (parsedValues.containsKey(entry.getKey())) {
                    parsedValues.get(entry.getKey()).setValueFromConfig(entry.getValue());
                }
            }
        }

        writeValuesToConf();
    }

    public void addConverter(TypeConverter converter) {
        this.typeConverters.add(converter);
    }

    public <T> T tryLoadingValue(Object value, ParsedValue<T> parsedValue) {
        for (TypeConverter converter : this.typeConverters) {
            T newValue = converter.loadValue(value, parsedValue);
            if (newValue != null) {
                return newValue;
            }
        }
        return null;
    }

    public <T> Object trySavingValue(T value, ParsedValue<T> parsedValue) {
        for (TypeConverter converter : this.typeConverters) {
            Object newValue = converter.saveValue(value, parsedValue);
            if (newValue != null) {
                return newValue;
            }
        }
        return value;
    }

    /**
     * Generates all values from a config class and then loads the config file and sets all there values
     */
    public void loadConfigClass(Class<?> configClass) {
        nextField: for (Field field : configClass.getDeclaredFields()) {

            // Only accept fields that are static & not final
            if (!Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) continue;

            // Check for ConfigValue annotation
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation instanceof ConfigValue configValue) {
                    for (Class<? extends ConfigValue.Condition> conditionClazz : configValue.condition()) {
                        ConfigValue.Condition condition = Utils.createInstance(conditionClazz);
                        if (!condition.shouldInclude()) {
                            continue nextField; // Skip this field entirely
                        }
                    }
                    Parser<?>[] parsers = Utils.createInstances(configValue.parser());
                    Observer<?>[] observers = Utils.createInstances(configValue.observer());
                    ParsedValue<?> parsedValue = new ParsedValue<>(
                            field,
                            configValue.desc(),
                            configValue.more(),
                            configValue.keyword(),
                            configValue.category(),
                            configValue.requires(),
                            configValue.conflict(),
                            configValue.requiresRestart(),
                            configValue.fixes(),
                            parsers,
                            observers
                    );
                    parsedValues.put(parsedValue.getName(), parsedValue);
                    break;
                }
            }
        }
    }

    private @Nullable Map<String, Object> loadValuesFromConf() {
        if (Files.exists(configPath)) {
            try {
                return new Toml().read(configPath.toFile()).toMap();
            } catch (IllegalStateException e) {
                throw new SerializationException(e);
            }
        }
        return null;
    }

    private void writeValuesToConf() {
        try {
            Files.createDirectories(configPath.getParent());
            Map<String, Object> savedValues = new HashMap<>();
            for (Map.Entry<String, ParsedValue<?>> entry : parsedValues.entrySet()) {
                ParsedValue<?> parsedValue = entry.getValue();
                Object value = parsedValue.getValue();
                savedValues.put(entry.getKey(), parsedValue.getValueForConfig());
            }
            tomlWriter.write(savedValues, configPath.toFile());
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

}
