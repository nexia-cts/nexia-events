package com.nexia.uhc.mixin;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
    // Fix getting sent to the wrong end dimension in a dynamic world
    @ModifyArg(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;"))
    public ResourceKey<Level> handleDynamicEnd(ResourceKey<Level> resourceKey) {
        String levelNamespace = resourceKey.location().getNamespace();
        ResourceKey<Level> end = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(levelNamespace, "the_end"));
        ResourceKey<Level> overworld = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(levelNamespace, "overworld"));

        return resourceKey == end ? overworld : end;
    }
}
