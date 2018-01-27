package net.veilmc.hcf.listener.fixes;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.utils.ConfigurationService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class AutoRespawnListener implements Listener {
    private HCF plugin;

    public AutoRespawnListener(HCF plugin){
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDie(PlayerDeathEvent event){
        if(ConfigurationService.KIT_MAP){
            event.getEntity().getPlayer().spigot().respawn();
        }
    }


}
