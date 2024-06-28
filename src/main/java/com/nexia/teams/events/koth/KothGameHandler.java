package com.nexia.teams.events.koth;

import java.util.ArrayList;
import java.util.List;

public abstract class KothGameHandler {
     public static List<KothGame> kothGames = new ArrayList<>();

     public static KothGame getKothGameByName(String name) {
          for (KothGame kothGame : kothGames) {
               if (kothGame.name.equalsIgnoreCase(name)) {
                    return kothGame;
               }
          }

          return null;
     }

     public static void stopAllKothGames() {
          for (KothGame kothGame : kothGames) {
               if (kothGame.isRunning) kothGame.end(null);
          }
     }

     public static void kothGamesSecond() {
          try {
               for (KothGame kothGame : kothGames) {
                    if(kothGame == null) return;
                    kothGame.kothSecond();
               }
          } catch (Exception ignored) { }
     }
}
