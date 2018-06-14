package net.veilmc.hcf.listeners;

import net.veilmc.base.BasePlugin;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.payloads.Cache;
import net.veilmc.hcf.payloads.types.Payload;
import net.veilmc.hcf.payloads.types.ServerSwitchPayload;
import net.veilmc.hcf.payloads.types.StaffChatPayload;
import net.veilmc.hcf.utils.config.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.stream.Collectors;

public class PayloadsListener implements Listener{

	public PayloadsListener(HCF plugin){
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player.hasPermission("hcf.utils.staff")){
			if(ConfigurationService.REDIS_ENABLED){
				Payload payload = new ServerSwitchPayload(player.getUniqueId(), player.getName(), "joined");
				payload.send();
				Cache.addPayload(payload);
				return;
			}
			Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("hcf.utils.staff")).collect(Collectors.toList())
					.forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&5[Staff] &d" + player.getName() + " &7has joined the server."
					)));
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if(player.hasPermission("hcf.utils.staff")){
			if(ConfigurationService.REDIS_ENABLED){
				Payload payload = new ServerSwitchPayload(player.getUniqueId(), player.getName(), "left");
				payload.send();
				Cache.addPayload(payload);
				return;
			}

			Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("hcf.utils.staff")).collect(Collectors.toList())
					.forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&5[Staff] &d" + player.getName() + " &7has left the server."
					)));
		}
	}


	@EventHandler
	public void onStaffChatChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();

		if(player.hasPermission("hcf.command.staffchat") && BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isInStaffChat() && !event.getMessage().startsWith("!")){
			if(ConfigurationService.REDIS_ENABLED){
				Payload payload = new StaffChatPayload(player.getUniqueId(), player.getName(), event.getMessage());
				payload.send();
				Cache.addPayload(payload);
				event.setCancelled(true);
				return;
			}

			Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("hcf.command.staffchat")).collect(Collectors.toList())
					.forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&5[StaffChat] &d" + player.getName() + "&7: " + event.getMessage()
					)));
		}
	}


}
