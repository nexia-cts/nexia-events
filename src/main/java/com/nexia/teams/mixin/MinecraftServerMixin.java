package com.nexia.teams.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow @Final protected WorldData worldData;

    @ModifyArg(method = "setDifficulty", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/WorldData;setDifficulty(Lnet/minecraft/world/Difficulty;)V"))
    public Difficulty makeHardcoreEasy(Difficulty difficulty) {
        return this.worldData.isHardcore() ? Difficulty.EASY : difficulty;
    }
}
