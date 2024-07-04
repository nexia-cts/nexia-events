package com.nexia.teams.events.koth;

import java.util.ArrayList;
import java.util.List;

public abstract class KothGameHandler {
     public static List<KothGame> kothGames = new ArrayList<>();

     public static KothGame getKothGameByName(String name) {
          try {
               for (KothGame kothGame : kothGames) {
                    if(kothGame == null || !kothGame.name.equalsIgnoreCase(name)) continue;
                    return kothGame;
               }
          } catch (Exception ignored) { }

          return null;
     }

     public static void stopAllKothGames() {
          try {
               for (KothGame kothGame : kothGames) {
                    if(kothGame == null || !kothGame.isRunning) continue;
                    kothGame.end(null);
               }
          } catch (Exception ignored) { }
     }

     public static void kothGamesSecond() {
          try {
               for (KothGame kothGame : kothGames) {
                    if(kothGame == null || !kothGame.isRunning) continue;
                    kothGame.kothSecond();
               }
          } catch (Exception ignored) { }
     }
}
