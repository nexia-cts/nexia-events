package com.nexia.teams;

import com.google.common.base.Suppliers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyori.adventure.text.Component;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;

public class NexiaTeams implements ModInitializer {
    @Override
    public void onInitialize() {

        ServerLifecycleEvents.SERVER_STARTED.register((server -> {
            ChatFormat.provider = Suppliers.ofInstance(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)).get();
        }));

        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
            handler.getPlayer().sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("hi"))));
        }));
    }
}
