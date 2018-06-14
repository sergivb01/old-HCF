package net.veilmc.hcf.listeners;

import net.veilmc.base.BasePlugin;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.payloads.Cache;
import net.veilmc.hcf.payloads.types.Payload;
import net.veilmc.hcf.payloads.types.ServerSwitchPayload;
import net.veilmc.hcf.payloads.types.StaffChatPayload;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PayloadsListener implements Listener{

	public PayloadsListener(HCF plugin){
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player.hasPermission("hcf.utils.staff")){
			Payload payload = new ServerSwitchPayload(player.getUniqueId(), player.getName(), "joined");
			payload.send();
			Cache.addPayload(payload);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if(player.hasPermission("hcf.utils.staff")){
			Payload payload = new ServerSwitchPayload(player.getUniqueId(), player.getName(), "left");
			payload.send();
			Cache.addPayload(payload);
		}
	}


	@EventHandler
	public void onStaffChatChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();

		if(player.hasPermission("hcf.command.staffchat") && BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isInStaffChat() && !event.getMessage().startsWith("!")){
			Payload payload = new StaffChatPayload(player.getUniqueId(), player.getName(), event.getMessage());
			payload.send();
			Cache.addPayload(payload);
			event.setCancelled(true);
		}
	}


}
