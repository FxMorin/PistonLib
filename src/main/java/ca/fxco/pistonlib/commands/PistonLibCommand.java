package ca.fxco.pistonlib.commands;

import ca.fxco.api.pistonlib.level.ServerLevelInteraction;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.commands.arguments.DirectionArgument;
import ca.fxco.pistonlib.helpers.BlockUtils;
import ca.fxco.pistonlib.helpers.PistonLibBehaviorManager;
import ca.fxco.pistonlib.helpers.PistonLibBehaviorManager.PistonMoveBehavior;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Collection;
import java.util.LinkedList;

public class PistonLibCommand implements Command {

    private static final String[] BEHAVIOR_NAMES;

    static {
        PistonMoveBehavior[] behaviors = PistonMoveBehavior.ALL;
        BEHAVIOR_NAMES = new String[behaviors.length];

        for (PistonMoveBehavior behavior : behaviors) {
            BEHAVIOR_NAMES[behavior.getIndex()] = behavior.getName();
        }
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("pistonlib")
            .requires(source -> source.hasPermission(4))
            .then(pistonEventSubCommand(registryAccess, PistonEventType.PUSH)) // Push Command
            .then(pistonEventSubCommand(registryAccess, PistonEventType.PULL)) // Pull Command
            .then(Commands.literal("behavior")
                .then(Commands.argument("block", BlockStateArgument.block(registryAccess))
                    .executes(context -> query(context.getSource(), BlockStateArgument.getBlock(context, "block")))
                    .then(Commands.literal("default")
                        .executes(ctx -> setOverride(ctx.getSource(), BlockStateArgument.getBlock(ctx, "block"), PistonMoveBehavior.DEFAULT))
                    )
                    .then(Commands.
                            argument("behavior", StringArgumentType.word()).
                            suggests((context, suggestionsBuilder) -> SharedSuggestionProvider.suggest(BEHAVIOR_NAMES, suggestionsBuilder)).
                            executes(context -> setOverride(context.getSource(), BlockStateArgument.getBlock(context, "block"), parsePistonMoveBehavior(StringArgumentType.getString(context, "behavior"))))
                    )
                )
            )
        );
    }

    private LiteralArgumentBuilder<CommandSourceStack> pistonEventSubCommand(CommandBuildContext registryAccess, PistonEventType eventType) {
        return Commands.literal(eventType.name().toLowerCase())
            .executes(ctx ->
                runPistonEvent(
                    ctx.getSource(),
                    null,
                    null,
                    null,
                    eventType
                )
            )
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .then(Commands.argument("towards", DirectionArgument.direction())
                    .executes(ctx ->
                        runPistonEvent(
                            ctx.getSource(),
                            GlobalPos.of(
                                ctx.getSource().getLevel().dimension(),
                                BlockPosArgument.getLoadedBlockPos(ctx, "pos")
                            ),
                            DirectionArgument.getDirection(ctx, "towards"),
                            null,
                            eventType
                        )
                    )
                    .then(Commands.literal("in")
                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                            .executes(ctx ->
                                runPistonEvent(
                                    ctx.getSource(),
                                    GlobalPos.of(
                                        DimensionArgument.getDimension(ctx, "dimension").dimension(),
                                        BlockPosArgument.getLoadedBlockPos(ctx, "pos")
                                    ),
                                    DirectionArgument.getDirection(ctx, "towards"),
                                    null,
                                    eventType
                                )
                            )
                        )
                    )
                    .then(Commands.argument("pistonBlock", BlockStateArgument.block(registryAccess))
                        .executes(ctx ->
                            runPistonEvent(
                                ctx.getSource(),
                                GlobalPos.of(
                                    ctx.getSource().getLevel().dimension(),
                                    BlockPosArgument.getLoadedBlockPos(ctx, "pos")
                                ),
                                DirectionArgument.getDirection(ctx, "towards"),
                                BlockStateArgument.getBlock(ctx, "pistonBlock"),
                                eventType
                            )
                        )
                        .then(Commands.literal("in")
                            .then(Commands.argument("dimension", DimensionArgument.dimension())
                                .executes(ctx ->
                                    runPistonEvent(
                                        ctx.getSource(),
                                        GlobalPos.of(
                                            DimensionArgument.getDimension(ctx, "dimension").dimension(),
                                            BlockPosArgument.getLoadedBlockPos(ctx, "pos")
                                        ),
                                        DirectionArgument.getDirection(ctx, "towards"),
                                        BlockStateArgument.getBlock(ctx, "pistonBlock"),
                                        eventType
                                    )
                                )
                            )
                        )
                    )
                )
            );
    }

    private static PistonMoveBehavior parsePistonMoveBehavior(String name) throws CommandSyntaxException {
        PistonMoveBehavior behavior = PistonMoveBehavior.fromName(name);

        if (behavior == null) {
            throw new SimpleCommandExceptionType(Component.literal("Unknown PistonLib behavior: " + name)).create();
        }

        return behavior;
    }

    private static int runPistonEvent(CommandSourceStack commandSourceStack, GlobalPos globalPos, Direction facing, BlockInput blockInput, PistonEventType eventType) throws CommandSyntaxException {
        Block block = blockInput == null ? ModBlocks.BASIC_STICKY_PISTON : blockInput.getState().getBlock();
        if (!(block instanceof BasicPistonBaseBlock basicPistonBaseBlock)) {
            throw new SimpleCommandExceptionType(Component.translatable("commands.pistonlib.notPistonBlock", block)).create();
        }
        BlockPos blockPos;
        ServerLevel serverLevel;
        if (globalPos == null || facing == null) {
            ServerPlayer player = commandSourceStack.getPlayerOrException();
            HitResult hitResult = player.pick(Minecraft.getInstance().gameMode.getPickRange(), 1.0F, false);
            if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult blockHitResult) {
                Direction face = blockHitResult.getDirection();
                blockPos = blockHitResult.getBlockPos();
                ((ServerLevelInteraction) commandSourceStack.getLevel()).triggerPistonEvent(basicPistonBaseBlock, blockPos.relative(face), face.getOpposite(), true); // todo: implement pulling
                commandSourceStack.sendSuccess(Component.translatable("commands.pistonlib.push.success", blockPos.getX(), blockPos.getY(), blockPos.getZ(), face.getName()), true);
                return 1;
            } else {
                if (globalPos == null) {
                    blockPos = player.blockPosition();
                    serverLevel = commandSourceStack.getLevel();
                } else {
                    blockPos = globalPos.pos();
                    serverLevel = commandSourceStack.getServer().getLevel(globalPos.dimension());
                }
                if (facing == null) {
                    facing = player.getDirection();
                }
            }
        } else {
            blockPos = globalPos.pos();
            serverLevel = commandSourceStack.getServer().getLevel(globalPos.dimension());
        }
        facing = facing.getOpposite();
        ((ServerLevelInteraction) serverLevel).triggerPistonEvent(basicPistonBaseBlock, blockPos.relative(facing), facing.getOpposite(), true); // todo: implement pulling
        commandSourceStack.sendSuccess(Component.translatable("commands.pistonlib.push.success", blockPos.getX(), blockPos.getY(), blockPos.getZ(), facing.getName()), true);
        return 1;
    }

    private static int query(CommandSourceStack source, BlockInput input) {
        BlockState state = input.getState();
        PushReaction pushReaction = state.getPistonPushReaction();
        PistonMoveBehavior behavior = PistonMoveBehavior.fromPushReaction(pushReaction);
        PistonMoveBehavior override = PistonLibBehaviorManager.getOverride(state);

        MutableComponent message = Component.
                literal("block state ").
                append(Component.
                        literal(BlockUtils.blockStateAsString(state)).
                        withStyle(ChatFormatting.YELLOW)).
                append(" has piston move behavior ").
                append(Component.
                        literal(behavior.getName()).
                        append(" (").
                        append(override.isPresent() ? "modified" : "vanilla").
                        append(")").
                        withStyle(override.isPresent() ? ChatFormatting.GOLD : ChatFormatting.GREEN, ChatFormatting.BOLD));
        source.sendSuccess(message, false);

        return 1;
    }

    private static int setOverride(CommandSourceStack source, BlockInput input, PistonMoveBehavior override) throws CommandSyntaxException {
        BlockState state = input.getState();
        PistonMoveBehavior currentOverride = PistonLibBehaviorManager.getOverride(state);
        if (override == currentOverride) {
            MutableComponent message = Component.
                    literal("State is already set to override: ").
                    append(Component.
                            literal(override.getName()).
                            withStyle(override == PistonMoveBehavior.DEFAULT ? ChatFormatting.GREEN : ChatFormatting.GOLD, ChatFormatting.BOLD));
            source.sendSuccess(message, true);
            return 0;
        }
        Collection<Property<?>> properties = input.getDefinedProperties();
        Collection<BlockState> states = collectMatchingBlockStates(state, properties);

        for (BlockState blockState : states) {
            PistonLibBehaviorManager.setOverride(blockState, override);
        }

        String stateString = BlockUtils.blockStateAsString(state, properties);

        MutableComponent message = Component.
                literal("set the ").
                append("piston move behavior override of all block states matching ").
                append(Component.
                        literal(stateString).
                        withStyle(ChatFormatting.YELLOW)).
                append(" to ").
                append(Component.
                        literal(override.getName()).
                        withStyle(override == PistonMoveBehavior.DEFAULT ? ChatFormatting.GREEN : ChatFormatting.GOLD, ChatFormatting.BOLD));

        source.sendSuccess(message, true);

        return 1;
    }

    private static Collection<BlockState> collectMatchingBlockStates(BlockState state, Collection<Property<?>> properties) throws CommandSyntaxException {
        Collection<BlockState> states = new LinkedList<>();

        for (BlockState blockState : state.getBlock().getStateDefinition().getPossibleStates()) {
            if (blockStatesMatchProperties(state, blockState, properties)) {
                if (!PistonLibBehaviorManager.canChangeOverride(blockState)) {
                    throw new SimpleCommandExceptionType(Component.literal("Cannot change the piston move behavior of " + BlockUtils.blockStateAsString(blockState))).create();
                }

                states.add(blockState);
            }
        }

        return states;
    }

    private static boolean blockStatesMatchProperties(BlockState state1, BlockState state2, Collection<Property<?>> properties) {
        for (Property<?> property : properties) {
            if (state1.getValue(property) != state2.getValue(property)) {
                return false;
            }
        }

        return true;
    }

    public enum PistonEventType {
        PUSH,
        PULL
    }
}
