package net.veilmc.hcf.listener.fixes;

import net.veilmc.hcf.HCF;
<<<<<<< HEAD
=======
import net.veilmc.hcf.HCF;
>>>>>>> origin/new
import org.bukkit.event.Listener;

public class AutoRespawnListener implements Listener{
	private HCF plugin;

	public AutoRespawnListener(HCF plugin){
		this.plugin = plugin;
	}

    /*@EventHandler //Laggy af
    public void onPlayerDie(PlayerDeathEvent event){
        if(ConfigurationService.KIT_MAP){
            plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                event.getEntity().getPlayer().spigot().respawn();

            }
            }, 0, 10);
        }
    }*/


}
