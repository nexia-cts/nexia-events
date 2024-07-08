package com.nexia.teams.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.teams.TeamUtil;
import com.nexia.teams.utilities.time.ServerTime;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;

public class TeamCommand {
    static String commandSeparator = ": ";

    static String[] commands = {
            "t" + commandSeparator + "Display this message",
            "t create <name>" + commandSeparator + "Create a team",
            "t leave" + commandSeparator + "Leave your current team",
            "t invite <player>" + commandSeparator + "Invite a player to a team",
            "t kick <player>" + commandSeparator + "Kick a player from your team",
            "t accept" + commandSeparator + "Accept an invite to a team",
            "t decline" + commandSeparator + "Decline an invite to a team",
            "t color <color>" + commandSeparator + "Change the prefix color of your team",
            "t list <name>" + commandSeparator + "List of members of a team",
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
                .then(Commands.literal("kick")
                        .then(Commands.argument("player", EntityArgument.player()).executes(context -> kickPlayer(context, EntityArgument.getPlayer(context, "player")))))
                .then(Commands.literal("accept")
                        .executes(TeamCommand::acceptInvite))
                .then(Commands.literal("decline")
                        .executes(TeamCommand::declineInvite))
                .then(Commands.literal("leave")
                        .executes(TeamCommand::leaveTeam))
                .then(Commands.literal("color")
                        .then(Commands.argument("color", ColorArgument.color()).executes(context -> setTeamColor(context, ColorArgument.getColor(context, "color")))))
                .then(Commands.literal("list")
                        .executes(context -> listMembers(context, context.getSource().getPlayer().getTeam()))
                        .then(Commands.argument("team", TeamArgument.team()).executes(context -> listMembers(context, TeamArgument.getTeam(context, "team")))))
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
        if (context.getSource().getPlayer().getTeam() != null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You're already in a team!"))));
            return 1;
        }

        if (ServerTime.minecraftServer.getScoreboard().getPlayerTeam(name) != null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That team already exists!"))));
            return 1;
        }

        if (name.length() > 12) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Team name is too long!"))));
            return 1;
        }

        PlayerTeam playerTeam = ServerTime.minecraftServer.getScoreboard().addPlayerTeam(name);
        playerTeam.setSeeFriendlyInvisibles(true);
        playerTeam.setAllowFriendlyFire(true);
        Component teamPrefix = Component.text(playerTeam.getName()).color(NamedTextColor.WHITE)
                .append(Component.text(" | ").color(ChatFormat.arrowColor));
        playerTeam.setPlayerPrefix(ChatFormat.convertComponent(teamPrefix));
        playerTeam.setColor(ChatFormat.getSecondaryColor(ChatFormatting.WHITE));
        ServerTime.minecraftServer.getScoreboard().addPlayerToTeam(context.getSource().getPlayer().getScoreboardName(), playerTeam);
        context.getSource().getPlayer().addTag("leader_" + playerTeam.getName());
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You have created your own team."))));

