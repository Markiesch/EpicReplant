package com.markiesch.listeners;

import com.markiesch.EpicReplant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CropTrample implements Listener {
    private final Plugin plugin = EpicReplant.getInstance();

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Player player = event.getPlayer();
        if (plugin.getConfig().getBoolean("CropTrampling.requirePermission") && !player.hasPermission("epicreplant.trampling")) return;

        List<String> disabledWorlds = plugin.getConfig().getStringList("CropTrampling.disabledWorlds");
        if (disabledWorlds.contains(block.getWorld().getName())) return;

        if (block.getType() != Material.FARMLAND) return;
        if (plugin.getConfig().getBoolean("CropTrampling.requireBoots")) {
            if (player.getInventory().getBoots() == null || !player.getInventory().getBoots().getType().equals(Material.LEATHER_BOOTS)) return;
        }
        event.setCancelled(true);
    }
}