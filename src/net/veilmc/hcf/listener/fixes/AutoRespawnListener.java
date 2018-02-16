package net.veilmc.hcf.listener.fixes;

import net.veilmc.hcf.HCF;
import org.bukkit.event.Listener;

public class AutoRespawnListener implements Listener {
    private HCF plugin;

    public AutoRespawnListener(HCF plugin){
        this.plugin = plugin;
    }

    /*@EventHandler //Laggy af
    public void onPlayerDie(PlayerDeathEvent event){
        if(ConfigurationService.KIT_MAP){
            event.getEntity().getPlayer().spigot().respawn();
        }
    }*/


}
