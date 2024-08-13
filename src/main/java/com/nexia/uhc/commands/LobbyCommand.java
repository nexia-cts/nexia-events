package com.nexia.uhc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nexia.nexus.api.command.CommandSourceInfo;
import com.nexia.nexus.api.command.CommandUtils;
import com.nexia.nexus.api.world.entity.player.Player;
import com.nexia.nexus.api.world.types.Minecraft;
import com.nexia.uhc.NexiaUHC;
import com.nexia.uhc.game.Game;

public class LobbyCommand {
    public void registerCommand(CommandDispatcher<CommandSourceInfo> commandDispatcher) {
        commandDispatcher.register(CommandUtils.literal("lobby").executes(LobbyCommand::execute));
        commandDispatcher.register(CommandUtils.literal("hub").executes(LobbyCommand::execute));
        commandDispatcher.register(CommandUtils.literal("spawn").executes(LobbyCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceInfo> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        Game game = NexiaUHC.manager.getGame(player);

        if (game != null) {
            player.kill();
            return 1;
        }

        NexiaUHC.lobby.returnToLobby(player, Minecraft.GameMode.SURVIVAL, true);
        return 0;
    }
}
