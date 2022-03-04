package com.markiesch;

import com.markiesch.commands.ReplantCommand;
import com.markiesch.listeners.CropBreak;
import com.markiesch.listeners.CropTrample;
import org.bukkit.plugin.java.JavaPlugin;

public class EpicReplant extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (getConfig().getBoolean("CropBreak.enabled")) getServer().getPluginManager().registerEvents(new CropBreak(this), this);
        if (getConfig().getBoolean("CropTrampling.enabled")) getServer().getPluginManager().registerEvents(new CropTrample(this), this);

        getCommand("epicReplant").setExecutor(new ReplantCommand(this));

        getServer().getConsoleSender().sendMessage("§aEpic Replant has been enabled");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("§cEpic Replant has been disabled");
    }
}