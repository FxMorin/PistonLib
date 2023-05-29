package ca.fxco.pistonlib.items;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.DebugStructureResolver;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

// TODO: Make this entire thing more visual, show the issues in the world with debug renderers instead of in text form
public class PistonDebugWandItem extends PistonWandItem {

    public PistonDebugWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        if (player == null || !player.canUseGameMasterBlocks()) { //todo: change later
            return InteractionResult.FAIL;
        }
        Level level = useOnContext.getLevel();
        if (level instanceof ServerLevel) {
            ItemStack wandItem = getWandItem(useOnContext.getItemInHand());
            if (wandItem == ItemStack.EMPTY) {
                ((ServerPlayer) player).sendSystemMessage(Component.literal("Piston Debug Wand does not currently have a piston!"), true); // todo: translations
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            BlockPos blockPos = useOnContext.getClickedPos();
            Direction face = useOnContext.getClickedFace();
            List<Component> output = new ArrayList<>();
            output.add(Component.literal("BlockPos: ").withStyle(ChatFormatting.BOLD)
                    .append(Component.literal(blockPos.toString()).withStyle(ChatFormatting.RESET)));
            output.add(Component.literal("PushDirection: ").withStyle(ChatFormatting.BOLD)
                    .append(Component.literal(face.getOpposite().toString()).withStyle(ChatFormatting.RESET)));
            DebugStructureResolver resolver = new DebugStructureResolver(
                    (BasicPistonBaseBlock) ((BlockItem) wandItem.getItem()).getBlock(),
                    level,
                    blockPos.relative(face),
                    face.getOpposite(),
                    1,
                    true
            );
            boolean canPush = resolver.resolve();
            output.add(Component.literal("CanPush: ").withStyle(ChatFormatting.BOLD)
                    .append(Component.literal("").withStyle(ChatFormatting.RESET))
                    .append(Component.literal("" + canPush).withStyle(canPush ? ChatFormatting.GREEN : ChatFormatting.RED)));
            if (!canPush) {
                switch (resolver.getResult()) {
                    case FAIL_IMMOVABLE -> output.add(Component.literal("Failed to move block at: ").withStyle(ChatFormatting.BOLD)
                            .append(Component.literal(resolver.getResultPos().toString()).withStyle(ChatFormatting.RESET)));
                    case FAIL_MOVELINE -> output.add(Component.literal("Failed to move line at: ").withStyle(ChatFormatting.BOLD)
                            .append(Component.literal(resolver.getResultPos().toString()).withStyle(ChatFormatting.RESET)));
                }
            }
            ServerPlayer serverPlayer = (ServerPlayer) player;
            for (Component comp : output) {
                serverPlayer.sendSystemMessage(comp, false);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
