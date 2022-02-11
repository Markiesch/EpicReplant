package com.markiesch.listeners;

import com.markiesch.EpicReplant;
import com.markiesch.events.CropTrampleEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class CropTrample implements Listener {
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) {
            return;
        }

        CropTrampleEvent cropTrampleEvent = new CropTrampleEvent(event.getPlayer(), event.getClickedBlock());
        Bukkit.getPluginManager().callEvent(cropTrampleEvent);
        if (cropTrampleEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTrample(EntityInteractEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }

        CropTrampleEvent cropTrampleEvent = new CropTrampleEvent(event.getEntity(), event.getBlock());
        Bukkit.getPluginManager().callEvent(cropTrampleEvent);
        if (cropTrampleEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCropTrample(CropTrampleEvent event) {
        FileConfiguration config = EpicReplant.getInstance().getConfig();
        List<String> disabledWorlds = config.getStringList("CropTrampling.disabledWorlds");
        if (disabledWorlds.contains(event.getBlock().getWorld().getName())) {
            return;
        }

        if (event.getBlock().getType() != Material.FARMLAND) {
            return;
        }

        if (event.getTrampler() instanceof Player) {
            Player player = ((Player) event.getTrampler());

            if (config.getBoolean("CropTrampling.requirePermission") && !player.hasPermission("epicreplant.trampling")) {
                return;
            }

            if (config.getBoolean("CropTrampling.requireBoots")
                    && (player.getInventory().getBoots() == null || !player.getInventory().getBoots().getType().equals(Material.LEATHER_BOOTS))) {
                return;
            }
            return;
        }

        List<String> mobs = config.getStringList("CropTrampling.blacklistedMobs");
        if (mobs.contains(event.getTrampler().getType().toString()) || mobs.contains("*")) {
            return;
        }
        event.setCancelled(true);
    }
}