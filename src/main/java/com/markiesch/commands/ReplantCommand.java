package com.markiesch.commands;

import com.markiesch.EpicReplant;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ReplantCommand implements CommandExecutor {
    private final Plugin plugin;

    public ReplantCommand(EpicReplant instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && "reload".equalsIgnoreCase(args[0])) {
            if (!sender.hasPermission("epicreplant.reload")) {
                sender.sendMessage("§7You do not have§c permissions §7to run this command!");
                return true;
            }

            try {
                long startTime = System.currentTimeMillis();
                plugin.reloadConfig();
                sender.sendMessage("§aConfig files reloaded! Took " + (System.currentTimeMillis() - startTime) + "ms");
            } catch (Exception error) {
                sender.sendMessage("§cFailed to load config file! Check spelling!" + error.getMessage());
            }

            return true;
        }

        if (!plugin.getConfig().getBoolean("HelpPage.enabled")) return true;

        List<String> helpPage = plugin.getConfig().getStringList("HelpPage.content");
        for (String line : helpPage) sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line.replace("[version]",  plugin.getDescription().getVersion())));

        return true;
    }
}