package com.markiesch.commands;

import com.markiesch.EpicReplant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ReplantCommand implements CommandExecutor {
    private final Plugin plugin = EpicReplant.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String helpPage =
            "§c§lEpicReplant §8>> §7version " + plugin.getDescription().getVersion() + " Created with <3 by §cMarkiesch" +
            "\n\n§8- §c/epicreplant §7- Shows this help page" +
            "\n§8- §c/epicreplant reload §7- Reloads the config files.";

        if (args.length == 0) {
            sender.sendMessage(helpPage);
            return true;
        }

        if ("reload".equalsIgnoreCase(args[0])) {
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
        }

        return true;
    }
}