package ca.fxco.pistonlib.commands;

import ca.fxco.api.pistonlib.level.ServerLevelInteraction;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.commands.arguments.DirectionArgument;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class PistonLibCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("pistonlib")
            .requires(source -> source.hasPermission(4))
            .then(pistonEventSubCommand(registryAccess, PistonEventType.PUSH)) // Push Command
            .then(pistonEventSubCommand(registryAccess, PistonEventType.PULL)) // Pull Command
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

    public enum PistonEventType {
        PUSH,
        PULL
    }
}
