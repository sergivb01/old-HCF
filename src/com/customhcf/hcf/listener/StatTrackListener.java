package com.customhcf.hcf.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.user.FactionUser;

public class StatTrackListener implements Listener {


    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            Player player = e.getEntity();
            FactionUser hcf = HCF.getPlugin().getUserManager().getUser(player.getUniqueId());
            if (hcf.getKills() > 0) {
                hcf.setKills(0);
            }
        }
    }
}
