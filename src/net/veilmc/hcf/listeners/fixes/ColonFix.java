package net.veilmc.hcf.listeners.fixes;

import net.veilmc.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ColonFix implements Listener{

	public ColonFix(HCF plugin){
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerColonCommand(PlayerCommandPreprocessEvent e){
		if((e.getMessage().startsWith("/minecraft:")) || (e.getMessage().startsWith("bukkit:"))){
			e.setCancelled(true);
		}else if(((e.getMessage().startsWith("/ver")) || (e.getMessage().startsWith("/about"))) && (!e.getPlayer().isOp())){
			e.setCancelled(true);
		}
	}
}
