package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.commands.Command;
import ca.fxco.pistonlib.commands.PistonLibCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {

    static {
        register(new PistonLibCommand());
    }

    private static void register(Command command) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            if (command.shouldLoad(environment)) {
                command.register(dispatcher, registryAccess, environment);
            }
        });
    }

    public static void bootstrap() {}
}
