package net.veilmc.hcf.listener;

import net.veilmc.hcf.HCF;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class MotdListener implements Listener {
    private HCF plugin;

    public MotdListener(HCF plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event){
        event.setMotd(
                ChatColor.YELLOW + "Next Koth:" + "\n" +
                        ChatColor.translateAlternateColorCodes('&', ((this.plugin.NEXT_KOTH > 0) ? "&9&l" + this.plugin.getNextGame() + " &7(" + this.plugin.getKothRemaining() + ")" : "&7None Scheduled")));
    }



}
