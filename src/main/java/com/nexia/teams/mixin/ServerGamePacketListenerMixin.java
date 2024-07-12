package com.nexia.teams.mixin;

import com.mojang.authlib.GameProfile;
import com.nexia.teams.NexiaTeams;
import com.nexia.teams.events.tournament.TournamentFight;
import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.time.ServerTime;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.GuiHelpers;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerMixin {
    @Shadow public ServerPlayer player;

    @Shadow public abstract ServerPlayer getPlayer();

    @Shadow protected abstract GameProfile playerProfile();

    @Inject(method = "handleUseItem", at = @At("TAIL"))
    public void pearlCooldownMessage(ServerboundUseItemPacket serverboundUseItemPacket, CallbackInfo ci) {
        InteractionHand interactionHand = serverboundUseItemPacket.getHand();
        ItemStack itemStack = this.player.getItemInHand(interactionHand);

        if (this.player.getCooldowns().isOnCooldown(itemStack.getItem()) && itemStack.getItem() == Items.ENDER_PEARL) {
            double secondsLeft = (double) Math.round((15 * this.player.getCooldowns().getCooldownPercent(itemStack.getItem(), 0.0F) * 100)) / 100;
            if(secondsLeft >= 14.9) return;
            String secondsLeftText = String.format("You're on pearl cooldown. You have %s seconds left.", secondsLeft);

            this.player.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text(secondsLeftText))));
        }
    }

    @Inject(method = "handleTeleportToEntityPacket", at = @At("HEAD"), cancellable = true)
    public void disableSpectatorsTeleporting(ServerboundTeleportToEntityPacket serverboundTeleportToEntityPacket, CallbackInfo ci) {
        if (this.player.isSpectator() && !this.player.hasPermissions(4)) ci.cancel();
    }

    @Inject(method = "handleInteract", at = @At("HEAD"), cancellable = true)
    public void disableOrEnablePvp(ServerboundInteractPacket serverboundInteractPacket, CallbackInfo ci) {
        final Entity entity = serverboundInteractPacket.getTarget(this.player.serverLevel());
        if (entity instanceof LargeFireball && entity.getTags().contains("meteor")) ci.cancel();
        if (!NexiaTeams.pvpEnabled && entity instanceof ServerPlayer) ci.cancel();
    }

    @ModifyArg(method = "handleClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setGameMode(Lnet/minecraft/world/level/GameType;)Z"))
    public GameType deathSystem(GameType gameType) {
        return NexiaTeams.hardcoreEnabled ? GameType.SPECTATOR : this.getPlayer().gameMode.getGameModeForPlayer();
    }

    @Inject(method = "handleContainerClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;suppressRemoteUpdates()V"), cancellable = true)
    public void chestRefill(ServerboundContainerClickPacket serverboundContainerClickPacket, CallbackInfo ci) {
        if (player.containerMenu instanceof ChestMenu chestMenu && TournamentFight.spawnArea.contains(player.position())) {
            int slot = serverboundContainerClickPacket.getSlotNum();
            int type = serverboundContainerClickPacket.getClickType().ordinal();

            if (slot >= 0 && slot < chestMenu.getContainer().getContainerSize()) {
                player.connection.send(new ClientboundContainerSetSlotPacket(chestMenu.containerId, chestMenu.incrementStateId(), slot, chestMenu.getSlot(slot).getItem()));

                if (type == ClickType.MOUSE_LEFT.ordinal()) {
                    ItemStack itemStack = chestMenu.getSlot(slot).getItem().copy();
                    player.addItem(itemStack);
                }
            }

            GuiHelpers.sendSlotUpdate(this.player, -1, -1, chestMenu.getCarried(), chestMenu.getStateId());
            GuiHelpers.sendPlayerScreenHandler(this.player);


            ci.cancel();
        }
    }
}
