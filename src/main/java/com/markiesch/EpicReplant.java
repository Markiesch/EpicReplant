package com.markiesch;

import com.markiesch.commands.ReplantCommand;
import com.markiesch.listeners.CropBreak;
import com.markiesch.listeners.CropTrample;
import org.bukkit.plugin.java.JavaPlugin;

public class EpicReplant extends JavaPlugin {
    private static EpicReplant instance;
    public static EpicReplant getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (getConfig().getBoolean("CropBreak.enabled")) getServer().getPluginManager().registerEvents(new CropBreak(), this);
        if (getConfig().getBoolean("CropTrampling.enabled")) getServer().getPluginManager().registerEvents(new CropTrample(), this);

        getCommand("epicReplant").setExecutor(new ReplantCommand());

        getServer().getConsoleSender().sendMessage("§aEpic Replant is now enabled");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("§cEpic Replant is now disabled");
    }
}