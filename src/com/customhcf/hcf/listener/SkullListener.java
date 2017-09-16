package com.customhcf.hcf.listener;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.customhcf.hcf.Utils.ConfigurationService;

public class SkullListener
implements Listener {
    private static final String KILL_BEHEAD_PERMISSION = "hcf.kill.behead";

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
    	if(ConfigurationService.KIT_MAP){
    		return;
    	}
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if (killer != null && killer.hasPermission("hcf.kill.behead")) {
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.getData());
            SkullMeta meta = (SkullMeta)skull.getItemMeta();
            meta.setOwner(player.getName());
            skull.setItemMeta((ItemMeta)meta);
            event.getDrops().add(skull);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            BlockState state = event.getClickedBlock().getState();
            if (state instanceof Skull) {
                Skull skull;
                player.sendMessage((Object)ChatColor.YELLOW + "This head belongs to " + (Object)ChatColor.WHITE + ((skull = (Skull)state).getSkullType() == SkullType.PLAYER && skull.hasOwner() ? skull.getOwner() : new StringBuilder().append("a ").append(WordUtils.capitalizeFully((String)skull.getSkullType().name())).append(" skull").toString()) + (Object)ChatColor.YELLOW + '.');
            }
        }
    }
}

