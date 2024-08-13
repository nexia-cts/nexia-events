package com.nexia.uhc.game;

import com.nexia.nexus.api.world.entity.player.Player;
import com.nexia.nexus.api.world.types.Minecraft;
import com.nexia.uhc.NexiaUHC;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    ArrayList<Game> games = new ArrayList<>();
    Path instancesDir = Path.of(FabricLoader.getInstance().getGameDir() + "/uhc");

    public List<Game> getGames() {
        return this.games;
    }

    public void createGame() {
        List<Player> players = NexiaUHC.lobby.getPlayers();
        Game game = new Game(players);
        NexiaUHC.lobby.getPlayers().removeAll(players);

        games.add(game);
        game.start();
    }

    public void tickAllGames() {
        for (Game game : this.games) {
            if (game.getPlayers().size() == 1 && game.isRunning) {
                Player winner = game.getPlayers().getFirst();
                game.getPlayers().forEach(player -> {
                    player.sendTitle(Title.title(winner.getName().color(NamedTextColor.GOLD), Component.text("has won the game!")));
                    player.playSound(Minecraft.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                });

                NexiaUHC.taskScheduler.schedule(() -> NexiaUHC.manager.deleteGame(game), 100);
                game.isRunning = false;
            }

            if (game.isRunning) game.tick();
        }
    }

    public void deleteGame(Game game) {
        game.end();
        this.games.remove(game);
    }

    public Game getGame(Player player) {
        for (Game game : this.games) {
            if (game.players.contains(player)) {
                return game;
            }
        }

        return null;
    }
}
