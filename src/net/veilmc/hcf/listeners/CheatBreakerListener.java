package net.veilmc.hcf.listeners;

import net.veilmc.hcf.HCF;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class CheatBreakerListener implements PluginMessageListener{

	public CheatBreakerListener(HCF plugin){
//		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPluginMessageReceived(String channel, Player player, byte[] arg2){
		if(channel.equals("CB|INIT") || channel.equals("CB-Binary")){
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "Thanks for using &3&lCheatBreaker&f, you will now be registered as a &bCheatbreaker User&f."));
		}
	}

}
