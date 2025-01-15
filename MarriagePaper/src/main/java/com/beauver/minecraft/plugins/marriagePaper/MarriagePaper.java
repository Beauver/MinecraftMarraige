package com.beauver.minecraft.plugins.marriagePaper;

import co.aikar.commands.PaperCommandManager;
import com.beauver.minecraft.plugins.marriagePaper.Commands.MarryCommand;
import com.beauver.minecraft.plugins.marriagePaper.Listeners.RightClickListener;
import com.beauver.minecraft.plugins.marriagePaper.Util.MarriageHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class MarriagePaper extends JavaPlugin {

    public static MarriagePaper plugin = null;


    @Override
    public void onEnable() {
        if(plugin == null){
            plugin = this;
        }

        registerCommands();
        registerEventListeners();
    }

    void registerCommands(){
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new MarryCommand());
    }

    void registerEventListeners(){
        getServer().getPluginManager().registerEvents(new RightClickListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
