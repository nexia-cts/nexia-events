package com.nexia.teams;

import com.google.common.base.Suppliers;
import com.nexia.teams.commands.CommandLoader;
import com.nexia.teams.utilities.chat.ChatFormat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyori.adventure.text.Component;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NexiaTeams implements ModInitializer {
    public static final String MOD_ID = "nexia-teams";
    public static Logger logger = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        ServerLifecycleEvents.SERVER_STARTED.register((server -> {
            ChatFormat.provider = Suppliers.ofInstance(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)).get();
        }));

        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
            handler.getPlayer().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("hi"))));
        }));

        logger.info("Loading mod...");
        logger.info("Registering commands...");
        CommandLoader.registerCommands();
        logger.info("Registered commands.");
    }
}
