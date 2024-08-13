package com.nexia.uhc.game;

import com.nexia.nexus.api.util.Identifier;
import com.nexia.nexus.api.world.entity.player.Player;
import com.nexia.nexus.api.world.item.ItemStack;
import com.nexia.nexus.api.world.types.Minecraft;
import com.nexia.nexus.api.world.util.Location;
import com.nexia.uhc.NexiaUHC;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private final List<Player> lobbyPlayers = new ArrayList<>();
    private final Location spawn;

    public Lobby(Location spawn) {
        this.spawn = spawn;
    }

    public Location getSpawn() {
        return spawn;
    }

    public List<Player> getPlayers() {
        return lobbyPlayers;
    }

    public boolean isInLobby(Player player) {
        return this.lobbyPlayers.contains(player) || player.getWorld().equals(NexiaUHC.nexusServer.getWorld(new Identifier("overworld")));
    }

    public void returnToLobby(Player player, Minecraft.GameMode gameMode, boolean teleport) {
        if (!this.lobbyPlayers.contains(player)) this.lobbyPlayers.add(player);

        player.setInvulnerabilityTime(0);
        player.clearEffects();
        player.getInventory().clear();
        player.setRemainingFireTicks(0);
        player.setExperienceLevel(0);
        player.setExperiencePoints(0);
        player.getInventory().setCursorStack(ItemStack.create(Minecraft.Item.AIR));
        player.setGlowing(false);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setAbleToFly(false);

        if (teleport) {
            player.setGameMode(gameMode);
            player.teleport(this.spawn);
        }
    }
}
