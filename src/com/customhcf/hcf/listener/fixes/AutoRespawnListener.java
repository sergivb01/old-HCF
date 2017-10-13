package com.customhcf.hcf.listener.fixes;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.utils.ConfigurationService;
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
            event.getEntity().getPlayer().spigot().respawn();
        }
    }


}
