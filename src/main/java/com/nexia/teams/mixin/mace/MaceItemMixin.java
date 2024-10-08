package com.nexia.teams.mixin.mace;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.enchantment.DensityEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
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
            float i = player.fallDistance;
            float j;
            if (i <= 3.0F) {
                j = 4.0F * i;
            } else if (i <= 8.0F) {
                j = 12.0F + 2.0F * (i - 3.0F);
            } else {
                j = 22.0F + i - 8.0F;
            }

            int x = EnchantmentHelper.getEnchantmentLevel(Enchantments.DENSITY, player);
            float z = DensityEnchantment.calculateDamageAddition(x, player.fallDistance);
            z = (float) (z / 2.8);
            j = (float) (j / 1.4);
            cir.setReturnValue(j + z);
        }
    }

}