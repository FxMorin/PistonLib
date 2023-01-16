package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.pistonLogic.sticky.StickRules;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import static ca.fxco.pistonlib.PistonLib.id;

public class ModStickyGroups {

    public static final StickyGroup SLIME = register(new ResourceLocation("slime"), new StickyGroup(StickRules.STRICT_SAME));
    public static final StickyGroup HONEY = register(new ResourceLocation("honey"), new StickyGroup(StickRules.STRICT_SAME));

    private static StickyGroup register(String name, StickyGroup family) {
        return register(id(name), family);
    }

    public static StickyGroup register(ResourceLocation id, StickyGroup family) {
        return Registry.register(ModRegistries.STICKY_GROUP, id, family);
    }

    public static StickyGroup get(ResourceLocation id) {
        return ModRegistries.STICKY_GROUP.get(id);
    }

    public static ResourceLocation getId(StickyGroup family) {
        return ModRegistries.STICKY_GROUP.getKey(family);
    }

    public static void bootstrap() { }

    static boolean locked;

    public static void validate() {
        if (!locked) {
            ModRegistries.STICKY_GROUP.forEach(group -> {
                StickyGroup parent = group.getParent();

                while (parent != null) {
                    if (parent == group)
                        throw new IllegalStateException("a sticky group cannot inherit from itself!");
                    parent = parent.getParent();
                }
            });

            locked = true;
        }
    }
}
