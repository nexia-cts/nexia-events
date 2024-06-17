package com.nexia.teams.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class KothCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("koth")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                .executes(context -> KothCommand.info(context.getSource()))
        );
    }

    public static int info(CommandSourceStack context) {
        return 1;
    }
}
