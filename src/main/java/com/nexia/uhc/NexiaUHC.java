package com.nexia.uhc;

import com.nexia.nexus.api.NexusAPI;
import com.nexia.nexus.api.NexusServer;
import com.nexia.nexus.api.entrypoint.NexusPlugin;
import com.nexia.nexus.api.scheduler.TaskScheduler;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NexiaUHC implements ModInitializer, NexusPlugin {
    public static final String MOD_ID = "nexia-uhc";
    public static Logger logger = LogManager.getLogger(MOD_ID);
    public static NexusAPI nexusAPI;
    public static NexusServer nexusServer;
    public static TaskScheduler taskScheduler;

    @Override
    public void onInitialize() {
        logger.info("Initialized Nexia UHC");
    }

    @Override
    public void onNexusLoad(NexusAPI nexusAPI, NexusServer nexusServer) {
        NexiaUHC.nexusAPI = nexusAPI;
        NexiaUHC.nexusServer = nexusServer;
        NexiaUHC.taskScheduler = nexusAPI.getScheduler();
    }
}

