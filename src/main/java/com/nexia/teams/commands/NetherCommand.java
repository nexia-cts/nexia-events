package com.nexia.teams.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.nexia.teams.NexiaTeams;
import com.nexia.teams.utilities.chat.ChatFormat;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class NetherCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("nether").requires(commandSourceStack -> commandSourceStack.hasPermission(4)).executes(NetherCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        NexiaTeams.netherDisabled = !NexiaTeams.netherDisabled;
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text((NexiaTeams.netherDisabled ? "Disabled" : "Enabled") + " nether."))));
        return 0;
    }
}
