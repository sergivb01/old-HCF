package com.sergivb01.hcf.listeners.fixes;

import com.sergivb01.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class VoidGlitchFixListener implements Listener{

	public VoidGlitchFixListener(HCF plugin){
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerDamage(EntityDamageEvent event){
		Entity entity;
		if(event.getCause() == EntityDamageEvent.DamageCause.VOID && (entity = event.getEntity()) instanceof Player){
			if(entity.getWorld().getEnvironment() == World.Environment.THE_END){
				return;
			}
			Location destination = Bukkit.getWorld("world").getSpawnLocation();
			if(destination == null){
				return;
			}
			if(entity.teleport(destination, PlayerTeleportEvent.TeleportCause.PLUGIN)){
				event.setCancelled(true);
				((Player) entity).sendMessage(ChatColor.RED + "You were saved from the void.");
			}
		}
	}
}

