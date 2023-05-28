package ca.fxco.pistonlib.gametest;

import ca.fxco.api.gametestlib.control.GameTestControl;
import ca.fxco.api.gametestlib.progressbar.DefaultProgressBar;
import ca.fxco.api.gametestlib.progressbar.GameTestProgressBar;
import ca.fxco.gametestlib.GameTestLibMod;
import ca.fxco.pistonlib.PistonLib;
import org.jetbrains.annotations.Nullable;

public class PistonLibTestControl implements GameTestControl {

    @Override
    public void onInitialize() {
        GameTestLibMod.setDevResources(PistonLib.MOD_ID);
    }

    @Nullable
    public GameTestProgressBar registerGameTestProgressBar() {
        return new DefaultProgressBar();
    }
}
