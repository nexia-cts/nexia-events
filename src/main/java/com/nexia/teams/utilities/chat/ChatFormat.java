package com.nexia.teams.utilities.chat;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;

import static com.nexia.teams.utilities.chat.ChatFormat.Minecraft.*;
import static com.nexia.teams.utilities.chat.ChatFormat.Minecraft.*;

/*
 * Directly taken from Nexus and Nexia-Mod
 * https://github.com/nexia-cts/nexus/blob/main/nexus-builder/src/main/java/com/nexia/nexus/builder/implementation/util/ObjectMappings.java#L3291
 * https://github.com/nexia-cts/Nexia-Mod/blob/main/src/main/java/com/nexia/core/utilities/chat/ChatFormat.java
 */

public abstract class ChatFormat {

    public static RegistryAccess.Frozen provider;

    public static TextColor brandColor1 = TextColor.fromHexString("#a400fc");

    public static TextColor brandColor2 = TextColor.fromHexString("#e700f0");

    public static TextColor arrowColor = TextColor.fromHexString("#4a4a4a");

    public static class Minecraft {
        public static TextColor dark_red = TextColor.fromHexString("#b51413");
        public static TextColor red = TextColor.fromHexString("#ff2b1c");
        public static TextColor gold = TextColor.fromHexString("#f5bc42");
        public static TextColor yellow = TextColor.fromHexString("#fff60c");
        public static TextColor dark_green = TextColor.fromHexString("#1b9c19");
        public static TextColor green = TextColor.fromHexString("#38e312");
        public static TextColor aqua = TextColor.fromHexString("#28e2ff");
        public static TextColor dark_aqua = TextColor.fromHexString("#399ca9");
        public static TextColor dark_blue = TextColor.fromHexString("#263daa");
        public static TextColor blue = TextColor.fromHexString("#2955ff");
        public static TextColor light_purple = ChatFormat.brandColor2;
        public static TextColor dark_purple = ChatFormat.brandColor1;
        public static TextColor white = TextColor.fromHexString("#ffffff");
        public static TextColor gray = TextColor.fromHexString("#c7c7c7");
        public static TextColor dark_gray = TextColor.fromHexString("#767676");
        public static TextColor black = TextColor.fromHexString("#252525");
    }

    public static net.kyori.adventure.text.Component nexiaMessage = MiniMessage.miniMessage().deserialize(String.format("<bold><gradient:%s:%s>Nexia</gradient></bold> <color:%s>»</color> ", ChatFormat.brandColor1, ChatFormat.brandColor2, ChatFormat.arrowColor));

    public static Component convertComponent(net.kyori.adventure.text.Component component) {
        if (component == null || provider == null) return null;
        String compString = GsonComponentSerializer.gson().serialize(component);
        return Component.Serializer.fromJson(compString, provider);
    }

    public static net.kyori.adventure.text.Component convertComponent(Component mcComponent) {
        if (mcComponent == null || provider == null) return null;
        String mcCompString = Component.Serializer.toJson(mcComponent, provider);
        return GsonComponentSerializer.gson().deserialize(mcCompString);
    }

    public static String getSecondaryColor(String primary) {
        String secondary = "";
        switch (primary) {
            case "aqua" -> secondary = "dark_aqua";
            case "dark_aqua" -> secondary = "aqua";
            case "blue" -> secondary = "dark_blue";
            case "dark_blue" -> secondary = "blue";
            case "white" -> secondary = "gray";
            case "gray" -> secondary = "white";
            case "dark_gray" -> secondary = "black";
            case "black" -> secondary = "dark_gray";
            case "red" -> secondary = "dark_red";
            case "dark_red" -> secondary = "red";
            case "green" -> secondary = "dark_green";
            case "dark_green" -> secondary = "green";
            case "light_purple" -> secondary = "dark_purple";
            case "dark_purple" -> secondary = "light_purple";
            case "yellow" -> secondary = "gold";
            case "gold" -> secondary = "yellow";
        }
        
        return secondary;
    }
}