        return 0;
    }

    private static int leaveTeam(CommandContext<CommandSourceStack> context) {
        PlayerTeam playerTeam = context.getSource().getPlayer().getTeam();

        if (playerTeam == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You aren't in a team!"))));
            return 1;
        }

        ServerTime.minecraftServer.getScoreboard().removePlayerFromTeam(context.getSource().getPlayer().getScoreboardName(), playerTeam);
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You have left your team."))));

        if (context.getSource().getPlayer().getTags().contains("leader_" + playerTeam.getName())) {
            context.getSource().getPlayer().removeTag("leader_" + playerTeam.getName());
            ServerTime.minecraftServer.getScoreboard().removePlayerTeam(playerTeam);
        }

        return 0;
    }

    private static int invitePlayer(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        PlayerTeam playerTeam = context.getSource().getPlayer().getTeam();

        if (playerTeam == null) {
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

        if (playerTeam.getPlayers().size() >= 3) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Your team has reached the member limit!"))));
            return 1;
        }

        if (!context.getSource().getPlayer().getTags().contains("leader_" + playerTeam.getName())) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You aren't the team leader!"))));
            return 1;
        }

        Component yes = Component.text("[").color(ChatFormat.Minecraft.dark_gray)
                .append(Component.text("ACCEPT")
                        .color(ChatFormat.Minecraft.green)
                        .decorate(TextDecoration.BOLD)
                        .hoverEvent(HoverEvent.showText(Component.text("Click me").color(ChatFormat.brandColor2)))
                        .clickEvent(ClickEvent.runCommand("/t accept")))
                .append(Component.text("]  ").color(ChatFormat.Minecraft.dark_gray)
                );

        Component no = Component.text("[").color(ChatFormat.Minecraft.dark_gray)
                .append(Component.text("DECLINE")
                        .color(ChatFormat.Minecraft.red)
                        .decoration(TextDecoration.BOLD, true)
                        .hoverEvent(HoverEvent.showText(Component.text("Click me")
                                .color(ChatFormat.brandColor2)))
                        .clickEvent(ClickEvent.runCommand("/t decline")))
                .append(Component.text("]").color(ChatFormat.Minecraft.dark_gray)
                );

        TeamUtil.addInvite(player, context.getSource().getPlayer().getTeam().getName());
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Invite sent has been sent to %s!".formatted(player.getScoreboardName())))));
        player.sendSystemMessage(ChatFormat.convertComponent(Component.text("You have been invited to ", ChatFormat.Minecraft.gray).append(Component.text(context.getSource().getPlayer().getTeam().getName(), TextColor.color(context.getSource().getPlayer().getTeam().getColor().getColor())))));
        player.sendSystemMessage(ChatFormat.convertComponent(yes.append(no)));

        return 0;
    }

    private static int kickPlayer(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        PlayerTeam playerTeam = context.getSource().getPlayer().getTeam();

        if (playerTeam == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You aren't in a team!"))));
            return 1;
        }

        if (player.getTeam() != playerTeam) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Can't kick a player from another team!"))));
            return 1;
        }

        if (!context.getSource().getPlayer().getTags().contains("leader_" + playerTeam.getName())) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You aren't the team leader!"))));
            return 1;
        }

        if (context.getSource().getPlayer() == player) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You can't kick yourself!"))));
            return 1;
        }

        ServerTime.minecraftServer.getScoreboard().removePlayerFromTeam(player.getScoreboardName(), playerTeam);
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Player %s has been kicked.".formatted(player.getScoreboardName())))));

        return 0;
    }

    private static int acceptInvite(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        assert player != null;
        String teamName = TeamUtil.getInvitedTeam(player);

        if (player.getTeam() != null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You're already in a team!"))));
            return 1;
        }

        if (teamName == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You're not invited to a team!"))));
            return 1;
        }

        player.getScoreboard().addPlayerToTeam(player.getScoreboardName(), player.getScoreboard().getPlayerTeam(teamName));

        for (String teamPlayer : ServerTime.minecraftServer.getScoreboard().getPlayerTeam(teamName).getPlayers()) {
            ServerPlayer serverPlayer = ServerTime.minecraftServer.getPlayerList().getPlayerByName(teamPlayer);

            if (serverPlayer != null) {
                serverPlayer.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("%s has joined your team.".formatted(context.getSource().getPlayer().getScoreboardName())))));
            }
        }

        player.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You have accepted the invite."))));
        TeamUtil.removeInvite(player);

        return 0;
    }

    private static int declineInvite(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        assert player != null;
        String teamName = TeamUtil.getInvitedTeam(player);

        if (teamName == null) {
            player.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You're not invited to a team!"))));
            return 1;
        }

        for (String teamPlayer : ServerTime.minecraftServer.getScoreboard().getPlayerTeam(teamName).getPlayers()) {
            ServerPlayer serverPlayer = ServerTime.minecraftServer.getPlayerList().getPlayerByName(teamPlayer);

            if (serverPlayer != null) {
                serverPlayer.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("%s hasn't joined your team.".formatted(context.getSource().getPlayer().getScoreboardName())))));
            }
        }

        player.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You have declined the invite."))));
        TeamUtil.removeInvite(player);

        return 0;
    }


    private static int setTeamColor(CommandContext<CommandSourceStack> context, ChatFormatting color) {
        PlayerTeam team = context.getSource().getPlayer().getTeam();

        if (team == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You aren't in a team!"))));
            return 1;
        }

        Component teamPrefix = Component.text(team.getName()).color(TextColor.color(color.getColor()))
                .append(Component.text(" | ").color(ChatFormat.arrowColor));
        team.setPlayerPrefix(ChatFormat.convertComponent(teamPrefix));
        team.setColor(ChatFormat.getSecondaryColor(color));

        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Changed team prefix color!"))));

        return 0;
    }

    private static int listMembers(CommandContext<CommandSourceStack> context, PlayerTeam playerTeam) {
        if (playerTeam == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You aren't in a team!"))));
            return 1;
        }

        Component message = Component.text("List of members in %s:".formatted(playerTeam.getName()));

        for (String teamPlayer : playerTeam.getPlayers()) {
            message = message.append(Component.text("\n" + teamPlayer));
        }

        context.getSource().sendSystemMessage(ChatFormat.convertComponent(message));
        return 0;
    }
}
