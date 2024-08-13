package com.nexia.uhc.game;

import com.nexia.nexus.api.world.entity.player.Player;
import com.nexia.uhc.NexiaUHC;
import net.fabricmc.loader.api.FabricLoader;

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
            if (game.players.size() == 1) continue;
            game.tick();
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
