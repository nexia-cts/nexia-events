package com.nexia.teams.utilities.chat;

import com.google.common.base.Function;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nexia.teams.utilities.time.ServerTime;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

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

    public static ChatFormatting getSecondaryColor(ChatFormatting primary) {
        return switch (primary) {
            case ChatFormatting.AQUA ->  ChatFormatting.DARK_AQUA;
            case ChatFormatting.DARK_AQUA ->  ChatFormatting.AQUA;
            case ChatFormatting.BLUE ->  ChatFormatting.DARK_BLUE;
            case ChatFormatting.DARK_BLUE ->  ChatFormatting.BLUE;
            case ChatFormatting.WHITE ->  ChatFormatting.GRAY;
            case ChatFormatting.GRAY ->  ChatFormatting.WHITE;
            case ChatFormatting.DARK_GRAY ->  ChatFormatting.BLACK;
            case ChatFormatting.BLACK ->  ChatFormatting.DARK_GRAY;
            case ChatFormatting.RED ->  ChatFormatting.DARK_RED;
            case ChatFormatting.DARK_RED ->  ChatFormatting.RED;
            case ChatFormatting.GREEN ->  ChatFormatting.DARK_GREEN;
            case ChatFormatting.DARK_GREEN ->  ChatFormatting.GREEN;
            case ChatFormatting.LIGHT_PURPLE ->  ChatFormatting.DARK_PURPLE;
            case ChatFormatting.DARK_PURPLE ->  ChatFormatting.LIGHT_PURPLE;
            case ChatFormatting.YELLOW ->  ChatFormatting.GOLD;
            case ChatFormatting.GOLD ->  ChatFormatting.YELLOW;
            default -> primary;
        };
    }

    public static void nexiaTitle(CommandSourceStack commandSourceStack, String message) throws CommandSyntaxException {
        Component messageComponent = convertComponent(MiniMessage.miniMessage().deserialize(String.format("<bold><gradient:%s:%s>%s</gradient></bold>", ChatFormat.brandColor1, ChatFormat.brandColor2, message)));

        for (ServerPlayer serverPlayer : ServerTime.minecraftServer.getPlayerList().getPlayers()) {
            Function<Component, ClientboundSetTitleTextPacket> title = ClientboundSetTitleTextPacket::new;
            serverPlayer.connection.send(title.apply(ComponentUtils.updateForEntity(commandSourceStack, messageComponent, serverPlayer, 0)));
            serverPlayer.level().playSound(
                    null,
                    BlockPos.containing(serverPlayer.getPosition(0)),
                    SoundEvents.ENDER_DRAGON_GROWL,
                    SoundSource.MASTER,
                    1f,
                    1f
            );
        }
    }
}
