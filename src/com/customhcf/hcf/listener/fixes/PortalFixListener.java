package com.customhcf.hcf.listener.fixes;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PortalFixListener
        implements Listener {
    @EventHandler
    public void onClick(PlayerInteractEvent e)
    {
        if (e.getClickedBlock() == null) {
            return;
        }
        if (e.getClickedBlock().getType() == Material.PORTAL) {
            e.getClickedBlock().setType(Material.AIR);
        }
    }
}
