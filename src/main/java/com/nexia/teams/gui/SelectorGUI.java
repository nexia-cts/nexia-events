package com.nexia.teams.gui;

import com.nexia.teams.events.tournament.TournamentFight;
import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.time.ServerTime;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SelectorGUI extends SimpleGui {
    static final Component title = Component.text("Select players").decoration(TextDecoration.BOLD, true);
    Collection<String> serverPlayers;
    List<ServerPlayer> selectedPlayers = new ArrayList<>();

    public SelectorGUI(MenuType<?> type, ServerPlayer player, boolean manipulatePlayerSlots, Collection<String> serverPlayers) {
        super(type, player, manipulatePlayerSlots);
        this.serverPlayers = serverPlayers;
    }

    private void setMainLayout() {
        int i = 0;
        for (String player : serverPlayers) {
            ServerPlayer serverPlayer = ServerTime.minecraftServer.getPlayerList().getPlayerByName(player);
            if (serverPlayer == null) continue;
            ItemStack playerHead = Items.PLAYER_HEAD.getDefaultInstance();
            playerHead.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(Component.text(serverPlayer.getScoreboardName()).color(NamedTextColor.namedColor(serverPlayer.getTeamColor())).decoration(TextDecoration.ITALIC, false)));
            playerHead.set(DataComponents.PROFILE, new ResolvableProfile(serverPlayer.getGameProfile()));
            this.setSlot(i, playerHead);
            i++;
        }

        ItemStack confirmButton = Items.GREEN_STAINED_GLASS_PANE.getDefaultInstance();
        confirmButton.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(Component.text("Confirm").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false)));
        this.setSlot(8, confirmButton);
    }

    public boolean click(int index, ClickType clickType, net.minecraft.world.inventory.ClickType action){
        GuiElementInterface element = this.getSlot(index);
        if(element != null && clickType != ClickType.MOUSE_DOUBLE_CLICK) {
            this.player.level().playSound(
                    null,
                    BlockPos.containing(this.player.getPosition(0)),
                    SoundEvents.COMPARATOR_CLICK,
                    SoundSource.MASTER,
                    1f,
                    1f
            );

            ItemStack itemStack = element.getItemStack();

            if (itemStack.getItem() == Items.PLAYER_HEAD) {
                Component itemComponent = ChatFormat.convertComponent(itemStack.get(DataComponents.CUSTOM_NAME));

                assert itemComponent != null;
                if (!itemComponent.hasDecoration(TextDecoration.BOLD)) {
                    ResolvableProfile resolvableProfile = itemStack.get(DataComponents.PROFILE);
                    assert resolvableProfile != null;
                    selectedPlayers.add(ServerTime.minecraftServer.getPlayerList().getPlayer(resolvableProfile.gameProfile().getId()));
                    itemStack.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(itemComponent.decoration(TextDecoration.BOLD, true)));
                    this.setSlot(index, itemStack);
                } else {
                    ResolvableProfile resolvableProfile = itemStack.get(DataComponents.PROFILE);
                    assert resolvableProfile != null;
                    selectedPlayers.remove(ServerTime.minecraftServer.getPlayerList().getPlayer(resolvableProfile.gameProfile().getId()));
                    itemStack.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(itemComponent.decoration(TextDecoration.BOLD, false)));
                    this.setSlot(index, itemStack);
                }
            }

            if (itemStack.getItem() == Items.GREEN_STAINED_GLASS_PANE) {
                TournamentFight.serverPlayers = selectedPlayers;
                TournamentFight.isStarting = true;
                this.close();
            }


        }
        return super.click(index, clickType, action);
    }

    public static void openSelectorGUI(ServerPlayer player, Collection<String> players) {
        SelectorGUI selectorGUI = new SelectorGUI(MenuType.GENERIC_9x1, player, false, players);
        selectorGUI.setTitle(ChatFormat.convertComponent(title));
        selectorGUI.setMainLayout();
        selectorGUI.open();
    }
}
