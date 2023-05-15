package ca.fxco.pistonlib.pistonLogic.internal;

import ca.fxco.pistonlib.helpers.PistonLibBehaviorManager;

public interface BlockStateBaseMoveBehavior {

    PistonLibBehaviorManager.PistonMoveBehavior getPistonMoveBehaviorOverride();

    void setPistonMoveBehaviorOverride(PistonLibBehaviorManager.PistonMoveBehavior override);

}
