package ca.fxco.pistonlib.commands.arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import ca.fxco.pistonlib.helpers.PistonLibBehaviorManager;

import lombok.NoArgsConstructor;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

@NoArgsConstructor
public class PistonMoveBehaviorArgument implements ArgumentType<PistonLibBehaviorManager.PistonMoveBehavior> {
    private static final Collection<String> EXAMPLES = Arrays.asList("default", "normal", "destroy", "block", "ignore", "push_only");
    public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((object) ->
        Component.translatable("argument.pistonlib.pistonMoveBehavior.invalid", object)
    );

    public static PistonMoveBehaviorArgument pistonMoveBehavior() {
        return new PistonMoveBehaviorArgument();
    }

    public static PistonLibBehaviorManager.PistonMoveBehavior getPistonMoveBehavior(CommandContext<CommandSourceStack> commandContext, String string) {
        return commandContext.getArgument(string, PistonLibBehaviorManager.PistonMoveBehavior.class);
    }

    public PistonLibBehaviorManager.PistonMoveBehavior parse(StringReader stringReader) throws CommandSyntaxException {
        String string = stringReader.readUnquotedString().toLowerCase();
        PistonLibBehaviorManager.PistonMoveBehavior behavior = PistonLibBehaviorManager.PistonMoveBehavior.fromName(string);
        if (behavior != null) {
            return behavior;
        }
        throw ERROR_INVALID_VALUE.create(string);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(EXAMPLES, suggestionsBuilder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
