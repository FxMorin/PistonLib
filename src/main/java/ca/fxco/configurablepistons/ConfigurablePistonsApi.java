package ca.fxco.configurablepistons;

import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

/**
 * ConfigurablePistons API
 * <p>
 * This is the main class used to access the ConfigurablePistons API. Providing the easy-to-access methods to easily get started!
 * <p>
 * Here you can register your pistons families, and custom pistons
 */
public class ConfigurablePistonsApi {

    /**
     * Creates a family with a specific id
     */
    public static PistonFamily createFamily(String id) {
        return PistonFamilies.createFamily(id);
    }

    /**
     * When creativeGroup is null, no item will be created for the piston. CreativeGroup is only used for base piston blocks
     */
    public static <T extends Block> T registerPiston(PistonFamily family, T block, @Nullable Item.Settings creativeGroup) {
        return ModBlocks.registerPiston(family, block, creativeGroup);
    }
}
