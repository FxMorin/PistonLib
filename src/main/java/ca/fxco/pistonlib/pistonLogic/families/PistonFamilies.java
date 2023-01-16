package ca.fxco.pistonlib.pistonLogic.families;

import ca.fxco.pistonlib.base.ModRegistries;

import java.util.Objects;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import static ca.fxco.pistonlib.PistonLib.id;

public class PistonFamilies {

    public static final PistonFamily BASIC = register("basic", new PistonFamily(false));
    public static final PistonFamily LONG = register("long", new PistonFamily(false));
    public static final PistonFamily CONFIGURABLE = register("configurable", new PistonFamily(false));
    public static final PistonFamily STALE = register("stale", new PistonFamily(false));
    public static final PistonFamily QUASI = register("quasi", new PistonFamily(false));
    public static final PistonFamily STRONG = register("strong", new PistonFamily(false));
    public static final PistonFamily FAST = register("fast", new PistonFamily(false));
    public static final PistonFamily FRONT_POWERED = register("front_powered", new PistonFamily(false));
    public static final PistonFamily TRANSLOCATION = register("translocation", new PistonFamily(false));
    public static final PistonFamily SLIPPERY = register("slippery", new PistonFamily(false));
    public static final PistonFamily SUPER = register("super", new PistonFamily(false));
    public static final PistonFamily MBE = register("mbe", new PistonFamily(false));
    public static final PistonFamily MERGE = register("merge", new PistonFamily(false));
    public static final PistonFamily VERY_STICKY = register("very_sticky", new PistonFamily(false));

    private static PistonFamily register(String name, PistonFamily family) {
        return register(id(name), family);
    }

    public static PistonFamily register(ResourceLocation id, PistonFamily family) {
        return Registry.register(ModRegistries.PISTON_FAMILY, id, family);
    }

    public static PistonFamily get(ResourceLocation id) {
        return ModRegistries.PISTON_FAMILY.get(id);
    }

    public static ResourceLocation getId(PistonFamily family) {
        return ModRegistries.PISTON_FAMILY.getKey(family);
    }

    public static void bootstrap() { }

    static boolean locked;

    static boolean requireNotLocked() {
        if (locked) {
            throw new IllegalStateException("cannot alter piston families after they have been locked!");
        }

        return true;
    }

    public static void validate() {
        if (!locked) {
            ModRegistries.PISTON_FAMILY.forEach(family -> {
                if (family.base.isEmpty())
                    throw new IllegalStateException("each piston family must have at least one base block!");
                Objects.requireNonNull(family.head);
                Objects.requireNonNull(family.moving);
                Objects.requireNonNull(family.movingBlockEntityType);
                Objects.requireNonNull(family.movingBlockEntityFactory);
            });

            locked = true;
        }
    }
}
