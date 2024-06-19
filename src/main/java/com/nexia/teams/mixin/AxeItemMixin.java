package com.nexia.teams.mixin;

import com.nexia.teams.koth.KothGame;
import com.nexia.teams.koth.KothGameHandler;
import com.nexia.teams.utilities.chat.ChatFormat;
import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class AxeItemMixin {
    @Inject(method = "useOn", at = @At("HEAD"))
    public void kothAxe(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        Player player = useOnContext.getPlayer();

        String kothName = null;
        for (String tag : player.getTags()) {
            if (tag.startsWith("koth_")) {
                kothName = tag.substring(5);
            }
        }

        if (kothName != null) {
            KothGame kothGame = KothGameHandler.getKothGameByName(kothName);
            assert kothGame != null;
            if (kothGame.initialCoordinates == null) {
                kothGame.initialCoordinates = useOnContext.getClickedPos();

                Component blockPosMessage = Component.text(String.format("Block position set (%s, %s, %s)", useOnContext.getClickedPos().getX(), useOnContext.getClickedPos().getY(), useOnContext.getClickedPos().getZ()));
                player.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(blockPosMessage)));
            } else {
                kothGame.area = new AABB(kothGame.initialCoordinates.getX(), kothGame.initialCoordinates.getY(), kothGame.initialCoordinates.getZ(), useOnContext.getClickedPos().getX(), useOnContext.getClickedPos().getY(), useOnContext.getClickedPos().getZ());

                Component blockPosMessage = Component.text(String.format("Block position set (%s, %s, %s)", useOnContext.getClickedPos().getX(), useOnContext.getClickedPos().getY(), useOnContext.getClickedPos().getZ()));
                player.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(blockPosMessage)));

                player.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Finished editing koth."))));

                player.removeTag("koth_" + kothName);
            }
        }
    }
}
