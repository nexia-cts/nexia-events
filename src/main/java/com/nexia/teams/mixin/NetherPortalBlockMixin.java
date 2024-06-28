package com.nexia.teams.mixin;

import com.nexia.teams.NexiaTeams;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void disableNetherPortal(BlockState blockState, Level level, BlockPos blockPos, Entity entity, CallbackInfo ci) {
        if (NexiaTeams.netherDisabled) {
            ci.cancel();
        }
    }
}
