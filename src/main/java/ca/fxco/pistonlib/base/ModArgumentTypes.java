package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.commands.arguments.DirectionArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;

public class ModArgumentTypes {

    static {
        register("direction", DirectionArgument.class, SingletonArgumentInfo.contextFree(DirectionArgument::direction));
    }

    private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(String id,  Class<? extends A> clazz, ArgumentTypeInfo<A, T> serializer) {
        ArgumentTypeRegistry.registerArgumentType(PistonLib.id(id), clazz, serializer);
    }

    public static void bootstrap() {}
}
