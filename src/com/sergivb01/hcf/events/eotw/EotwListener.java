package com.sergivb01.hcf.events.eotw;

import com.sergivb01.base.kit.event.KitApplyEvent;
import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.faction.event.FactionClaimChangeEvent;
import com.sergivb01.hcf.faction.event.cause.ClaimChangeCause;
import com.sergivb01.hcf.faction.type.ClaimableFaction;
import com.sergivb01.hcf.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EotwListener
		implements Listener{
	private final HCF plugin;

	public EotwListener(HCF plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onMobSpawnFromSpawner(CreatureSpawnEvent e){
		if(this.plugin.getEotwHandler().isEndOfTheWorld()){
			switch(e.getSpawnReason()){
				case SPAWNER:{
					if(e.getEntity().getType() != EntityType.PIG){
						e.setCancelled(true);
					}
				}
				case SPAWNER_EGG:{
					if(e.getEntity().getType() != EntityType.PIG){
						e.setCancelled(true);
					}
				}
				case DISPENSE_EGG:{
					if(e.getEntity().getType() == EntityType.PIG) break;
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void KitApplyEvent(KitApplyEvent event){
		if(!event.isForce() && this.plugin.getEotwHandler().isEndOfTheWorld()){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Kits cannot be applied during EOTW.");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFactionClaimChange(FactionClaimChangeEvent event){
		ClaimableFaction faction;
		if(this.plugin.getEotwHandler().isEndOfTheWorld() && event.getCause() == ClaimChangeCause.CLAIM && (faction = event.getClaimableFaction()) instanceof PlayerFaction){
			event.setCancelled(true);
			event.getSender().sendMessage(ChatColor.RED + "Player based faction land cannot be claimed during EOTW.");
		}
	}

}

