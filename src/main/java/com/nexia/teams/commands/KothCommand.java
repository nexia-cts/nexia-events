package com.nexia.teams.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.nexia.teams.koth.KothGame;
import com.nexia.teams.koth.KothGameHandler;
import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.time.ServerTime;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class KothCommand {
    static String commandSeparator = ": ";

    static String[] commands = {
            "koth" + commandSeparator + "Display this message",
            "koth create <koth>" + commandSeparator + "Create a KOTH",
            "koth pos <koth name>" + commandSeparator + "Set the positions of the KOTH",
            "koth time <koth name> <amount of time>" + commandSeparator + "Set the time for how long a KOTH lasts (in seconds)",
            "koth schedule <koth name> <timestamp>" + commandSeparator + "When the KOTH will automatically start.",
            "koth start <koth name>" + commandSeparator + "Start the KOTH.",
            "koth stop <koth name>" + commandSeparator + "Stop the KOTH."
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("koth")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                .executes(KothCommand::info)
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.greedyString()).executes(context -> createKoth(context, StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("pos")
                        .then(Commands.argument("name", StringArgumentType.greedyString()).executes(context -> setKothPos(context, StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("start")
                        .then(Commands.argument("name", StringArgumentType.greedyString()).executes(context -> startKoth(context, StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("stop")
                        .then(Commands.argument("name", StringArgumentType.greedyString()).executes(context -> stopKoth(context, StringArgumentType.getString(context, "name")))))
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

    public static int createKoth(CommandContext<CommandSourceStack> context, String name) {
        if (KothGameHandler.getKothGameByName(name) != null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH name is taken!"))));
            return 1;
        }

        KothGameHandler.kothGames.add(new KothGame(context.getSource().getPlayer(), name, null, null, null, context.getSource().getLevel()));
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("KOTH " + name + " has been created!"))));
        return 0;
    }

    public static int setKothPos(CommandContext<CommandSourceStack> context, String name) {
        KothGame kothGame = KothGameHandler.getKothGameByName(name);

        if (kothGame == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH doesn't exist!"))));
            return 1;
        }

        ItemStack kothEditorItem = new ItemStack(Items.DIAMOND_AXE);
        kothEditorItem.applyComponents(DataComponentMap.builder().set(
                DataComponents.CUSTOM_NAME,
                net.minecraft.network.chat.Component.literal("Koth editor")
        ).build());
        context.getSource().getPlayer().addItem(kothEditorItem);
        context.getSource().getPlayer().addTag("koth_" + name);
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("You are now editing the KOTH area."))));


        return 0;
    }

    public static int setKothTime(CommandContext<CommandSourceStack> context, String name, String amountOfTime) {
        return 0;
    }

    public static int scheduleKoth(CommandContext<CommandSourceStack> context, String name, String timestamp) {
        return 0;
    }

    public static int startKoth(CommandContext<CommandSourceStack> context, String name) {
        KothGame kothGame = KothGameHandler.getKothGameByName(name);

        if (kothGame == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH doesn't exist!"))));
            return 1;
        }

        kothGame.start();
        return 0;
    }

    public static int stopKoth(CommandContext<CommandSourceStack> context, String name) {
        KothGame kothGame = KothGameHandler.getKothGameByName(name);

        if (kothGame == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH doesn't exist!"))));
            return 1;
        }

        kothGame.end();
        return 0;
    }
}
