package ca.fxco.pistonlib.pistonLogic.families;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;

import net.minecraft.core.registries.BuiltInRegistries;

public class PistonFamilies {

    private static final Map<String, PistonFamily> ID_TO_FAMILY = new HashMap<>();
    private static final Map<BasicPistonHeadBlock, PistonFamily> PISTON_HEAD_TO_FAMILY = new HashMap<>();

    public static final PistonFamily BASIC = new PistonFamily("basic", false);
    public static final PistonFamily LONG = new PistonFamily("long", false);
    public static final PistonFamily STALE = new PistonFamily("stale", false);
    public static final PistonFamily STRONG = new PistonFamily("strong");
    public static final PistonFamily FAST = new PistonFamily("fast", false);
    public static final PistonFamily STICKY = new PistonFamily("sticky", false);
    public static final PistonFamily FRONT_POWERED = new PistonFamily("front_powered", false);
    public static final PistonFamily TRANSLOCATION = new PistonFamily("translocation", false);
    public static final PistonFamily SLIPPERY = new PistonFamily("slippery", false);
    public static final PistonFamily SUPER = new PistonFamily("super", false);

    public static Map<String, PistonFamily> getFamilyMap() {
        return ID_TO_FAMILY;
    }

    public static Collection<PistonFamily> getFamilies() {
        return ID_TO_FAMILY.values();
    }

    public static PistonFamily getFamily(String familyId) {
        return ID_TO_FAMILY.get(familyId);
    }

    public static PistonFamily getFamily(BasicPistonHeadBlock headBlock) {
        return PISTON_HEAD_TO_FAMILY.get(headBlock);
    }

    public static void registerBlockId(String familyId, PistonFamily family) {
        PistonFamily blockFamily = ID_TO_FAMILY.put(familyId, family);
        if (blockFamily != null) {
            throw new IllegalStateException("Duplicate piston family definition for: " + familyId);
        }
    }

    public static void registerPistonHead(BasicPistonHeadBlock headBlock, PistonFamily family) {
        PistonFamily blockFamily = PISTON_HEAD_TO_FAMILY.put(headBlock, family);
        if (blockFamily != null) {
            throw new IllegalStateException(
                    "Duplicate piston family definition for: " + BuiltInRegistries.BLOCK.getId(headBlock)
            );
        }
    }

    public static PistonFamily createFamily(String id) {
        return new PistonFamily(id);
    }
}
