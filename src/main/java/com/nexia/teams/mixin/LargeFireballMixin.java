package com.nexia.teams.mixin;

import com.nexia.teams.utilities.chat.ChatFormat;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
            BlockPos chestPosition;

            List<BlockPos> toBlow = explode.getToBlow();
            if (!toBlow.isEmpty()) {
                toBlow.sort(Comparator.comparing(BlockPos::getY));
                chestPosition = toBlow.getFirst();
            } else {
                chestPosition = BlockPos.containing(this.getPosition(0));
            }

            this.level().setBlockAndUpdate(chestPosition, Blocks.CHEST.defaultBlockState());
            this.level().getBlockEntity(chestPosition).applyComponents(DataComponentMap.builder().set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(MiniMessage.miniMessage().deserialize(String.format("<bold><gradient:%s:%s>Meteor Supply Drop</gradient></bold>", ChatFormat.brandColor1, ChatFormat.brandColor2)))).build(), DataComponentPatch.builder().build());
            RandomizableContainer.setBlockEntityLootTable(this.level(), this.level().getRandom(), chestPosition, ResourceKey.create(Registries.LOOT_TABLE, new ResourceLocation("nexia:chests/meteor")));
        }
        this.discard();
        ci.cancel();
    }
}
