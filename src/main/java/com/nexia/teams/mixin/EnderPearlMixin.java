package com.nexia.teams.mixin;

import net.minecraft.world.item.EnderpearlItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EnderpearlItem.class)
public class EnderPearlMixin {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemCooldowns;addCooldown(Lnet/minecraft/world/item/Item;I)V"), method = "use", index = 1)
    public int addEnderPearlCooldown(int i) {
        // returns 30 seconds in minecraft ticks
        return 300;
    }
}
