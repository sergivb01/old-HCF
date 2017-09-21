package com.customhcf.hcf.timer.type;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.utils.ConfigurationService;
import com.customhcf.hcf.classes.archer.ArcherClass;
import com.customhcf.hcf.timer.PlayerTimer;
import com.customhcf.hcf.timer.event.TimerExpireEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ArcherTimer
        extends PlayerTimer
        implements Listener
{
    private final HCF plugin;

    public ChatColor getScoreboardPrefix()
    {
        return ConfigurationService.ARCHER_COLOUR;
    }

    public ArcherTimer(HCF plugin)
    {
        //super("Archer Tag", TimeUnit.SECONDS.toMillis(7L));
    	super(ConfigurationService.ARCHER_TIMER, TimeUnit.SECONDS.toMillis(10L));
        this.plugin = plugin;
    }

    public void run() {}

    @EventHandler
    public void onExpire(TimerExpireEvent e)
    {
        if ((e.getUserUUID().isPresent()) &&
                (e.getTimer().equals(this)))
        {
            UUID userUUID = e.getUserUUID().get();
            Player player = Bukkit.getPlayer(userUUID);
            if (player == null) return;

            if(!player.isOnline()) return;

            Bukkit.getPlayer(ArcherClass.tagged.get(userUUID)).sendMessage(ChatColor.GOLD + "Your archer mark on " + ChatColor.RED + player.getName() + ChatColor.GOLD + " has expired.");
            player.sendMessage(ChatColor.GOLD + "You are no longer archer marked.");
            ArcherClass.tagged.remove(player.getUniqueId());
            for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                this.plugin.getScoreboardHandler().getPlayerBoard(players.getUniqueId()).addUpdate(player);
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e)
    {
        if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Player)))
        {
            Player entity = (Player)e.getEntity();
            Entity damager = e.getDamager();
            if (getRemaining(entity) > 0L)
            {
                e.setDamage(e.getDamage() * 1.3D);
            }
        }
        if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Arrow)) && ((((Arrow)e.getDamager()).getShooter() instanceof Player)))
        {
            Player entity = (Player)e.getEntity();
            Entity damager = (Player)((Arrow)e.getDamager()).getShooter();
            if (((damager instanceof Player)) &&
                    (getRemaining(entity) > 0L))
            {
                if (ArcherClass.tagged.get(entity.getUniqueId()).equals(damager.getUniqueId())) {
                    setCooldown(entity, entity.getUniqueId());
                }
                e.setDamage(e.getDamage() * 1.3D);
            }
        }
    }
}