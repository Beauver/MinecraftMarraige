package com.beauver.minecraft.mods.marraigemod;

import com.beauver.minecraft.mods.marraigemod.Commands.MarryCommand;
import com.beauver.minecraft.mods.marraigemod.Listeners.RightClickListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class MarriageMod implements ModInitializer {

    public static MinecraftServer server;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::setServer);

        registerCommands();
        registerListeners();
    }

    private void registerListeners(){
        RightClickListener.registerEvents();
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register(MarryCommand::register);
    }

    private void setServer(MinecraftServer server) {
        MarriageMod.server = server;
    }
}
