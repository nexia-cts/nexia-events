package com.nexia.uhc.mixin;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public Level level;

    // Fix getting sent to the wrong nether in a dynamic world
    @ModifyArg(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;"))
    private ResourceKey<Level> handleDynamicNether(ResourceKey<Level> resourceKey) {
        String levelNamespace = this.level.dimension().location().getNamespace();
        ResourceKey<Level> nether = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(levelNamespace, "the_nether"));
        ResourceKey<Level> overworld = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(levelNamespace, "overworld"));

        return this.level.dimension() == nether ? overworld : nether;
    }

    @Redirect(method = "changeDimension", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;END:Lnet/minecraft/resources/ResourceKey;"))
    private ResourceKey<Level> dynamicObsidianPlatform() {
        String levelNamespace = this.level.dimension().location().getNamespace();
        return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(levelNamespace, "the_end"));
    }

    @Redirect(method = "findDimensionEntryPoint", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;END:Lnet/minecraft/resources/ResourceKey;"))
    private ResourceKey<Level> changeEnd() {
        String levelNamespace = this.level.dimension().location().getNamespace();
        return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(levelNamespace, "the_end"));
    }

    @Redirect(method = "findDimensionEntryPoint", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;NETHER:Lnet/minecraft/resources/ResourceKey;"))
    private ResourceKey<Level> changeNether() {
        String levelNamespace = this.level.dimension().location().getNamespace();
        return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(levelNamespace, "the_nether"));
    }

    @Redirect(method = "findDimensionEntryPoint", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;OVERWORLD:Lnet/minecraft/resources/ResourceKey;"))
    private ResourceKey<Level> changeOverworld() {
        String levelNamespace = this.level.dimension().location().getNamespace();
        return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(levelNamespace, "overworld"));
    }
}
