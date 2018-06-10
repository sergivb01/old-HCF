package net.veilmc.hcf.listener;

import net.md_5.bungee.api.ChatColor;
import net.veilmc.base.BasePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class DonorOnlyListener
		implements Listener{
	private static final String DONOR_ONLY_PERMISSION = "hcf.donoronly.bypass";

	@EventHandler
	public void onJoinServerWhileNotDonor(PlayerLoginEvent e){
		if(BasePlugin.getPlugin().getServerHandler().isDonorOnly() && !e.getPlayer().hasPermission(DONOR_ONLY_PERMISSION)){
			e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "The server is currently in Donor-Only mode. \n\n " + ChatColor.YELLOW + "store.veilhcf.us");
		}
	}
}

