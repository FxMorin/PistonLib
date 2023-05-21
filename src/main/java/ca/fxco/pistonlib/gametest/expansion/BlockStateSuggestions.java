package ca.fxco.pistonlib.gametest.expansion;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.SneakyThrows;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlockStateSuggestions extends CommandSuggestions {

    private static final Style LITERAL_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
    private static final Style BRACKET_STYLE = Style.EMPTY.withColor(ChatFormatting.AQUA);
    private static final Style NBT_BRACKET_STYLE = Style.EMPTY.withColor(ChatFormatting.GREEN);
    private static final Style ERROR_STYLE = Style.EMPTY.withColor(ChatFormatting.RED);

    public BlockStateSuggestions(Minecraft minecraft, Screen screen, EditBox editBox, Font font, boolean bl, boolean bl2, int i, int j, boolean bl3, int k) {
        super(minecraft, screen, editBox, font, bl, bl2, i, j, bl3, k);
    }

    @Override
    public void showSuggestions(boolean bl) {
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
            Suggestions suggestions = this.pendingSuggestions.join();
            if (!suggestions.isEmpty()) {
                int i = 0;

                Suggestion suggestion;
                for(Iterator<Suggestion> var4 = suggestions.getList().iterator(); var4.hasNext(); i = Math.max(i, this.font.width(suggestion.getText()))) {
                    suggestion = var4.next();
                }

                int j = Mth.clamp(this.input.getScreenX(suggestions.getRange().getStart()), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - i);
                int k = this.input.getY() + this.input.getHeight();
                this.suggestions = new SuggestionsList(j, k, i, this.sortSuggestions(suggestions), bl);
            }
        }

    }

    @Override
    public void updateCommandInfo() {
        String string = this.input.getValue();
        if (!this.keepSuggestions) {
            this.input.setSuggestion(null);
            this.suggestions = null;
        }
        this.commandUsage.clear();
        HolderLookup<Block> holderLookup = BuiltInRegistries.BLOCK.asLookup();
        SuggestionsBuilder suggestionsBuilder = new SuggestionsBuilder(string, 0);
        this.pendingSuggestions = BlockStateParser.fillSuggestions(holderLookup, suggestionsBuilder, false, true);
        this.showSuggestions(true);
    }

    @Override
    public void updateUsageInfo() {
        this.commandUsagePosition = 0;
        this.commandUsageWidth = this.screen.width;
        if (this.commandUsage.isEmpty()) {
            this.fillNodeUsage(ChatFormatting.GRAY);
        }

        this.suggestions = null;
        if (this.allowSuggestions && this.minecraft.options.autoSuggestions().get()) {
            this.showSuggestions(false);
        }
    }

    @SneakyThrows
    @Override
    public final void fillNodeUsage(ChatFormatting chatFormatting) {
        String string = this.input.getValue();
        Suggestions suggestions = this.pendingSuggestions.get();
        List<Suggestion> suggestionList = suggestions.getList();
        List<String> suggestionList2 = new ArrayList<>();
        for (Suggestion suggestion : suggestionList) {
            suggestionList2.add(suggestion.apply(string));
        }
        List<FormattedCharSequence> list = Lists.newArrayList();
        int i = 0;
        Style style = Style.EMPTY.withColor(chatFormatting);

        for (String suggestion : suggestionList2) {
            list.add(FormattedCharSequence.forward(suggestion, style));
            i = Math.max(i, this.font.width(suggestion));
        }

        if (!list.isEmpty()) {
            this.commandUsage.addAll(list);
            this.commandUsagePosition = Mth.clamp(this.input.getScreenX(this.input.getX()), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - i);
            this.commandUsageWidth = i;
        }
    }

    @Override
    public void renderUsage(PoseStack poseStack) {
        for (FormattedCharSequence formattedCharSequence : this.commandUsage) {
            int j = this.input.getY() + this.input.getHeight();
            System.out.println(j);
            GuiComponent.fill(poseStack, this.commandUsagePosition - 1, j, this.commandUsagePosition + this.commandUsageWidth + 1, j + 12, this.fillColor);
            this.font.drawShadow(poseStack, formattedCharSequence, (float) this.commandUsagePosition, (float) (j + 2), -1);
        }
    }

    @Override
    public FormattedCharSequence formatChat(String string, int i) {
        List<FormattedCharSequence> list = Lists.newArrayList();
        int firstBracket = string.indexOf('[');
        if (firstBracket != -1) {
            list.add(FormattedCharSequence.forward(string.substring(0, firstBracket), LITERAL_STYLE));
            int lastBracket = string.indexOf(']');
            if (lastBracket != -1) {
                list.add(FormattedCharSequence.forward(string.substring(firstBracket, lastBracket + 1), BRACKET_STYLE));
            } else {
                list.add(FormattedCharSequence.forward(string.substring(firstBracket), ERROR_STYLE));
            }
        } else {
            list.add(FormattedCharSequence.forward(string, LITERAL_STYLE));
        }
        firstBracket = string.indexOf('{');
        if (firstBracket != -1) {
            int lastBracket = string.indexOf('}');
            if (lastBracket != -1) {
                list.add(FormattedCharSequence.forward(string.substring(firstBracket, lastBracket + 1), NBT_BRACKET_STYLE));
            } else {
                list.add(FormattedCharSequence.forward(string.substring(firstBracket), ERROR_STYLE));
            }
        }
        return FormattedCharSequence.composite(list);
    }
}
