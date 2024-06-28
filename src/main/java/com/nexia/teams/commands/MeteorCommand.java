package com.nexia.teams.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.nexia.teams.events.meteor.Meteor;
import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.time.ServerTime;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class MeteorCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("meteor")
                .then(Commands.argument("scheduledTimestamp", IntegerArgumentType.integer())
                .executes(context -> execute(context, IntegerArgumentType.getInteger(context, "scheduledTimestamp"))))
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context, long scheduledTimestamp) {
        long unixTime = System.currentTimeMillis() / 1000L;
        if (scheduledTimestamp < unixTime) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Cannot schedule a meteor in the past!"))));
            return 0;
        }

        Meteor.scheduledTimestamp = scheduledTimestamp;
        Meteor.serverPlayer = context.getSource().getPlayer();
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You have scheduled a meteor!"))));

        int secondsDifference = (int) (scheduledTimestamp - unixTime);
        int seconds = secondsDifference % 60;
        int hours = secondsDifference / 60;
        int minutes = hours % 60;
        hours = hours / 60;

        Meteor.generateRandomCoordinates();
        for (ServerPlayer serverPlayer : ServerTime.minecraftServer.getPlayerList().getPlayers()) {
            serverPlayer.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text(String.format("Meteor will strike at (%s, %s) in %s hours, %s minutes and %s seconds.", Meteor.meteorCoordinates[0], Meteor.meteorCoordinates[2], hours, minutes, seconds)))));
        }

        return 0;
    }
}
