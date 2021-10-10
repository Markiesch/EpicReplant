package com.markiesch.commands;

import com.markiesch.EpicReplant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ReplantCommand implements CommandExecutor {
    private final Plugin plugin = EpicReplant.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        String[] helpPage = {
                "&c&lEpicReplant &8>> &7version " + plugin.getDescription().getVersion() + "Created with <3 by &cMarkiesch",
                "&r",
                "&8- &c/replant &7- Shows this help page",
                "&8- &c/replant reload &7- Reloads the config files.",
        };

        if (args.length == 0) {
            for (String line : helpPage) player.sendMessage(EpicReplant.changeColor(line));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("epicreplant.reload")) {
                player.sendMessage(EpicReplant.changeColor("&cYou do not have permissions to run this command!"));
                return true;
            }
            try {
                long startTime = System.currentTimeMillis();
                plugin.reloadConfig();
                player.sendMessage(EpicReplant.changeColor("&aConfig files reloaded! Took " + (System.currentTimeMillis() - startTime) + "ms"));
            } catch (Exception e) {
                player.sendMessage(EpicReplant.changeColor("&cFailed to load config file! Check spelling!" + e.getMessage()));
            }
            return true;
        }
        return true;
    }
}
