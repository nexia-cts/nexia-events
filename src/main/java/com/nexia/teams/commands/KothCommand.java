package com.nexia.teams.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.nexia.teams.events.koth.KothGame;
import com.nexia.teams.events.koth.KothGameHandler;
import com.nexia.teams.utilities.chat.ChatFormat;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class KothCommand {
    static String commandSeparator = ": ";

    static String[] commands = {
            "koth" + commandSeparator + "Display this message",
            "koth list" + commandSeparator + "Show all koths.",
            "koth create <koth>" + commandSeparator + "Create a KOTH",
            "koth pos <koth name>" + commandSeparator + "Set the positions of the KOTH",
            "koth time <koth name> <amount of time>" + commandSeparator + "Set the time for how much a player needs to win (in seconds)",
            "koth schedule <koth name> <timestamp>" + commandSeparator + "When the KOTH will automatically start.",
            "koth start <koth name>" + commandSeparator + "Start the KOTH.",
            "koth stop <koth name>" + commandSeparator + "Stop the KOTH."
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("koth")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                .executes(KothCommand::info)
                .then(Commands.literal("list")
                        .executes(KothCommand::listKothGames))
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.word()).executes(context -> createKoth(context, StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("pos")
                        .then(Commands.argument("name", StringArgumentType.word()).executes(context -> setKothPos(context, StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("start")
                        .then(Commands.argument("name", StringArgumentType.word()).executes(context -> startKoth(context, StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("stop")
                        .then(Commands.argument("name", StringArgumentType.word()).executes(context -> stopKoth(context, StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("delete")
                        .then(Commands.argument("name", StringArgumentType.word()).executes(context -> deleteKoth(context, StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("time")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("time", IntegerArgumentType.integer())
                                        .executes(context -> setKothTime(context, StringArgumentType.getString(context, "name"), IntegerArgumentType.getInteger(context, "time"))))))
                .then(Commands.literal("schedule")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("epoch", IntegerArgumentType.integer())
                                        .executes(context -> scheduleKoth(context, StringArgumentType.getString(context, "name"), IntegerArgumentType.getInteger(context, "epoch"))))))
        );
    }

    private static int listKothGames(CommandContext<CommandSourceStack> context) {
        if (KothGameHandler.kothGames.isEmpty()) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("There are no KOTH games!"))));
            return 0;
        }

        Component message = Component.text("List of KOTHs:").color(ChatFormat.Minecraft.white);

        for (KothGame kothGame : KothGameHandler.kothGames) {
            if (kothGame.area != null) {
                message = message.append(Component.text("\n" + kothGame.name).color(ChatFormat.Minecraft.white)
                        .append(Component.text(" | ").color(ChatFormat.Minecraft.dark_gray))
                        .append(Component.text(kothGame.creator)).color(ChatFormat.Minecraft.white)
                        .append(Component.text(" | ").color(ChatFormat.Minecraft.dark_gray))
                        .append(Component.text(String.format("(%s, %s, %s)", kothGame.area.getCenter().x, kothGame.area.getCenter().y, kothGame.area.getCenter().z))).color(ChatFormat.Minecraft.white)
                );
            } else {
                message = message.append(Component.text("\n" + kothGame.name).color(ChatFormat.Minecraft.white)
                        .append(Component.text(" | ").color(ChatFormat.Minecraft.dark_gray))
                        .append(Component.text(kothGame.creator)).color(ChatFormat.Minecraft.white)
                );
            }

        }

        context.getSource().sendSystemMessage(ChatFormat.convertComponent(message));
        return 0;
    }

    private static int info(CommandContext<CommandSourceStack> context) {
        Component message = Component.text("");

        for (String command : commands) {
            String[] commandInfo = command.split(commandSeparator);
            if (commandInfo.length < 2) continue;

            message = message.append(Component.text("\n/" + commandInfo[0])
                    .color(ChatFormat.brandColor1)
                    .append(Component.text(" | ").color(ChatFormat.Minecraft.aqua))
                    .append(Component.text(commandInfo[1]).color(ChatFormat.brandColor2)));
        }

        context.getSource().sendSystemMessage(ChatFormat.convertComponent(message));

        return 0;
    }

    private static int createKoth(CommandContext<CommandSourceStack> context, String name) {
        if (KothGameHandler.getKothGameByName(name) != null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH name is taken!"))));
            return 1;
        }

        KothGameHandler.kothGames.add(new KothGame(context.getSource().getPlayer().getScoreboardName(), name, null, null, null, context.getSource().getLevel()));
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("KOTH " + name + " has been created!"))));
        return 0;
    }

    private static int setKothPos(CommandContext<CommandSourceStack> context, String name) {
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

    private static int setKothTime(CommandContext<CommandSourceStack> context, String name, int amountOfTime) {
        KothGame kothGame = KothGameHandler.getKothGameByName(name);

        if (kothGame == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH doesn't exist!"))));
            return 1;
        }

        if (kothGame.isRunning) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH is running!"))));
            return 1;
        }

        if (amountOfTime <= 0) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Invalid amount of time!"))));
            return 1;
        }

        kothGame.time = amountOfTime;
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Changed the amount of time a player needs to win."))));
        return 0;
    }

    private static int scheduleKoth(CommandContext<CommandSourceStack> context, String name, long scheduledTimestamp) {
        KothGame kothGame = KothGameHandler.getKothGameByName(name);

        if (kothGame == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH doesn't exist!"))));
            return 1;
        }

        if (kothGame.isRunning) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH is running!"))));
            return 1;
        }

        long unixTime = System.currentTimeMillis() / 1000L;
        if (scheduledTimestamp < unixTime) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Cannot schedule a KOTH in the past!"))));
            return 1;
        }

        kothGame.scheduledTimestamp = scheduledTimestamp;
        context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Scheduled KOTH."))));
        return 0;
    }

    private static int startKoth(CommandContext<CommandSourceStack> context, String name) {
        KothGame kothGame = KothGameHandler.getKothGameByName(name);

        if (kothGame == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH doesn't exist!"))));
            return 1;
        }

        if (kothGame.area == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("KOTH position hasn't been set!"))));
            return 1;
        }

        if (kothGame.isRunning) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH is running!"))));
            return 1;
        }

        kothGame.start();
        return 0;
    }

    private static int stopKoth(CommandContext<CommandSourceStack> context, String name) {
        KothGame kothGame = KothGameHandler.getKothGameByName(name);

        if (kothGame == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH doesn't exist!"))));
            return 1;
        }

        if (!kothGame.isRunning) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH isn't running!"))));
            return 1;
        }

        kothGame.end(null);
        return 0;
    }


    private static int deleteKoth(CommandContext<CommandSourceStack> context, String name) {
        KothGame kothGame = KothGameHandler.getKothGameByName(name);

        if (kothGame == null) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH doesn't exist!"))));
            return 1;
        }

        if (kothGame.isRunning) {
            context.getSource().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("That KOTH is running!"))));
            return 1;
        }

        KothGameHandler.kothGames.remove(kothGame);
        return 0;
    }
}
