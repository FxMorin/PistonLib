package ca.fxco.pistonlib.pistonLogic.structureRunners;

import ca.fxco.pistonlib.PistonLibConfig;

public interface StructureRunner {

    void taskRemovePistonHeadOnRetract();

    // Sets the lists, when returning false it means it failed to resolve
    boolean taskRunStructureResolver();

    void taskSetPositionsToMove();

    void taskMergeBlocks();

    void taskDestroyBlocks();

    void taskPreventTntDuping();

    void taskMoveBlocks();

    void taskPlaceExtendingHead();

    void taskRemoveLeftOverBlocks();

    void taskDoRemoveNeighborUpdates();

    void taskDoDestroyNeighborUpdates();

    void taskDoMoveNeighborUpdates();

    void taskDoUnMergeUpdates();

    void taskDoPistonHeadExtendingUpdate();

    default boolean run() {
        taskRemovePistonHeadOnRetract();

        // Create the structure resolver lists
        if (!taskRunStructureResolver()) {
            return false;
        }

        // collect blocks to move
        taskSetPositionsToMove();

        taskMergeBlocks();

        // destroy blocks
        taskDestroyBlocks();

        if (PistonLibConfig.tntDupingFix) {
            taskPreventTntDuping();
        }

        // move blocks
        taskMoveBlocks();

        // place extending head
        taskPlaceExtendingHead();

        // remove left over blocks
        taskRemoveLeftOverBlocks();

        // do remove neighbor updates
        taskDoRemoveNeighborUpdates();

        // do destroy neighbor updates
        taskDoDestroyNeighborUpdates();

        // do move neighbor updates
        taskDoMoveNeighborUpdates();

        // do unmerge neighbor updates
        taskDoUnMergeUpdates();

        // update neighbors at piston head on extending
        taskDoPistonHeadExtendingUpdate();

        return true;
    }
}
