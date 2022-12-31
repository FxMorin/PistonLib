package ca.fxco.configurablepistons.pistonLogic.families;

import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonArmBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonHeadBlock;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

import static ca.fxco.configurablepistons.ConfigurablePistons.id;

public class PistonFamilies {
    private static final Map<String, PistonFamily> ID_TO_FAMILY = Maps.newHashMap();
    private static final Map<BasicPistonHeadBlock, PistonFamily> PISTON_HEAD_TO_FAMILY = Maps.newHashMap();

    public static final PistonFamily BASIC = new PistonFamily("basic", false);
    public static final PistonFamily LONG = new PistonFamily("long", false);
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
                    "Duplicate piston family definition for: " + Registry.BLOCK.getId(headBlock)
            );
        }
    }

    public static PistonFamily createFamily(String id) {
        return new PistonFamily(id);
    }
}
