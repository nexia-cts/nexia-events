package com.nexia.teams.mixin;

import com.nexia.teams.utilities.time.ServerTime;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Unique boolean firstTickPassed = false;

    @Inject(method = "tickChildren", at = @At("HEAD"))
    private void tickHead(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        if (!firstTickPassed) {
            firstTickPassed = true;
            ServerTime.firstTick((MinecraftServer)(Object)this);
        }
    }

    @Inject(at = @At("TAIL"), method = "tickChildren")
    private void tickTail(CallbackInfo ci) {
        ServerTime.everyTick();
    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    protected void stopServer(CallbackInfo ci) {
        ServerTime.stopServer();
    }
}
