package ca.fxco.pistonlib.blocks.gametest;

import ca.fxco.pistonlib.gametest.expansion.BlockStateSuggestions;
import ca.fxco.pistonlib.helpers.BlockStateExp;
import ca.fxco.pistonlib.network.PLNetwork;
import ca.fxco.pistonlib.network.packets.ServerboundSetCheckStatePacket;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CheckStateScreen extends Screen {

    private final CheckStateBlockEntity blockEntity;
    private EditBox tickEdit;
    private CycleButton<Direction> directionCycleButton;
    private EditBox stateEdit;
    private BlockStateSuggestions blockStateSuggestions;
    private Checkbox failOnFoundCheckbox;

    protected CheckStateScreen(CheckStateBlockEntity blockEntity) {
        super(Component.translatable("screen.pistonlib.check_state_block.title"));
        this.blockEntity = blockEntity;
    }

    @Override
    public void tick() {
        this.tickEdit.tick();
        this.stateEdit.tick();
    }

    private void onDone() {
        HolderLookup<Block> holderLookup = BuiltInRegistries.BLOCK.asLookup();
        try {
            String stateValue = this.stateEdit.getValue();
            BlockState state = BlockStateParser.parseForBlock(holderLookup, stateValue, true).blockState();
            // Has properties, matches specific state. - Has no properties, only matches block
            BlockStateExp blockStateExp = BlockStateExp.of(state, stateValue.indexOf('[') == -1);
            ServerboundSetCheckStatePacket packet = new ServerboundSetCheckStatePacket(
                    this.blockEntity.getBlockPos(),
                    Integer.parseInt(this.tickEdit.getValue()),
                    this.failOnFoundCheckbox.selected(),
                    this.directionCycleButton.getValue(),
                    blockStateExp
            );
            PLNetwork.sendToServer(packet);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        } // Just don't send if validation failed
        this.minecraft.setScreen(null);
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.onDone();
        }).bounds(this.width / 2 - 4 - 150, 210, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
            this.minecraft.setScreen(null);
        }).bounds(this.width / 2 + 4, 210, 150, 20).build());

        this.tickEdit = new EditBox(this.font, this.width / 2 - 150 - 4, 50, 150, 20, Component.translatable("screen.pistonlib.check_state_block.tick"));
        this.tickEdit.setMaxLength(6);
        this.tickEdit.setValue("" + this.blockEntity.getTick());
        this.addWidget(this.tickEdit);
        //this.font, this.width / 2 + 4, 50, 150, 20, Component.translatable("screen.pistonlib.pulse_state_block.duration")
        this.directionCycleButton = new CycleButton.Builder<Direction>(dir -> Component.literal(dir.toString()))
                .withInitialValue(this.blockEntity.getDirection())
                .withValues(Direction.values())
                .create(this.width / 2 + 4, 50, 150, 20, Component.translatable("screen.pistonlib.check_state_block.direction"));
        this.addWidget(this.directionCycleButton);
        this.stateEdit = new EditBox(this.font, this.width / 2 - 154, 90, 308, 20, Component.translatable("screen.pistonlib.check_state_block.state"));
        this.stateEdit.setMaxLength(255);
        this.stateEdit.setValue(this.blockEntity.getBlockStateExp().asString());
        this.addWidget(this.stateEdit);
        this.failOnFoundCheckbox = new Checkbox(this.width / 2 - 154, 130, 100, 20, Component.translatable("screen.pistonlib.check_state_block.failOnFind"), this.blockEntity.isFailOnFound());
        this.addWidget(this.failOnFoundCheckbox);

        this.setInitialFocus(this.stateEdit);

        this.blockStateSuggestions = new BlockStateSuggestions(this.minecraft, this, this.stateEdit, this.font, true, false, 0, 7, false, Integer.MIN_VALUE);
        this.blockStateSuggestions.setAllowSuggestions(true);
        this.blockStateSuggestions.updateCommandInfo();

        this.stateEdit.setResponder(str -> this.blockStateSuggestions.updateCommandInfo());
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (this.blockStateSuggestions.keyPressed(i, j, k)) {
            return true;
        } else if (super.keyPressed(i, j, k)) {
            return true;
        } else if (i != 257 && i != 335) {
            return false;
        }
        this.onDone();
        return true;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        return this.blockStateSuggestions.mouseScrolled(f) || super.mouseScrolled(d, e, f);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        return this.blockStateSuggestions.mouseClicked(d, e, i) || super.mouseClicked(d, e, i);
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 10, 16777215);

        drawString(poseStack, this.font, Component.translatable("screen.pistonlib.check_state_block.tick"), this.width / 2 - 153, 40, 10526880);
        this.tickEdit.render(poseStack, i, j, f);
        drawString(poseStack, this.font, Component.translatable("screen.pistonlib.check_state_block.direction"), this.width / 2 + 4, 40, 10526880);
        this.directionCycleButton.render(poseStack, i, j, f);
        drawString(poseStack, this.font, Component.translatable("screen.pistonlib.check_state_block.state"), this.width / 2 - 153, 80, 10526880);
        this.stateEdit.render(poseStack, i, j, f);
        this.failOnFoundCheckbox.render(poseStack, i, j, f);

        super.render(poseStack, i, j, f);
        this.blockStateSuggestions.render(poseStack, i, j);
    }
}
