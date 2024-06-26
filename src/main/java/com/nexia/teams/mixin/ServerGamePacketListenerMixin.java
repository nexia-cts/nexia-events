package com.nexia.teams.mixin;

import com.nexia.teams.utilities.chat.ChatFormat;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerMixin {
    @Shadow public ServerPlayer player;

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
}
