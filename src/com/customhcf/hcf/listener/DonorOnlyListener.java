
package com.customhcf.hcf.listener;

import com.customhcf.base.ServerHandler;
import com.customhcf.hcf.HCF;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class DonorOnlyListener
implements Listener {
    private static final String DONOR_ONLY_PERMISSION = "hcf.donoronly.bypass";

    @EventHandler
    public void onJoinServerWhileNotDonor(PlayerLoginEvent e) {
        if (HCF.getPlugin().getServerHandler().isDonorOnly() && !e.getPlayer().hasPermission(DONOR_ONLY_PERMISSION)) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, (Object)ChatColor.RED + "The server is currently in Donor-Only mode. \n\n " + (Object)ChatColor.YELLOW + "store.veilhcf.us");
            return;
        }
    }
}

