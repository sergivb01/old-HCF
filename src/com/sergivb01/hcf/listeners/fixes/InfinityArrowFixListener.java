package com.sergivb01.hcf.listeners.fixes;

import com.sergivb01.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class InfinityArrowFixListener implements Listener{

	public InfinityArrowFixListener(HCF plugin){
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileHit(ProjectileHitEvent event){
		Arrow arrow;
		Projectile entity = event.getEntity();
		if(entity instanceof Arrow && (!((arrow = (Arrow) entity).getShooter() instanceof Player) || ((CraftArrow) arrow).getHandle().fromPlayer == 2)){
			arrow.remove();
		}
	}
}

