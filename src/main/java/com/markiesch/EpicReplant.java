package com.markiesch;

import com.markiesch.commands.ReplantCommand;
import com.markiesch.listeners.CropBreak;
import com.markiesch.listeners.CropTrample;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class EpicReplant extends JavaPlugin {
    public static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        // Register commands and Listeners
        if (getConfig().getBoolean("CropBreak.enabled"))
            getServer().getPluginManager().registerEvents(new CropBreak(), this);

        if (getConfig().getBoolean("CropTrampling.enabled"))
            getServer().getPluginManager().registerEvents(new CropTrample(), this);

        PluginCommand command = getCommand("epicReplant");
        if (command != null) command.setExecutor(new ReplantCommand());
        // Send a console message when the plugin is Enabled
        getServer().getConsoleSender().sendMessage("§aEpic Replant is now enabled");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("§cEpic Replant is now disabled");
    }
}