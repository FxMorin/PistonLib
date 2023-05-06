package ca.fxco.pistonlib.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.NoArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor
public class DirectionArgument implements ArgumentType<Direction> {
    private static final Collection<String> EXAMPLES = Arrays.asList("up", "down", "north", "south", "west", "east");
    public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((object) ->
        Component.translatable("argument.pistonlib.direction.invalid", object)
    );

    public static DirectionArgument direction() {
        return new DirectionArgument();
    }

    public static Direction getDirection(CommandContext<CommandSourceStack> commandContext, String string) {
        return commandContext.getArgument(string, Direction.class);
    }

    public Direction parse(StringReader stringReader) throws CommandSyntaxException {
        String string = stringReader.readUnquotedString().toLowerCase();
        Direction direction = Direction.byName(string);
        if (direction != null) {
            return direction;
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