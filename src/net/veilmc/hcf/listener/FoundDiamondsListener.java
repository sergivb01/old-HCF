
package net.veilmc.hcf.listener;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import net.veilmc.hcf.HCF;

public class FoundDiamondsListener
implements Listener {
    public static final Material SEARCH_TYPE = Material.DIAMOND_ORE;
    public final Set<String> foundLocations = new HashSet<String>();
    private final HCF plugin;

    public FoundDiamondsListener(HCF plugin) {
        this.plugin = plugin;
        if (!plugin.getConfig().getBoolean("found-diamonds", true)) {
            Bukkit.getScheduler().runTaskLater((Plugin)plugin, () -> {
                HandlerList.unregisterAll((Listener)this);
            }
            , 5);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (block.getType() != SEARCH_TYPE) continue;
            this.foundLocations.add(block.getLocation().toString());
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() == SEARCH_TYPE) {
            this.foundLocations.add(block.getLocation().toString());
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (player.getItemInHand().getEnchantments().containsKey((Object)Enchantment.SILK_TOUCH)) {
            return;
        }
        Block block = event.getBlock();
        Location blockLocation = block.getLocation();
        if (block.getType() == SEARCH_TYPE && this.foundLocations.add(blockLocation.toString())) {
            int count = 1;
            for (int x = -5; x < 5; ++x) {
                for (int y = -5; y < 5; ++y) {
                    for (int z = -5; z < 5; ++z) {
                        Block otherBlock = blockLocation.clone().add((double)x, (double)y, (double)z).getBlock();
                        if (otherBlock.equals((Object)block) || otherBlock.getType() != SEARCH_TYPE || !this.foundLocations.add(otherBlock.getLocation().toString())) continue;
                        ++count;
                    }
                }
            }
            this.plugin.getUserManager().getUser(player.getUniqueId()).setDiamondsMined(this.plugin.getUserManager().getUser(player.getUniqueId()).getDiamondsMined() + count);
            for (Player on : Bukkit.getServer().getOnlinePlayers()) {
                String message;
                if (this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) != null) {
                    message = ChatColor.WHITE + "[FD] " + ChatColor.AQUA + player.getName() + ChatColor.AQUA + " has found " + count + " diamonds.";
                    on.sendMessage(message);
                    continue;
                }
                message = ChatColor.WHITE + "[FD] " + ChatColor.AQUA + player.getName() + ChatColor.AQUA + " has found " + count + " diamonds.";
                //message = (Object)ChatColor.RED + player.getName() + (Object)ChatColor.GRAY + " has found" + (Object)ChatColor.AQUA + " Diamonds " + (Object)ChatColor.GRAY + '[' + (Object)ChatColor.AQUA + count + (Object)ChatColor.GRAY + ']';
                on.sendMessage(message);
            }
        }
    }
}

