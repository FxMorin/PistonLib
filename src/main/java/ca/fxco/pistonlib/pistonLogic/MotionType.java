package ca.fxco.pistonlib.pistonLogic;

public class MotionType {

    /**
     * Cancel the motion from happening
     */
    public static final int NONE    = -1;
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

    public static String toString(int type) {
        return switch (type) {
            case -1 -> "NONE";
            case 0 -> "PUSH";
            case 1 -> "PULL";
            case 2 -> "RETRACT";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
