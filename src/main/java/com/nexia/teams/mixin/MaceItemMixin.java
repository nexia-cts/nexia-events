package com.nexia.teams.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.enchantment.DensityEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.item.MaceItem.canSmashAttack;

@Mixin(MaceItem.class)
public class MaceItemMixin extends Item {
    public MaceItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getAttackDamageBonus", at = @At("HEAD"), cancellable = true)
    public void getAttackDamageBonus(Player player, float f, CallbackInfoReturnable<Float> cir) {
        if (!canSmashAttack(player)) {
            cir.setReturnValue(0.0F);
        } else {
            float g = 3.0F;
            float h = 8.0F;
            float i = player.fallDistance;
            float j;
            if (i <= 3.0F) {
                j = 4.0F * i;
            } else if (i <= 8.0F) {
                j = 12.0F + 2.0F * (i - 3.0F);
            } else {
                j = 22.0F + i - 8.0F;
            }

            Level var10 = player.level();
            if (var10 instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)var10;
                int x = EnchantmentHelper.getEnchantmentLevel(Enchantments.DENSITY, player);
                float z = DensityEnchantment.calculateDamageAddition(x, player.fallDistance);
                cir.setReturnValue(j + z);
            } else {
                cir.setReturnValue(j);
            }
        }
    }

}