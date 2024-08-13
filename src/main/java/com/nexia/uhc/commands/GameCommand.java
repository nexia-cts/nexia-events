package com.nexia.uhc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nexia.nexus.api.command.CommandSourceInfo;
import com.nexia.nexus.api.command.CommandUtils;
import com.nexia.nexus.api.world.types.Minecraft;
import com.nexia.uhc.NexiaUHC;
import com.nexia.uhc.utility.ChatFormat;
import net.kyori.adventure.text.Component;

public class GameCommand {
    public void registerCommand(CommandDispatcher<CommandSourceInfo> commandDispatcher) {
        commandDispatcher.register(
                CommandUtils.literal("uhc").requires(commandSourceInfo -> commandSourceInfo.getSender().getPermissionLevel() > 2)
                        .executes(GameCommand::help)
                        .then(CommandUtils.literal("start")
                                .executes(GameCommand::start))
                        .then(CommandUtils.literal("end")
                                .executes(GameCommand::end))
        );
    }

    private static int help(CommandContext<CommandSourceInfo> context) {
        Component helpMessage = ChatFormat.nexia
                .append(Component.text("\nuhc start").append(ChatFormat.arrow.append(Component.text("Starts the UHC."))))
                .append(Component.text("\nuhc end").append(ChatFormat.arrow.append(Component.text("Ends the UHC."))));

        context.getSource().sendMessage(helpMessage);
        return 0;
    }

    private static int start(CommandContext<CommandSourceInfo> context) throws CommandSyntaxException {
        if (!NexiaUHC.manager.getGames().isEmpty()) {
            context.getSource().sendMessage(Component.text("There is already an ongoing UHC game!").color(ChatFormat.failColor));
            context.getSource().getPlayerOrException().playSound(Minecraft.Sound.NOTE_BLOCK_BASS, 1, 1);
            return 1;
        }

        if (NexiaUHC.lobby.getPlayers().size() <= 1) {
            context.getSource().sendMessage(Component.text("Not enough players!").color(ChatFormat.failColor));
            context.getSource().getPlayerOrException().playSound(Minecraft.Sound.NOTE_BLOCK_BASS, 1, 1);
            return 1;
        }

        NexiaUHC.manager.createGame();
        context.getSource().sendMessage(ChatFormat.nexiaMessage.append(Component.text("Successfully started UHC game.")));
        return 0;
    }

    private static int end(CommandContext<CommandSourceInfo> context) throws CommandSyntaxException {
        if (NexiaUHC.manager.getGames().isEmpty()) {
            context.getSource().sendMessage(Component.text("There isn't an ongoing UHC game!").color(ChatFormat.failColor));
            context.getSource().getPlayerOrException().playSound(Minecraft.Sound.NOTE_BLOCK_BASS, 1, 1);
            return 1;
        }

        NexiaUHC.manager.deleteGame(NexiaUHC.manager.getGames().getFirst());
        return 0;
    }
}
