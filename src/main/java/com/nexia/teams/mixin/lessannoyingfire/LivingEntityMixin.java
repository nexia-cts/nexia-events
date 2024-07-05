package com.nexia.teams.mixin.lessannoyingfire;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Shadow
    @Nullable
    private DamageSource lastDamageSource;

    @Shadow @Final public int invulnerableDuration;
    @Unique
    DamageSource damageSource;

    @Inject(method = "hurt", at = @At(value = "FIELD", ordinal = 0, opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/entity/LivingEntity;invulnerableTime:I"))
    private void setSource1(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        damageSource = source;
    }

    @Redirect(method = "hurt", at = @At(value = "FIELD", ordinal = 0, opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/entity/LivingEntity;invulnerableTime:I"))
    private int getInvulnerabilityTicks(LivingEntity value) {
        // Make fire caused invulnerability ticks irrelevant if damage comes from an entity
        if (acceptableSource(value, lastDamageSource) && damageSource.getDirectEntity() != null) return 0;
        return invulnerableDuration;
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;markHurt()V"))
    private void setSource2(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        damageSource = source;
    }

    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;markHurt()V"))
    private void velocityUpdateCondition(LivingEntity instance) {
        // Prevent fire from messing up movement
        if (!acceptableSource(instance, damageSource)) ((LivingEntity) (Object) this).markHurt();
    }

    private static boolean acceptableSource(LivingEntity entity, DamageSource damageSource) {
        DamageSources damageSources = entity.level().damageSources();
        return damageSource == damageSources.onFire()
                || damageSource == damageSources.inFire()
                || damageSource == damageSources.wither()
                || damageSource == damageSources.sweetBerryBush() // funny
                || damageSource == damageSources.starve() // funny
                || damageSource == damageSources.cactus()
                || damageSource == damageSources.magic() // harming & poison ( i think )
                || damageSource == damageSources.stalagmite() // funny
                || damageSource == damageSources.fall()
                || damageSource == damageSources.freeze() // extra funny
                || damageSource == damageSources.lava()
                ;
    }
}
