package com.markiesch.listeners;

import com.markiesch.EpicReplant;
import org.bukkit.Material;
import org.bukkit.World;
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
    private final FileConfiguration config;

    public CropTrample(EpicReplant instance) {
        config = instance.getConfig();
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.FARMLAND)) return;
        if (!event.getAction().equals(Action.PHYSICAL) || isDisabledWorld(event.getPlayer().getLocation().getWorld())) return;

        Player player = event.getPlayer();
        if (config.getBoolean("CropTrampling.requirePermission") && !player.hasPermission("epicreplant.trampling")) return;
        if (config.getBoolean("CropTrampling.requireBoots") && (player.getInventory().getBoots() == null || !player.getInventory().getBoots().getType().equals(Material.LEATHER_BOOTS))) return;

        event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTrample(EntityInteractEvent event) {
        if (isDisabledWorld(event.getBlock().getWorld()) || !event.getBlock().getType().equals(Material.FARMLAND)) return;

        List<String> mobs = config.getStringList("CropTrampling.blacklistedMobs");
        if (mobs.contains(event.getEntity().getType().toString()) || mobs.contains("*")) return;
        event.setCancelled(true);
    }

    public boolean isDisabledWorld(World world) {
        if (world == null) return true;

        List<String> disabledWorlds = config.getStringList("CropTrampling.disabledWorlds");
        return disabledWorlds.contains(world.toString());
    }
}