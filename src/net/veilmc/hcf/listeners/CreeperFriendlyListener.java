package net.veilmc.hcf.listeners;

import net.veilmc.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class CreeperFriendlyListener implements Listener{

	public CreeperFriendlyListener(HCF plugin){
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onTargetEvent(EntityTargetEvent e){
		if((e.getEntity() instanceof Creeper)){
			e.setCancelled(true);
		}
	}
}
