package com.customhcf.hcf.listener;

//
//import com.customhcf.hcf.HCF;
//import com.customhcf.hcf.faction.FactionMember;
//import com.customhcf.hcf.faction.event.*;
//import com.customhcf.hcf.faction.type.Faction;
//import com.customhcf.hcf.faction.type.PlayerFaction;
//
//import com.customhcf.util.JavaUtils;
//import me.joeleoli.construct.ConstructLibrary;
//import me.joeleoli.construct.api.IConstruct;
//import me.joeleoli.construct.util.TaskUtil;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.Statistic;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.EventPriority;
//import org.bukkit.event.Listener;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;
//import org.bukkit.event.entity.PlayerDeathEvent;
//import org.bukkit.event.player.PlayerJoinEvent;
//import org.bukkit.event.player.PlayerKickEvent;
//import org.bukkit.event.player.PlayerMoveEvent;
//import org.bukkit.event.player.PlayerQuitEvent;
//import org.bukkit.plugin.Plugin;
//
//import java.text.DecimalFormat;
//
//public class TabListener implements Listener {
//
//    private IConstruct construct;
//
//    public TabListener(Plugin plugin) {
//        // Define construct before registering event listeners
//        this.construct = ConstructLibrary.getApi();
//
//        plugin.getServer().getPluginManager().registerEvents(this, plugin);
//    }
//
//    @EventHandler
//    public void onJoin(PlayerJoinEvent event) {
//        TaskUtil.runTaskNextTick(() -> {
//            this.construct.createTabList(event.getPlayer());
//            this.initialUpdate(event.getPlayer());
//        });
//    }
//
//    @EventHandler
//    public void onQuit(PlayerQuitEvent event) {
//        this.construct.removeTabList(event.getPlayer());
//    }
//
//    @EventHandler
//    public void onKick(PlayerKickEvent event) {
//        ConstructLibrary.getApi().removeTabList(event.getPlayer());
//    }
//
//    @EventHandler
//    public void onMove(PlayerMoveEvent event) {
//        Player player = event.getPlayer();
//
//        if (!this.construct.hasTabList(player)) {
//            return;
//        }
//        String direction = "";
//        float yaw = event.getPlayer().getLocation().getYaw();
//        if (yaw > 135 || yaw < -135) {
//            direction = "[N]";
//
//        } else if (yaw < -45) {
//            direction = "[E]";
//
//        } else if (yaw > 45) {
//            direction = "[W]";
//
//        } else {
//            direction = "[S]";
//
//        }
//
//        DecimalFormat df = new DecimalFormat("#");
//        String x = df.format(player.getLocation().getBlockX());
//        String z = df.format(player.getLocation().getBlockZ());
//
//        this.construct.setPosition(player, 4, ChatColor.GRAY + x + ", " + z + " " + direction);
//
//        Location location = player.getLocation();
//        Faction factionAt = HCF.getPlugin().getFactionManager().getFactionAt(location);
//        this.construct.setPosition(player, 3, ChatColor.RED + factionAt.getDisplayName(player));
//
//    }
//
//
//
//    @EventHandler
//    public void onEnter(PlayerClaimEnterEvent event) {
//        Player player = event.getPlayer();
//        Location location = player.getLocation();
//        Faction factionAt = HCF.getPlugin().getFactionManager().getFactionAt(location);
//        this.construct.setPosition(player, 3, ChatColor.GRAY + factionAt.getDisplayName(player));
//    }
//
//
////    @EventHandler
////    public void onPlayerJoin(PlayerJoinEvent event) {
////        if (!this.construct.hasTabList(event.getPlayer())) {
////            return;
////        }
////        this.construct.setPosition(event.getPlayer(), 22, Bukkit.getServer().getOnlinePlayers().size() + "/" + Bukkit.getServer().getMaxPlayers());
////    }
//
//
//
//
//
//    public void initialUpdate(Player player) {
//        if (!this.construct.hasTabList(player)) {
//            return;
//        }
//
//        this.construct.setPosition(player, 21, ChatColor.GOLD + "" + ChatColor.BOLD + "VeilMC");
//        this.construct.setPosition(player, 2, ChatColor.YELLOW + "Location:");
//
//        String kills = player.getStatistic(Statistic.PLAYER_KILLS) + "";
//        String deaths = player.getStatistic(Statistic.DEATHS) + "";
//
//        this.construct.setPosition(player, 6, ChatColor.YELLOW + "Faction Info:");
//
//        PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId());
//        if (playerFaction == null) {
//            this.construct.setPosition(player, 8, ChatColor.RED + "You are not");
//            this.construct.setPosition(player, 9, ChatColor.RED + "in a faction");
//        }
//
//
//
//
//
//    }
//}

import com.customhcf.hcf.HCF;
/*import me.joeleoli.construct.ConstructLibrary;
import me.joeleoli.construct.api.IConstruct;
import me.joeleoli.construct.util.TaskUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;*/
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TabListener implements Listener {
    private HCF plugin;
    /*private IConstruct construct;

    public TabListener(HCF plugin){
        this.plugin = plugin;
        this.construct = ConstructLibrary.getApi();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        TaskUtil.runTaskNextTick(() -> {
            this.construct.createTabList(event.getPlayer());
            this.initialUpdate(event.getPlayer());
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        TaskUtil.runTaskNextTick(() -> {
            this.construct.removeTabList(event.getPlayer());
            this.initialUpdate(event.getPlayer());
        });
    }


    private void initialUpdate(Player player) {
        if (!this.construct.hasTabList(player)) {
            return;
        }
        for(int i = 1; i < 60; i++){
            this.construct.setPosition(player, i, "Position #" + i);
        }
    }*/

}