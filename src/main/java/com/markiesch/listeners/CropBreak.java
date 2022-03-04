package com.markiesch.listeners;

import com.markiesch.EpicReplant;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CropBreak implements Listener {
    private final Plugin plugin;

    public CropBreak(EpicReplant instance) {
        plugin = instance;
    }

    private final HashMap<Material, Material> crops = new HashMap<Material, Material>(){{
        put(Material.WHEAT, Material.WHEAT_SEEDS);
        put(Material.CARROTS, Material.CARROT);
        put(Material.POTATOES, Material.POTATO);
        put(Material.NETHER_WART, Material.NETHER_WART);
        put(Material.BEETROOTS, Material.BEETROOT_SEEDS);
    }};

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void breakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material cropBlockType = block.getType();
        if (!crops.containsKey(cropBlockType)) return;

        Player player = event.getPlayer();
        if (plugin.getConfig().getBoolean("CropBreak.requirePermission") && !player.hasPermission("epicreplant.replant")) return;

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!checkTools(heldItem.getType().toString())) return;
        if (!checkEnchants(heldItem)) return;

        List<String> disabledWorlds = plugin.getConfig().getStringList("CropBreak.disabledWorlds");
        if (disabledWorlds.contains(block.getWorld().getName())) return;

        Material seedVariant = crops.get(cropBlockType);
        World world = event.getPlayer().getWorld();

        event.setDropItems(false);
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            Iterator<ItemStack> iterator = event.getBlock().getDrops(heldItem).iterator();
            boolean removedAItem = false;
            while(iterator.hasNext()) {
                ItemStack item = iterator.next();
                if (item.getType().equals(Material.AIR)) continue;
                if (!removedAItem && item.getType().equals(seedVariant)) {
                    item.setAmount(item.getAmount() - 1);
                    removedAItem = true;
                }
                if (item.getAmount() < 1) continue;
                world.dropItemNaturally(event.getBlock().getLocation(), item);
            }
            removeDurability(heldItem, player);
        }

        Location location = block.getLocation();

        long delay = plugin.getConfig().getLong("CropBreak.delay");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location baseBlock = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
            // Returns if the block under the crop is not a farmland
            if (
                baseBlock.getBlock().getType() != Material.FARMLAND &&
                baseBlock.getBlock().getType() != Material.SOUL_SAND ||
                block.getType() != Material.AIR
            ) {
                world.dropItemNaturally(event.getBlock().getLocation(), new ItemStack(seedVariant));
                return;
            }
            location.getBlock().setType(cropBlockType);
            if (location.getWorld() != null) {
                try {
                    Particle particle = Particle.valueOf(plugin.getConfig().getString("CropBreak.particle"));
                    int amount = plugin.getConfig().getInt("CropBreak.particleAmount");
                    if (amount > 0) location.getWorld().spawnParticle(particle, location.add(0.5, 0.25, 0.5), amount, 0.25, 0.10, 0.25);
                } catch (IllegalArgumentException error) {
                    plugin.getServer().getConsoleSender().sendMessage("\"" + plugin.getConfig().getString("CropBreak.particle") + "\" is not a valid particle");
                }
            }
        }, delay);
    }

    public boolean checkTools(String tool) {
        List<String> requiredItems = plugin.getConfig().getStringList("CropBreak.requiredItems");
        return (requiredItems.contains("*") || requiredItems.isEmpty() || requiredItems.contains(tool));
    }

    public boolean checkEnchants(ItemStack item) {
        if (!plugin.getConfig().getBoolean("CropBreak.requireEnchants")) return true;
        List<String> requiredEnchants = plugin.getConfig().getStringList("CropBreak.requiredEnchants");
        if (requiredEnchants.isEmpty()) return true;

        int count = 0;
        for (String element : requiredEnchants) {
            Enchantment name = Enchantment.getByKey(NamespacedKey.minecraft(element.toLowerCase(Locale.US)));
            if (name == null || !item.containsEnchantment(name)) continue;
            if (!plugin.getConfig().getBoolean("CropBreak.requireAllEnchants")) return true;
            count++;
        }
        return count == requiredEnchants.size();
    }

    public void removeDurability(ItemStack item, Player player) {
        if (!plugin.getConfig().getBoolean("CropBreak.useDurability")) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (item.getType().getMaxDurability() < 1 || meta.isUnbreakable()) return;

        Damageable damageableItem = (Damageable) meta;
        if (meta.hasEnchant(Enchantment.DURABILITY)) {
            Random random = new Random();
            if (random.nextInt(meta.getEnchantLevel(Enchantment.DURABILITY) + 1) != 0) return;
        }

        damageableItem.setDamage(damageableItem.getDamage() + 1);
        item.setItemMeta((ItemMeta)damageableItem);
        if (damageableItem.getDamage() < item.getType().getMaxDurability()) return;
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
        player.getInventory().remove(item);
    }
}