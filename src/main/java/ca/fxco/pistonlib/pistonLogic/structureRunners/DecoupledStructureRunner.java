package ca.fxco.pistonlib.pistonLogic.structureRunners;

import lombok.RequiredArgsConstructor;

/**
 * A Structure Runner Wrapper that decouples the structure runner from having a physical piston
 * Allowing the Piston pushing code to be used anywhere**
 */
@RequiredArgsConstructor
public class DecoupledStructureRunner implements StructureRunner {

    private final StructureRunner structureRunner;

    @Override
    public void taskRemovePistonHeadOnRetract() {
        // No piston head to modify!
    }

    @Override
    public boolean taskRunStructureResolver() {
        return structureRunner.taskRunStructureResolver();
    }

    @Override
    public void taskSetPositionsToMove() {
        structureRunner.taskSetPositionsToMove();
    }

    @Override
    public void taskMergeBlocks() {
        structureRunner.taskMergeBlocks();
    }

    @Override
    public void taskDestroyBlocks() {
        structureRunner.taskDestroyBlocks();
    }

    @Override
    public void taskFixUpdatesAndStates() {
        structureRunner.taskFixUpdatesAndStates();
    }

    @Override
    public void taskMoveBlocks() {
        structureRunner.taskMoveBlocks();
    }

    @Override
    public void taskPlaceExtendingHead() {
        // Don't place a piston head without a piston base!
    }

    @Override
    public void taskRemoveLeftOverBlocks() {
        structureRunner.taskRemoveLeftOverBlocks();
    }

    @Override
    public void taskDoRemoveNeighborUpdates() {
        structureRunner.taskDoRemoveNeighborUpdates();
    }

    @Override
    public void taskDoDestroyNeighborUpdates() {
        structureRunner.taskDoDestroyNeighborUpdates();
    }

    @Override
    public void taskDoMoveNeighborUpdates() {
        structureRunner.taskDoMoveNeighborUpdates();
    }

    @Override
    public void taskDoUnMergeUpdates() {
        structureRunner.taskDoUnMergeUpdates();
    }

    @Override
    public void taskDoPistonHeadExtendingUpdate() {
        structureRunner.taskDoPistonHeadExtendingUpdate();
    }
}
