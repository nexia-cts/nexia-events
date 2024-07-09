package com.nexia.teams.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.nexia.teams.events.koth.KothGame;
import com.nexia.teams.events.koth.KothGameHandler;
import net.minecraft.commands.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

public class KothSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        if (KothGameHandler.kothGames.isEmpty()) {
            return null;
        }

        for (KothGame kothGame : KothGameHandler.kothGames) {
            builder.suggest(kothGame.name);
        }

        return builder.buildFuture();
    }
}