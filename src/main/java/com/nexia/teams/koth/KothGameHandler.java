package com.nexia.teams.koth;

import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class KothGameHandler {
     public static List<KothGame> kothGames = new ArrayList<>();

     public static KothGame getKothGameByName(String name) {
          for (KothGame kothGame : kothGames) {
               if (kothGame.name.equalsIgnoreCase(name)) {
                    return kothGame;
               }
          }

          return null;
     }

     public static KothGame getKothGameByCreator(ServerPlayer serverPlayer) {
          for (KothGame kothGame : kothGames) {
               if (kothGame.getCreator().equals(serverPlayer)) {
                    return kothGame;
               }
          }

          return null;
     }

     public static void stopAllKothGames() {
          for (KothGame kothGame : kothGames) {
               kothGame.end();
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
