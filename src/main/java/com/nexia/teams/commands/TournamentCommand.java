package com.nexia.teams.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nexia.teams.NexiaTeams;
import com.nexia.teams.events.tournament.TournamentFight;
import com.nexia.teams.utilities.chat.ChatFormat;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.world.scores.PlayerTeam;

public class TournamentCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("tournament").requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                .then(Commands.literal("create")
                        .then(Commands.argument("redTeam", TeamArgument.team())
                        .then(Commands.argument("blueTeam", TeamArgument.team())
                        .executes(context -> execute(context, TeamArgument.getTeam(context, "redTeam"), TeamArgument.getTeam(context, "blueTeam"))))))
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context, PlayerTeam redTeam, PlayerTeam blueTeam) {
        TournamentFight.redTeam = redTeam;
        TournamentFight.blueTeam = blueTeam;

        TournamentFight.isStarting = true;
        return 0;
    }
}
