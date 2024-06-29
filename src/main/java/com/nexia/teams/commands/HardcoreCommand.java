package com.nexia.teams.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nexia.teams.NexiaTeams;
import com.nexia.teams.utilities.chat.ChatFormat;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class HardcoreCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("hardcore").requires(commandSourceStack -> commandSourceStack.hasPermission(4)).executes(HardcoreCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        NexiaTeams.hardcoreEnabled = !NexiaTeams.hardcoreEnabled;
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text((NexiaTeams.hardcoreEnabled ? "Enabled" : "Disabled") + " hardcore death system."))));
        ChatFormat.nexiaTitle(context.getSource(), "Hardcore has been " + (NexiaTeams.hardcoreEnabled ? "enabled" : "disabled"));
        return 0;
    }
}
