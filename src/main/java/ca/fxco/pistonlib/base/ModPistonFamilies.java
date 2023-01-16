package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.pistonLogic.families.PistonBehavior;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import java.util.Objects;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import static ca.fxco.pistonlib.PistonLib.id;

public class ModPistonFamilies {

    public static final PistonFamily BASIC = register("basic", new PistonFamily(new PistonBehavior(), false));
    public static final PistonFamily LONG = register("long", new PistonFamily(new PistonBehavior(), false));
    public static final PistonFamily CONFIGURABLE = register("configurable", new PistonFamily(new PistonBehavior(), false));
    public static final PistonFamily STALE = register("stale", new PistonFamily(new PistonBehavior().noQuasi(), false));
    public static final PistonFamily VERY_QUASI = register("very_quasi", new PistonFamily(new PistonBehavior(), false));
    public static final PistonFamily STRONG = register("strong", new PistonFamily(new PistonBehavior().pushLimit(24), false));
    public static final PistonFamily FAST = register("fast", new PistonFamily(new PistonBehavior().pushLimit(2), false));
    public static final PistonFamily FRONT_POWERED = register("front_powered", new PistonFamily(new PistonBehavior(), false));
    public static final PistonFamily TRANSLOCATION = register("translocation", new PistonFamily(new PistonBehavior(), false));
    public static final PistonFamily SLIPPERY = register("slippery", new PistonFamily(new PistonBehavior(), false));
    public static final PistonFamily SUPER = register("super", new PistonFamily(new PistonBehavior().pushLimit(Integer.MAX_VALUE).verySticky(), false));
    public static final PistonFamily MBE = register("mbe", new PistonFamily(new PistonBehavior(), false));
    public static final PistonFamily MERGE = register("merge", new PistonFamily(new PistonBehavior(), false));
    public static final PistonFamily VERY_STICKY = register("very_sticky", new PistonFamily(new PistonBehavior().verySticky(), false));

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

    private static boolean locked;

    public static boolean requireNotLocked() {
        if (locked) {
            throw new IllegalStateException("cannot alter piston families after they have been locked!");
        }

        return true;
    }

    public static void validate() {
        if (!locked) {
            ModRegistries.PISTON_FAMILY.forEach(family -> {
                if (family.getBases().isEmpty())
                    throw new IllegalStateException("each piston family must have at least one base block!");
                Objects.requireNonNull(family.getHead());
                Objects.requireNonNull(family.getMoving());
                Objects.requireNonNull(family.getMovingBlockEntityType());
                Objects.requireNonNull(family.getMovingBlockEntityFactory());
            });

            locked = true;
        }
    }
}
