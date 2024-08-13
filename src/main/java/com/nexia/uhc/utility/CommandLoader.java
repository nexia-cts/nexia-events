package com.nexia.uhc.utility;

import com.mojang.brigadier.CommandDispatcher;
import com.nexia.nexus.api.command.CommandSourceInfo;
import com.nexia.uhc.NexiaUHC;
import com.nexia.uhc.commands.GameCommand;
import com.nexia.uhc.commands.LobbyCommand;
import com.nexia.uhc.commands.SpectateCommand;

public class CommandLoader {
    public static void registerCommands() {
        CommandDispatcher<CommandSourceInfo> commandDispatcher = NexiaUHC.nexusServer.getCommandDispatcher();

        new GameCommand().registerCommand(commandDispatcher);
        new SpectateCommand().registerCommand(commandDispatcher);
        new LobbyCommand().registerCommand(commandDispatcher);
    }
}
