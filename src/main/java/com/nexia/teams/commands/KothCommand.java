package com.nexia.teams.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nexia.teams.utilities.chat.ChatFormat;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class KothCommand {
    static String commandSeparator = ": ";

    static String[] commands = {
            "koth create <koth>" + commandSeparator + "Create a koth",
            "koth enable <koth>" + commandSeparator + "Enable a koth",
            "koth pos" + commandSeparator + "Set the area for a koth",
            "koth time <amount of time>" + commandSeparator + "Set the time for how long a koth lasts",
            "koth schedule <time stamp>" + commandSeparator + "When will the koth happen",
            "koth start <koth>" + commandSeparator + "Start the koth",
            "koth stop <koth>" + commandSeparator + "Stop the koth"
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("koth")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                .executes(KothCommand::info)
        );
    }

    public static int info(CommandContext<CommandSourceStack> context) {
        Component message = Component.text("");

        for (String command : commands) {
            String[] commandInfo = command.split(commandSeparator);
            if (commandInfo.length < 2) continue;

            message = message.append(Component.text("\n/" + commandInfo[0])
                    .color(ChatFormat.brandColor1)
                    .append(Component.text(" | ").color(ChatFormat.Minecraft.aqua))
                    .append(Component.text(commandInfo[1]).color(ChatFormat.brandColor2)));

            //message += "\n" + ChatFormat.brandColor1 + "/" + commandInfo[0] + ChatFormat.lineColor + " | " + ChatFormat.brandColor2 + commandInfo[1];
        }

        context.getSource().sendSystemMessage(ChatFormat.convertComponent(message));

        return 0;
    }
}
