package com.beauver.minecraft.plugins.marriagePaper;

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

    }

    void registerEventListeners(){

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
