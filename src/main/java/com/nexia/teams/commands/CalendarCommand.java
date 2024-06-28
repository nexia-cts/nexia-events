package com.nexia.teams.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.nexia.teams.gui.CalendarGUI;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CalendarCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("calendar").executes(CalendarCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CalendarGUI.openCalendarGUI(context.getSource().getPlayer());
        return 0;
    }
}
