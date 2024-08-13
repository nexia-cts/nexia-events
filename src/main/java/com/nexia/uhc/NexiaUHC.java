package com.nexia.uhc;

import com.nexia.nexus.api.NexusAPI;
import com.nexia.nexus.api.NexusServer;
import com.nexia.nexus.api.entrypoint.NexusPlugin;
import com.nexia.nexus.api.scheduler.TaskScheduler;
import com.nexia.nexus.api.util.Identifier;
import com.nexia.nexus.api.world.util.Location;
import com.nexia.uhc.game.GameManager;
import com.nexia.uhc.game.Lobby;
import com.nexia.uhc.utility.CommandLoader;
import com.nexia.uhc.utility.ListenerLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NexiaUHC implements ModInitializer, NexusPlugin {
    public static final String MOD_ID = "nexia-uhc";
    public static Logger logger = LogManager.getLogger(MOD_ID);

    public static MinecraftServer minecraftServer;

    public static NexusAPI nexusAPI;
    public static NexusServer nexusServer;
    public static TaskScheduler taskScheduler;

    public static Lobby lobby;
    public static GameManager manager;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> NexiaUHC.minecraftServer = server);

        logger.info("Initialized Nexia UHC");
    }

    @Override
    public void onNexusLoad(NexusAPI nexusAPI, NexusServer nexusServer) {
        NexiaUHC.nexusAPI = nexusAPI;
        NexiaUHC.nexusServer = nexusServer;
        NexiaUHC.taskScheduler = nexusAPI.getScheduler();

        NexiaUHC.lobby = new Lobby(new Location(0.5, 80.0, 0.5, nexusServer.getWorld(new Identifier("overworld"))));
        NexiaUHC.manager = new GameManager();

        CommandLoader.registerCommands();
        ListenerLoader.registerListeners();
    }
}

