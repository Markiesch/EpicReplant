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

public class CropTrample implements Listener {
    private static final EpicReplant plugin = EpicReplant.getPlugin(EpicReplant.class);

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.PHYSICAL)) return;
        Player player = e.getPlayer();
        if (!player.hasPermission("epicreplant.trampling")) return;
        Block block = e.getClickedBlock();
        if (block == null) return;
        if (plugin.getConfig().getBoolean("requireBoots")) {
            if (player.getInventory().getBoots() == null) return;
            if (!player.getInventory().getBoots().getType().equals(Material.LEATHER_BOOTS)) return;
        }
        if (block.getType() == Material.FARMLAND) e.setCancelled(true);
    }
}
