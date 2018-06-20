package com.sergivb01.hcf.listeners;

import com.sergivb01.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CheatBreakerListener implements Listener{

	public CheatBreakerListener(HCF plugin){
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPluginMessageReceived(String channel, Player player, byte[] arg2){
		if(channel.equals("CB|INIT") || channel.equals("CB-Binary")){
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "Thanks for using &3&lCheatBreaker&f, you will now be registered as a &bCheatbreaker User&f."));
		}
	}

}
