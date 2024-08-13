package com.nexia.uhc.utility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public abstract class ChatFormat {
    public static TextColor brandColor1 = TextColor.fromHexString("#a400fc");
    public static TextColor brandColor2 = TextColor.fromHexString("#e700f0");
    public static TextColor arrowColor = TextColor.fromHexString("#4a4a4a");
    public static TextColor failColor = TextColor.fromHexString("#ff2b1c");

    public static Component nexia = MiniMessage.get().parse(String.format("<bold><gradient:%s:%s>Nexia UHC</gradient></bold>", ChatFormat.brandColor1, ChatFormat.brandColor2));
    public static Component nexiaIp = MiniMessage.get().parse(String.format("<bold><gradient:%s:%s>uhc.nexia.dev</gradient></bold>", ChatFormat.brandColor1, ChatFormat.brandColor2));
    public static Component arrow = MiniMessage.get().parse(String.format(" <color:%s>»</color> ", ChatFormat.arrowColor));
    public static Component nexiaMessage = nexia.append(arrow);
}
