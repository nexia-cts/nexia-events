package com.nexia.teams.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;

@Mixin(LargeFireball.class)
public abstract class LargeFireballMixin extends Fireball {
    @Shadow public int explosionPower;

    public LargeFireballMixin(EntityType<? extends Fireball> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    private void dropSupply(HitResult hitResult, CallbackInfo ci) {
        super.onHit(hitResult);
        boolean bl = this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        Explosion explode = this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionPower, bl, Level.ExplosionInteraction.MOB);

        if (this.getTags().contains("meteor")) {
            List<BlockPos> toBlow = explode.getToBlow();
            toBlow.sort(Comparator.comparing(BlockPos::getY));

            BlockPos chestPos = toBlow.getFirst();
            this.level().setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 1);
            RandomizableContainer.setBlockEntityLootTable(this.level(), this.level().getRandom(), chestPos, BuiltInLootTables.END_CITY_TREASURE);
        }
        this.discard();
        ci.cancel();
    }
}
