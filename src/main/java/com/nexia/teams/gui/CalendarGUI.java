package com.nexia.teams.gui;

import com.nexia.teams.utilities.chat.ChatFormat;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;

public class CalendarGUI extends SimpleGui {
    static final Component title = Component.text("Calendar").decoration(TextDecoration.BOLD, true);

    public CalendarGUI(MenuType<?> type, ServerPlayer player, boolean manipulatePlayerSlots) {
        super(type, player, manipulatePlayerSlots);
    }

    private void setMainLayout(ServerPlayer player) {
        ItemStack day1 = Items.DIAMOND_PICKAXE.getDefaultInstance();
        day1.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(
                Component.text("Day 1: Start Of The World")
                .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)
                .color(NamedTextColor.AQUA))
        );
        day1.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        List<net.minecraft.network.chat.Component> list1 = new java.util.ArrayList<>();
        list1.add(ChatFormat.convertComponent(Component.text("15 minute grace period.").color(NamedTextColor.AQUA)));
        list1.add(ChatFormat.convertComponent(Component.text("Border will start at 1.5k and expand to 5k.").color(NamedTextColor.AQUA)));
        day1.set(DataComponents.LORE, new ItemLore(list1));
        this.setSlot(10, day1);

        ItemStack day2 = Items.NETHERITE_PICKAXE.getDefaultInstance();
        day2.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(
                Component.text("Day 2: Nether Opening")
                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.DARK_RED))
        );
        day2.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        List<net.minecraft.network.chat.Component> list2 = new java.util.ArrayList<>();
        list2.add(ChatFormat.convertComponent(Component.text("Nether opens. 15 minutes later").color(NamedTextColor.DARK_RED)));
        list2.add(ChatFormat.convertComponent(Component.text("Nether KOTH will take place.").color(NamedTextColor.DARK_RED)));
        list2.add(ChatFormat.convertComponent(Component.text("KOTH reward will be netherite,").color(NamedTextColor.DARK_RED)));
        list2.add(ChatFormat.convertComponent(Component.text("netherite will not be obtainable otherwise.").color(NamedTextColor.DARK_RED)));
        day2.set(DataComponents.LORE, new ItemLore(list2));
        this.setSlot(12, day2);

        ItemStack day3 = Items.SMOOTH_BASALT.getDefaultInstance();
        day3.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(
                Component.text("Day 3: Meteorite Shower")
                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.DARK_PURPLE))
        );
        day3.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        List<net.minecraft.network.chat.Component> list3 = new java.util.ArrayList<>();
        list3.add(ChatFormat.convertComponent(Component.text("Meteors will land around the map").color(NamedTextColor.DARK_PURPLE)));
        list3.add(ChatFormat.convertComponent(Component.text("containing useful items, coords").color(NamedTextColor.DARK_PURPLE)));
        list3.add(ChatFormat.convertComponent(Component.text("will be displayed before landing.").color(NamedTextColor.DARK_PURPLE)));
        day3.set(DataComponents.LORE, new ItemLore(list3));
        this.setSlot(14, day3);

        ItemStack day4 = Items.GOLDEN_SWORD.getDefaultInstance();
        day4.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(
                Component.text("Day 4: Tournament")
                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.BLUE))
        );
        day4.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        List<net.minecraft.network.chat.Component> list4 = new java.util.ArrayList<>();
        list4.add(ChatFormat.convertComponent(Component.text("At spawn there will be a tournament.").color(NamedTextColor.BLUE)));
        list4.add(ChatFormat.convertComponent(Component.text("Teams must be equal in number.").color(NamedTextColor.BLUE)));
        list4.add(ChatFormat.convertComponent(Component.text("Each member of the team which wins will each receive a totem.").color(NamedTextColor.BLUE)));
        day4.set(DataComponents.LORE, new ItemLore(list4));
        this.setSlot(16, day4);

        ItemStack day5 = Items.DRAGON_EGG.getDefaultInstance();
        day5.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(
                Component.text("Day 5: End Fight")
                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.GRAY))
        );
        day5.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        List<net.minecraft.network.chat.Component> list5 = new java.util.ArrayList<>();
        list5.add(ChatFormat.convertComponent(Component.text("The End will open.").color(NamedTextColor.GRAY)));
        list5.add(ChatFormat.convertComponent(Component.text("Once the dragon dies").color(NamedTextColor.GRAY)));
        list5.add(ChatFormat.convertComponent(Component.text("players no longer respawn.").color(NamedTextColor.GRAY)));
        day5.set(DataComponents.LORE, new ItemLore(list5));
        this.setSlot(30, day5);

        ItemStack day6 = Items.BARRIER.getDefaultInstance();
        day6.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(
                Component.text("Day 6: End Of The World")
                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.WHITE))
        );
        day6.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        List<net.minecraft.network.chat.Component> list6 = new java.util.ArrayList<>();
        list6.add(ChatFormat.convertComponent(Component.text("The world border will shrink to 500.").color(NamedTextColor.WHITE)));
        list6.add(ChatFormat.convertComponent(Component.text("15 minutes later all players will be teleported into").color(NamedTextColor.WHITE)));
        list6.add(ChatFormat.convertComponent(Component.text("the tournament arena, last team standing wins.").color(NamedTextColor.WHITE)));
        day6.set(DataComponents.LORE, new ItemLore(list6));
        this.setSlot(32, day6);
    }

    public static void openCalendarGUI(ServerPlayer player) {
        CalendarGUI calendarGUI = new CalendarGUI(MenuType.GENERIC_9x5, player, false);
        calendarGUI.setTitle(ChatFormat.convertComponent(title));
        calendarGUI.setMainLayout(player);
        calendarGUI.open();
    }
}
