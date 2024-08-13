package com.nexia.uhc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nexia.nexus.api.command.CommandSourceInfo;
import com.nexia.nexus.api.command.CommandUtils;
import com.nexia.nexus.api.world.entity.player.Player;
import com.nexia.nexus.api.world.types.Minecraft;
import com.nexia.nexus.api.world.util.Location;
import com.nexia.uhc.NexiaUHC;
import com.nexia.uhc.utility.ChatFormat;
import net.kyori.adventure.text.Component;

public class SpectateCommand {
    public void registerCommand(CommandDispatcher<CommandSourceInfo> commandDispatcher) {
        commandDispatcher.register(CommandUtils.literal("spectate").executes(SpectateCommand::spectate));
    }

    private static int spectate(CommandContext<CommandSourceInfo> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        if (NexiaUHC.manager.getGames().isEmpty()) {
            context.getSource().sendMessage(Component.text("There isn't an ongoing UHC game!").color(ChatFormat.failColor));
            context.getSource().getPlayerOrException().playSound(Minecraft.Sound.NOTE_BLOCK_BASS, 1, 1);
            return 1;
        }

        player.teleport(new Location(0.5, 80, 0.5, NexiaUHC.manager.getGames().getFirst().getOverworld()));
        return 0;
    }
}
