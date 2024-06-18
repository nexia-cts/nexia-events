package com.nexia.teams.koth;

import java.util.ArrayList;
import java.util.List;

public class KothGameHandler {
     static List<KothGame> kothGames = new ArrayList<>();

     public static List<KothGame> getKothGames() {
         return kothGames;
     }

     public static KothGame getKothGameByName(String name) {
          for (KothGame kothGame : kothGames) {
               if (kothGame.name == name) {
                    return kothGame;
               }
          }

          return null;
     }

     public static void stopAllKothGames() {
          for (KothGame kothGame : kothGames) {
               kothGame.isRunning = false;
          }
     }

     public static void kothGamesSecond() {
          for (KothGame kothGame : kothGames) {
               kothGame.kothSecond();
          }
     }
}
