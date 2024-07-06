package com.nexia.teams.mixin;

import com.nexia.teams.NexiaTeams;
import com.nexia.teams.utilities.CombatUtil;
import com.nexia.teams.utilities.chat.ChatFormat;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
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

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void disconnect(net.minecraft.network.chat.Component component, CallbackInfo ci) {
        boolean carpetPlayer = component.getString().equalsIgnoreCase("Killed") // /kill
                || component.getString().equalsIgnoreCase(player.getScoreboardName() + " died") //killed by entity
        ;
        player.getCombatTracker().recheckStatus();

        if(player.getCombatTracker().inCombat && !carpetPlayer) {
            CombatUtil.addPlayer(player);
        }

        if(carpetPlayer) {
            CombatUtil.combatLoggedPlayersTimer.remove(player.getUUID());
            if(!player.getTags().contains("leavekill")) {
                player.dropEquipment();
                player.dropExperience();

                player.addTag("scheduleKill");
            }
        }
    }

    @Inject(method = "handleTeleportToEntityPacket", at = @At("HEAD"), cancellable = true)
    public void disableSpectatorsTeleporting(ServerboundTeleportToEntityPacket serverboundTeleportToEntityPacket, CallbackInfo ci) {
        if (this.player.isSpectator() && !this.player.hasPermissions(4)) ci.cancel();
    }

    @Inject(method = "handleInteract", at = @At("HEAD"), cancellable = true)
    public void disableOrEnablePvp(ServerboundInteractPacket serverboundInteractPacket, CallbackInfo ci) {
        final Entity entity = serverboundInteractPacket.getTarget(this.player.serverLevel());
        if (!NexiaTeams.pvpEnabled && entity instanceof ServerPlayer) ci.cancel();
    }

    @ModifyArg(method = "handleClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setGameMode(Lnet/minecraft/world/level/GameType;)Z"))
    public GameType deathSystem(GameType gameType) {
        return NexiaTeams.hardcoreEnabled ? GameType.SPECTATOR : this.getPlayer().gameMode.getGameModeForPlayer();
    }
}
