package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.pistonLogic.families.PistonBehavior;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import java.util.Objects;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import static ca.fxco.pistonlib.PistonLib.id;

public class ModPistonFamilies {

    public static final PistonFamily BASIC = register("basic", new PistonFamily(PistonBehavior.DEFAULT, false));
    public static final PistonFamily LONG = register("long", new PistonFamily(PistonBehavior.DEFAULT, false));
    public static final PistonFamily CONFIGURABLE = register("configurable", new PistonFamily(PistonBehavior.DEFAULT, false));
    public static final PistonFamily STALE = register("stale", new PistonFamily(PistonBehavior.Builder().noQuasi().build(), false));
    public static final PistonFamily VERY_QUASI = register("very_quasi", new PistonFamily(PistonBehavior.DEFAULT, false));
    public static final PistonFamily STRONG = register("strong", new PistonFamily(PistonBehavior.Builder().pushLimit(24).build(), false));
    public static final PistonFamily FAST = register("fast", new PistonFamily(PistonBehavior.Builder().pushLimit(2).build(), false));
    public static final PistonFamily FRONT_POWERED = register("front_powered", new PistonFamily(PistonBehavior.Builder().frontPowered().build(), false));
    public static final PistonFamily TRANSLOCATION = register("translocation", new PistonFamily(PistonBehavior.Builder().translocation().build(), false));
    public static final PistonFamily SLIPPERY = register("slippery", new PistonFamily(PistonBehavior.DEFAULT, false));
    public static final PistonFamily SUPER = register("super", new PistonFamily(PistonBehavior.Builder().pushLimit(Integer.MAX_VALUE).verySticky().build(), false));
    public static final PistonFamily MBE = register("mbe", new PistonFamily(PistonBehavior.DEFAULT, false));
    public static final PistonFamily MERGE = register("merge", new PistonFamily(PistonBehavior.DEFAULT, false));
    public static final PistonFamily VERY_STICKY = register("very_sticky", new PistonFamily(PistonBehavior.Builder().verySticky().build(), false));

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
                try {
                    if (family.getBases().isEmpty())
                        throw new IllegalStateException("missing base block");
                    Objects.requireNonNull(family.getHead(), "head block");
                    Objects.requireNonNull(family.getMoving(), "moving block");
                    Objects.requireNonNull(family.getMovingBlockEntityType(), "moving block entity type");
                    Objects.requireNonNull(family.getMovingBlockEntityFactory(), "moving block entity factory");
                } catch (Exception e) {
                    throw new IllegalStateException("piston family " + family + " is invalid!", e);
                }
            });

            locked = true;
        }
    }
}
