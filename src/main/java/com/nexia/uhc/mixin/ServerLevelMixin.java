package com.nexia.uhc.mixin;

import com.google.common.collect.Maps;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    @Shadow public abstract RegistryAccess registryAccess();

    protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, DimensionType dimensionType, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l) {
        super(writableLevelData, resourceKey, dimensionType, supplier, bl, bl2, l);
    }

    @ModifyArg(method = "<init>", index = 5, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureManager;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/chunk/ChunkGenerator;IZLnet/minecraft/server/level/progress/ChunkProgressListener;Ljava/util/function/Supplier;)V"))
    private ChunkGenerator makeOverworldVoid(ChunkGenerator chunkGenerator) {
        if (this.dimension().equals(Level.OVERWORLD)) {
            return new FlatLevelSource(new FlatLevelGeneratorSettings(new StructureSettings(Optional.empty(), Maps.newHashMap()), this.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)));
        }

        return chunkGenerator;
    }
}
