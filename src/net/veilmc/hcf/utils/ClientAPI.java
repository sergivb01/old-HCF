package net.veilmc.hcf.utils;

import net.veilmc.hcf.HCF;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ClientAPI implements Listener{

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		HCF.cbUser.remove(player.getUniqueId());
		HCF.blUser.remove(player.getUniqueId());

	}
}

