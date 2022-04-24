package ca.fxco.configurablepistons.helpers;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.base.BasicPistonBlock;
import ca.fxco.configurablepistons.base.BasicPistonHeadBlock;

// TODO: Stop using this jank system, holy
public class PistonFamily {
    private final BasicPistonBlock basicPiston;
    private final BasicPistonBlock basicStickyPiston;
    private final BasicPistonHeadBlock headBlock;

    public PistonFamily(BasicPistonBlock basicPiston, BasicPistonBlock basicStickyPiston) {
        this(basicPiston, basicStickyPiston, ConfigurablePistons.BASIC_PISTON_HEAD);
    }

    PistonFamily(BasicPistonBlock basicPiston, BasicPistonBlock basicStickyPiston, BasicPistonHeadBlock headBlock) {
        this.basicPiston = basicPiston;
        this.basicStickyPiston = basicStickyPiston;
        this.headBlock = headBlock;
    }
    public BasicPistonBlock getPistonBlock() {
        return basicPiston;
    }
    public BasicPistonBlock getStickyPistonBlock() {
        return basicStickyPiston;
    }
    public BasicPistonHeadBlock getPistonHeadBlock() {
        return headBlock;
    }
}
