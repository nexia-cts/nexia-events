package com.nexia.uhc.utility;

import com.nexia.uhc.listeners.*;

public abstract class ListenerLoader {
    public static void registerListeners() {
        new PlayerDamageListener().registerListener();
        new PlayerDeathListener().registerListener();
        new PlayerHungerListener().registerListener();
        new PlayerInteractListener().registerListener();
        new PlayerJoinListener().registerListener();
        new PlayerLeaveListener().registerListener();
        new PlayerMoveListener().registerListener();
        new PlayerRespawnListener().registerListener();
        new ServerEventsListener().registerListener();
    }
}
