package net.veilmc.hcf.listener.fixes;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.utils.ConfigurationService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class AutoRespawnListener implements Listener {
    private HCF plugin;

    public AutoRespawnListener(HCF plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event){
        if(ConfigurationService.KIT_MAP){
            plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                event.getEntity().getPlayer().spigot().respawn();

            }
            }, 0, 10);
        }
    }


}
