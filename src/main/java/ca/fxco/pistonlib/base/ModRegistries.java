package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

import net.minecraft.core.Registry;

import static ca.fxco.pistonlib.PistonLib.id;

public class ModRegistries {

    public static final Registry<PistonFamily> PISTON_FAMILY = FabricRegistryBuilder.createSimple(PistonFamily.class, id("piston_type")).buildAndRegister();
    public static final Registry<StickyGroup>  STICKY_GROUP  = FabricRegistryBuilder.createSimple(StickyGroup.class, id("sticky_group")).buildAndRegister();

    public static void bootstrap() { }

}
