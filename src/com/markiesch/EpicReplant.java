package com.markiesch;

import com.markiesch.commands.ReplantCommand;
import com.markiesch.listeners.CropBreakEvent;
import com.markiesch.listeners.CropTrample;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class EpicReplant extends JavaPlugin implements Listener {
    public static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        // Register commands and Listeners
        if (getConfig().getBoolean("CropBreak.enabled")) {
            getServer().getPluginManager().registerEvents(new CropBreakEvent(), this);
        }
        if (getConfig().getBoolean("preventCropTrampling")) {
            getServer().getPluginManager().registerEvents(new CropTrample(), this);
        }
        Objects.requireNonNull(getCommand("epicReplant")).setExecutor(new ReplantCommand());
        // Send a console message when the plugin is Enabled
        getServer().getConsoleSender().sendMessage(changeColor("&aEpicPunishments is now enabled"));
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Send a console message when the plugin is Disabled
        getServer().getConsoleSender().sendMessage(changeColor("&aEpicPunishments is now disabled"));
    }

    public static Plugin getInstance() { return instance; }
    public static String changeColor(String s) { return ChatColor.translateAlternateColorCodes('&', s); }
}
