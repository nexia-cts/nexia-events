package com.nexia.teams.gui;

import com.nexia.teams.utilities.chat.ChatFormat;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;


public class KitGUI extends SimpleGui {
    Component title = Component.text("Kit GUI").decorate(TextDecoration.BOLD);
    public KitGUI(MenuType<?> type, ServerPlayer player, boolean manipulatePlayerSlots) {
        super(type, player, manipulatePlayerSlots);
    }

    public void setMainLayout() {
        BlockEntity blockEntity = this.player.serverLevel().getBlockEntity(BlockPos.containing(0, 62, 0));
        if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
            for (int i = 0; i < chestBlockEntity.getContainerSize(); i++) {
                this.setSlot(i, chestBlockEntity.getItem(i));
            }
        }
    }

    public static void openKitGUI(ServerPlayer player) {
        KitGUI kitGUI = new KitGUI(MenuType.GENERIC_9x6, player, false);
        kitGUI.setTitle(ChatFormat.convertComponent(kitGUI.title));
        kitGUI.setMainLayout();
        kitGUI.open();
    }

    @Override
    public boolean onClick(int index, ClickType type, net.minecraft.world.inventory.ClickType action, GuiElementInterface element) {
        if (element != null) this.player.addItem(element.getItemStack().copy());
        return false;
    }
}
