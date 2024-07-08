package com.nexia.teams.gui;

import com.nexia.teams.utilities.chat.ChatFormat;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.kyori.adventure.text.Component;
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

    private void fillEmptySlots(ItemStack itemStack){
        for(int i = 0; i < 45; i++){
            this.setSlot(i, itemStack);
        }
    }

    private void setMainLayout(ServerPlayer player) {
        ItemStack emptySlot = new ItemStack(Items.BLACK_STAINED_GLASS_PANE, 1);
        emptySlot.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(Component.text("")));

        fillEmptySlots(emptySlot);
        int airSlots = 10;
        for(int air = 0; air < 21; air++){
            if(airSlots == 17) {
                airSlots = 19;
            }
            if(airSlots == 26) {
                airSlots = 28;
            }
            this.setSlot(airSlots, new ItemStack(Items.AIR));
            airSlots++;
        }

        ItemStack day1 = Items.GRASS_BLOCK.getDefaultInstance();
        day1.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(
                Component.text("Day 1: Start Of The World")
                .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)
                .color(ChatFormat.Minecraft.green))
        );
        day1.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
        List<net.minecraft.network.chat.Component> list1 = new java.util.ArrayList<>();
        list1.add(ChatFormat.convertComponent(Component.text("15 minute grace period.").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        list1.add(ChatFormat.convertComponent(Component.text("Border starts at 1k and expands to 4k.").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        day1.set(DataComponents.LORE, new ItemLore(list1));
        this.setSlot(10, day1);

        ItemStack day2 = Items.NETHERRACK.getDefaultInstance();
        day2.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(
                Component.text("Day 2: Nether Opening")
                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)
                        .color(ChatFormat.Minecraft.red))
        );
        day2.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
        List<net.minecraft.network.chat.Component> list2 = new java.util.ArrayList<>();
            list2.add(ChatFormat.convertComponent(Component.text("The Nether opens, after 30 minutes").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        list2.add(ChatFormat.convertComponent(Component.text("the Nether KOTH begins").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        list2.add(ChatFormat.convertComponent(Component.text("at 0, 0 on the Nether roof.").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        list2.add(ChatFormat.convertComponent(Component.text("Winner gets 3 Netherite.").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        day2.set(DataComponents.LORE, new ItemLore(list2));
        this.setSlot(13, day2);

        ItemStack day3 = Items.FIRE_CHARGE.getDefaultInstance();
        day3.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(
                Component.text("Day 3: Meteorite Shower")
                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)
                        .color(ChatFormat.Minecraft.dark_gray))
        );
        day3.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
        List<net.minecraft.network.chat.Component> list3 = new java.util.ArrayList<>();
        list3.add(ChatFormat.convertComponent(Component.text("Meteors will land around the map").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        list3.add(ChatFormat.convertComponent(Component.text("containing useful items, coords").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        list3.add(ChatFormat.convertComponent(Component.text("will be displayed before landing.").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        day3.set(DataComponents.LORE, new ItemLore(list3));
        this.setSlot(16, day3);

        ItemStack day4 = Items.END_CRYSTAL.getDefaultInstance();
        day4.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(
                Component.text("Day 4: End Fight")
                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)
                        .color(ChatFormat.Minecraft.dark_purple))
        );
        day4.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        List<net.minecraft.network.chat.Component> list4 = new java.util.ArrayList<>();
        list4.add(ChatFormat.convertComponent(Component.text("The End will open.").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        list4.add(ChatFormat.convertComponent(Component.text("The Dragon will drop an elytra.").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        day4.set(DataComponents.LORE, new ItemLore(list4));
        this.setSlot(28, day4);

        ItemStack day5 = Items.GOLDEN_SWORD.getDefaultInstance();
        day5.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(
                Component.text("Day 5: Tournament")
                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)
                        .color(ChatFormat.Minecraft.yellow))
        );
        day5.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
        List<net.minecraft.network.chat.Component> list5 = new java.util.ArrayList<>();
        list5.add(ChatFormat.convertComponent(Component.text("There will be a tournament at spawn.").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        list5.add(ChatFormat.convertComponent(Component.text("All matches will be equal in players.").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        list5.add(ChatFormat.convertComponent(Component.text("Reward of 3 totems.").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        day5.set(DataComponents.LORE, new ItemLore(list5));
        this.setSlot(31, day5);

        ItemStack day6 = Items.BARRIER.getDefaultInstance();
        day6.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(
                Component.text("Day 6: End Of The World")
                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)
                        .color(ChatFormat.Minecraft.white))
        );
        day6.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
        List<net.minecraft.network.chat.Component> list6 = new java.util.ArrayList<>();
        list6.add(ChatFormat.convertComponent(Component.text("Players no longer respawn and the border shrinks.").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        list6.add(ChatFormat.convertComponent(Component.text("After 15 minutes all players are teleported to spawn.").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        list6.add(ChatFormat.convertComponent(Component.text("The last team standing wins.").color(ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
        day6.set(DataComponents.LORE, new ItemLore(list6));
        this.setSlot(34, day6);
    }

    public static void openCalendarGUI(ServerPlayer player) {
        CalendarGUI calendarGUI = new CalendarGUI(MenuType.GENERIC_9x5, player, false);
        calendarGUI.setTitle(ChatFormat.convertComponent(title));
        calendarGUI.setMainLayout(player);
        calendarGUI.open();
    }
}
