package ca.fxco.configurablepistons.pistonLogic;

public class MotionType {

    /**
     * Extend and push any blocks in front.
     */
    public static final int PUSH    = 0;
    /**
     * Retract and pull any blocks in front.
     */
    public static final int PULL    = 1;
    /**
     * Retract without pulling any blocks.
     */
    public static final int RETRACT = 2;

    public static boolean isExtend(int type) {
        return type == PUSH;
    }

    public static boolean isRetract(int type) {
        return type == PULL || type == RETRACT;
    }
}
