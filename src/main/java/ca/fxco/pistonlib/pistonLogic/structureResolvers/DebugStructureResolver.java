package ca.fxco.pistonlib.pistonLogic.structureResolvers;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class DebugStructureResolver extends BasicStructureResolver {

    @Getter
    private ResolverResult result = ResolverResult.PASS;
    @Getter
    private BlockPos resultPos = BlockPos.ZERO;

    public DebugStructureResolver(BasicPistonBaseBlock piston, Level level, BlockPos pos,
                                  Direction facing, int length, boolean extend) {
        super(piston, level, pos, facing, length, extend);
    }

    protected boolean runStructureGeneration() {
        // Structure Generation
        BlockState state = this.level.getBlockState(this.startPos);
        if (!this.piston.canMoveBlock(state, this.level, this.startPos, this.pushDirection, false, this.pistonDirection)) {
            // Block directly int front is immovable, can only be true if extending, and it can be destroyed
            if (this.extending) {
                ConfigurablePistonBehavior pistonBehavior = (ConfigurablePistonBehavior)state.getBlock();
                if (pistonBehavior.usesConfigurablePistonBehavior()) {
                    if (pistonBehavior.canDestroy(this.level, this.startPos, state)) {
                        this.toDestroy.add(this.startPos);
                        return true;
                    }
                } else if (state.getPistonPushReaction() == PushReaction.DESTROY) {
                    this.toDestroy.add(this.startPos);
                    return true;
                }
            }
            addResultFail(ResolverResult.FAIL_IMMOVABLE, this.startPos);
            return false;
        } else { // Start block is movable, now check if it's possible to move the rest, while generating the structure
            if (this.cantMove(this.startPos, !this.extending ? this.pushDirection.getOpposite() : this.pushDirection)) {
                addResultFail(ResolverResult.FAIL_IMMOVABLE, this.startPos); // TODO: Move this into the cantMove method for more precision
                return false;
            }
        }

        // This loops through the blocks to push and creates the branches
        for (int i = 0; i < this.toPush.size(); ++i) {
            BlockPos blockPos = this.toPush.get(i);
            state = this.level.getBlockState(blockPos);
            ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness) state.getBlock();
            if (!attemptMove(stick, state, blockPos)) {
                addResultFail(ResolverResult.FAIL_MOVELINE, blockPos); // TODO: Move this into the attemptMove method for more precision
                return false;
            }
        }
        return true;
    }

    private void addResultFail(ResolverResult result, BlockPos resultPos) {
        this.result = result;
        this.resultPos = resultPos;
    }

    public enum ResolverResult {
        PASS,
        //FAIL_PUSHLIMIT,
        FAIL_IMMOVABLE,
        FAIL_MOVELINE;
    }
}
