package net.veilmc.hcf.tab;

<<<<<<< HEAD
import net.veilmc.hcf.HCF;
=======
import net.veilmc.base.event.PlayerMoveByBlockEvent;
import net.veilmc.hcf.HCF;
import org.bukkit.ChatColor;
>>>>>>> origin/new
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
<<<<<<< HEAD
=======
import org.bukkit.event.player.PlayerMoveEvent;
>>>>>>> origin/new
import org.bukkit.event.player.PlayerQuitEvent;

public class TabListener implements Listener{
	private static HCF plugin;
	private static TUtils t;

	public TabListener(HCF plugin){
		Tab.setPlugin(plugin);
		this.t = new TUtils(plugin);
	}

	/*
		TODO:
			* Add listeners
			* Create player tab
			* Check different tab styles
			* Disable tab thing
			* Caching system (?)
	 */

	private static void update18(Player player){
		//61-80
		Tab tab = Tab.getByPlayer(player);
		if(tab.isClient18()){
			tab.setSlot(10, "&7For an optimal experience");
			tab.setSlot(11, "&fwe recommend the use of &4&l1.7");
		}
	}



	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
<<<<<<< HEAD

		Tab.createTabList(player).setPlayerListHeaderFooter("My cool header", "My boring footer");

		for(int i = 1; i <= 80; i++){
			Tab.getByPlayer(player).setSlot(i, "Position " + i);
		}
	}

=======
		if(Tab.hasTabList(player)){
			Tab.deleteTabList(player);
		}
		Tab.createTabList(player);
		Tab.createTabList(player).setPlayerListHeaderFooter("Header", "Footer");
		//for(int i = 1; i <= 80; i++){
		//	Tab.getByPlayer(player).setSlot(i, "Position " + i);
		//}
	}




>>>>>>> origin/new
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Tab.deleteTabList(event.getPlayer());
	}


}
