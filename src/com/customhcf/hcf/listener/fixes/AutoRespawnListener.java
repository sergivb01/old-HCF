package com.customhcf.hcf.listener.fixes;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.utils.ConfigurationService;
import me.sergivb01.giraffe.utils.TaskUtil;
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
            TaskUtil.runTaskNextTick(()-> event.getEntity().spigot().respawn());
        }
    }


}
