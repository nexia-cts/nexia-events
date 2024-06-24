package com.nexia.teams.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.teams.TeamUtil;
import com.nexia.teams.utilities.time.ServerTime;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;

public class TeamCommand {
    static String commandSeparator = ": ";

    static String[] commands = {
            "t" + commandSeparator + "Display this message",
            "t create <name>" + commandSeparator + "Create a team",
            "t leave" + commandSeparator + "Leave your current team",
            "t invite <player>" + commandSeparator + "Invite a player to a team",
            "t accept <name>" + commandSeparator + "Accept an invite to a team",
            //"t decline <name>" + commandSeparator + "Decline an invite to a team",
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("t")
                .executes(TeamCommand::info)
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.word()).executes(context -> createTeam(context, StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("create")
                        .executes(TeamCommand::leaveTeam))
                .then(Commands.literal("invite")
                        .then(Commands.argument("player", EntityArgument.player()).executes(context -> invitePlayer(context, EntityArgument.getPlayer(context, "player")))))
                .then(Commands.literal("accept")
                        .executes(TeamCommand::acceptInvite))
        );
    }

    private static int info(CommandContext<CommandSourceStack> context) {
        Component message = Component.text("");

        for (String command : commands) {
            String[] commandInfo = command.split(commandSeparator);
            if (commandInfo.length < 2) continue;

            message = message.append(Component.text("\n/" + commandInfo[0])
                    .color(ChatFormat.brandColor1)
                    .append(Component.text(" | ").color(ChatFormat.Minecraft.aqua))
                    .append(Component.text(commandInfo[1]).color(ChatFormat.brandColor2)));
        }

        context.getSource().sendSystemMessage(ChatFormat.convertComponent(message));

        return 0;
    }

    private static int createTeam(CommandContext<CommandSourceStack> context, String name) {
        if (ServerTime.minecraftServer.getScoreboard().getPlayerTeam(name) != null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That team already exists!"))));
            return 1;
        }

        if (context.getSource().getPlayer().getTeam() != null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You're already in a team!"))));
            return 1;
        }

        PlayerTeam playerTeam = ServerTime.minecraftServer.getScoreboard().addPlayerTeam(name);
        ServerTime.minecraftServer.getScoreboard().addPlayerToTeam(context.getSource().getPlayer().getScoreboardName(), playerTeam);

        return 0;
    }

    private static int leaveTeam(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getPlayer().getTeam() == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You aren't in a team!"))));
            return 1;
        }

        ServerTime.minecraftServer.getScoreboard().removePlayerFromTeam(context.getSource().getPlayer().getScoreboardName(), context.getSource().getPlayer().getTeam());
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You have left your team."))));
        return 0;
    }

    private static int invitePlayer(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        if (context.getSource().getPlayer().getTeam() == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You aren't in a team!"))));
            return 1;
        }

        if (player.getTeam() != null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That player is already in a team!"))));
            return 1;
        }

        if (TeamUtil.getInvitedTeam(player) != null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That player has already been invited to a team!"))));
            return 1;
        }

        TeamUtil.addInvite(player, context.getSource().getPlayer().getTeam().getName());
        player.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You've been invited to %s!".formatted(context.getSource().getPlayer().getTeam().getName())))));
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Invite sent has been sent to %s!".formatted(player.getScoreboardName())))));

        return 0;
    }

    private static int acceptInvite(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        String teamName = TeamUtil.getInvitedTeam(player);

        if (player.getTeam() != null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You're already in a team!"))));
            return 1;
        }

        if (TeamUtil.getInvitedTeam(player) == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You're not invited to a team!"))));
            return 1;
        }

        player.getScoreboard().addPlayerToTeam(player.getScoreboardName(), player.getScoreboard().getPlayerTeam(teamName));

        for (String teamPlayer : player.getScoreboard().getPlayerTeam(teamName).getPlayers()) {
            ServerPlayer serverPlayer = ServerTime.minecraftServer.getPlayerList().getPlayerByName(teamPlayer);

            if (serverPlayer != null) {
                serverPlayer.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("%s has joined your team.".formatted(context.getSource().getPlayer().getScoreboardName())))));
            }
        }

        TeamUtil.removeInvite(player);

        return 0;
    }
}
