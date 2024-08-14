package com.nexia.uhc.mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {
    /**
     * @author NotInfinityy
     * @reason This fixes an issue where you can't ignite nether portals in nexus dynamic worlds.
     */
    @Overwrite
    private static boolean inPortalDimension(Level level) {
        String levelName = level.dimension().location().getPath();
        return levelName.equals("overworld") || levelName.equals("the_nether");
    }
}
