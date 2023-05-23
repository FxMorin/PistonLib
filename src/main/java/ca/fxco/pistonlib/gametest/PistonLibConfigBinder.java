package ca.fxco.pistonlib.gametest;

import ca.fxco.api.gametestlib.config.IndirectParsedValue;
import ca.fxco.api.gametestlib.config.ParsedValue;
import ca.fxco.api.gametestlib.config.binder.ConfigBinder;
import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.config.ResolveValue;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;

public class PistonLibConfigBinder extends ConfigBinder {
    @Override
    public Map<String, ParsedValue<?>> registerConfigValues(MinecraftServer server) {
        Map<String, ParsedValue<?>> configValues = new HashMap<>();
        for (Map.Entry<String, ResolveValue<?>> entry : PistonLib.CONFIG_MANAGER.getResolvedValues().entrySet()) {
            configValues.put(entry.getKey(), convertToParsedValue(entry.getValue()));
            System.out.println(entry.getKey());
        }
        return configValues;
    }

    private <T> ParsedValue<T> convertToParsedValue(ResolveValue<T> value) {
        return new IndirectParsedValue<>(value::setValue, value::reset, value::getAllTestingValues);
    }
}
