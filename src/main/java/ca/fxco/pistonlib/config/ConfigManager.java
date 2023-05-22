package ca.fxco.pistonlib.config;

import ca.fxco.api.pistonlib.config.ConfigValue;
import ca.fxco.pistonlib.PistonLibConfig;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.SerializationException;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final Path configPath;
    private final TomlWriter tomlWriter;

    @Getter
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

    /**
     * Generates all values from a config class and then loads the config file and sets all there values
     */
    public void loadConfigClass(Class<?> configClass) {
        for (Field field : configClass.getDeclaredFields()) {

            // Only accept fields that are static & not final
            if (!Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) continue;

            // Check for ConfigValue annotation
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation instanceof ConfigValue configValue) {
                    ParsedValue<?> parsedValue = new ParsedValue<>(
                            field,
                            configValue.desc(),
                            configValue.more(),
                            configValue.keyword(),
                            configValue.category(),
                            configValue.fixes()
                    );
                    parsedValues.put(parsedValue.getName(), parsedValue);
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
                savedValues.put(entry.getKey(), entry.getValue().getValue());
            }
            tomlWriter.write(savedValues, configPath.toFile());
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

}
