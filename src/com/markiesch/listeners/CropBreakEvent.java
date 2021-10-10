package com.markiesch.listeners;

import com.markiesch.EpicReplant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class CropBreakEvent implements Listener {
    private final Plugin plugin = EpicReplant.getInstance();

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void breakEvent(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!player.hasPermission("epicreplant.replant")) return;

        // Check if held item meets requirements
        List<String> requiredItems = plugin.getConfig().getStringList("CropBreak.requiredItems");
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!checkTools(requiredItems, heldItem.getType().toString())) return;

        if (plugin.getConfig().getBoolean("CropBreak.requireEnchants")) {
            List<String> requiredEnchants = plugin.getConfig().getStringList("CropBreak.requiredEnchants");
            if (!checkEnchants(requiredEnchants, heldItem)) return;
        }

        Block block = e.getBlock();
        Material material = block.getType();
        Material cropBlockType = null;
        Material seedVariant = null;

        if (material.equals(Material.WHEAT)) {
            cropBlockType = Material.WHEAT;
            seedVariant = Material.WHEAT_SEEDS;
        } else if (material.equals(Material.CARROTS)) {
            cropBlockType = Material.CARROTS;
            seedVariant = Material.CARROT;
        } else if (material.equals(Material.POTATOES)) {
            cropBlockType = Material.POTATOES;
            seedVariant = Material.POTATO;
        } else if (material.equals(Material.NETHER_WART)) {
            cropBlockType = Material.NETHER_WART;
        } else if (material.equals(Material.BEETROOTS)) {
            cropBlockType = Material.BEETROOTS;
            seedVariant = Material.BEETROOT_SEEDS;
        }

        if (cropBlockType == null) return;
        if (seedVariant == null) seedVariant = cropBlockType;

        World world = e.getPlayer().getWorld();

        e.setDropItems(false);
        Iterator<ItemStack> iterator = e.getBlock().getDrops().iterator();
        boolean removedAItem = false;
        while(iterator.hasNext()) {
            ItemStack item = iterator.next();
            if (item.getType().equals(Material.AIR)) continue;
            if (!removedAItem && item.getType().equals(seedVariant)) {
                item.setAmount(item.getAmount() - 1);
                removedAItem = true;
            }
            if (item.getAmount() < 1) continue;
            world.dropItemNaturally(e.getBlock().getLocation(), item);
        }

        Location location = block.getLocation();
        Material finalCropBlockType = cropBlockType;
        Material finalSeedVariant = seedVariant;
        Bukkit.getScheduler().runTaskLater(EpicReplant.getInstance(),() -> {
            Location baseBlock = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
            // Returns if the block under the crop is not a farmland
            if (baseBlock.getBlock().getType() != Material.FARMLAND && baseBlock.getBlock().getType() != Material.SOUL_SAND) {
                world.dropItemNaturally(e.getBlock().getLocation(), new ItemStack(finalSeedVariant));
                return;
            }
            location.getBlock().setType(finalCropBlockType);
            if (location.getWorld() != null) {
                try {
                    Particle particle = Particle.valueOf(plugin.getConfig().getString("CropBreak.particle"));
                    int amount = plugin.getConfig().getInt("CropBreak.particleAmount");
                    if (amount > 0) {
                        location.getWorld().spawnParticle(particle, location.add(0.5, 0.25, 0.5), amount, 0.25, 0.10, 0.25);
                    }
                } catch (IllegalArgumentException error) {
                    plugin.getServer().getConsoleSender().sendMessage("\"" + plugin.getConfig().getString("CropBreak.particle") + "\" is not a valid particle");
                }
            }
        }, 15L);
    }

    public boolean checkTools(List<String> arr, String value) {
        for (String element : arr)
            if (element.equals(value)) return true;
        return false;
    }

    public boolean checkEnchants(List<String> arr, ItemStack item) {
        if (arr.isEmpty()) return true;
        boolean requireAllEnchants = plugin.getConfig().getBoolean("CropBreak.requireAllEnchants");
        int count = 0;

        for (String element : arr) {
            Enchantment name = Enchantment.getByKey(NamespacedKey.minecraft(element.toLowerCase(Locale.US)));
            if (name != null && item.containsEnchantment(name)) {
                if (!requireAllEnchants) {
                    return true;
                } else {
                    count++;
                }
            }
        }
        return count == arr.size();
    }
}
