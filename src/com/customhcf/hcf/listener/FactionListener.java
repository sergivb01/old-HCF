package com.customhcf.hcf.listener;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.utils.ConfigurationService;
import com.customhcf.hcf.faction.claim.Claim;
import com.customhcf.hcf.faction.event.*;
import com.customhcf.hcf.faction.struct.RegenStatus;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.hcf.kothgame.faction.KothFaction;
import com.google.common.base.Optional;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FactionListener
implements Listener {
    private static final long FACTION_JOIN_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(30);
    private static final String FACTION_JOIN_WAIT_WORDS = DurationFormatUtils.formatDurationWords((long)FACTION_JOIN_WAIT_MILLIS, (boolean)true, (boolean)true);
    private static final String LAND_CHANGED_META_KEY = "landChangedMessage";
    private static final long LAND_CHANGE_MSG_THRESHOLD = 225;
    private final HCF plugin;

    public FactionListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (e.getLine(0).equalsIgnoreCase("[KOTH]") && this.plugin.getFactionManager().getFaction(e.getLine(1)) instanceof KothFaction) {
            KothFaction kothFaction = (KothFaction)this.plugin.getFactionManager().getFaction(e.getLine(1));
            e.setLine(0, ChatColor.LIGHT_PURPLE + "KOTH");
            e.setLine(1, ChatColor.GOLD + kothFaction.getName());
            for (Claim claim : kothFaction.getClaims()) {
                Location location = claim.getCenter();
                e.setLine(2, ChatColor.RED.toString() + location.getBlockX() + " | " + location.getBlockZ());
            }
            e.setLine(3, ChatColor.RED + kothFaction.getCaptureZone().getDefaultCaptureWords());
        }
    }



    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onFactionCreate(FactionCreateEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            CommandSender sender = event.getSender();
            Bukkit.broadcastMessage(net.md_5.bungee.api.ChatColor.RED + "" + event.getFaction().getName() + net.md_5.bungee.api.ChatColor.YELLOW + " has been " + ChatColor.GREEN + "created " + ChatColor.YELLOW + "by " + net.md_5.bungee.api.ChatColor.WHITE + "" + sender.getName() + ChatColor.YELLOW + ".");
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onFactionRemove(FactionRemoveEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            CommandSender sender = event.getSender();
            Bukkit.broadcastMessage(net.md_5.bungee.api.ChatColor.RED + "" + event.getFaction().getName() + net.md_5.bungee.api.ChatColor.YELLOW + " has been " + ChatColor.RED + "disbanded " + ChatColor.YELLOW + "by " + net.md_5.bungee.api.ChatColor.WHITE + sender.getName() + ChatColor.YELLOW + ".");
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onFactionRename(FactionRenameEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            Bukkit.broadcastMessage(net.md_5.bungee.api.ChatColor.RED + event.getOriginalName() + net.md_5.bungee.api.ChatColor.YELLOW + " has been" + ChatColor.GREEN + " renamed " + ChatColor.YELLOW + "to " + net.md_5.bungee.api.ChatColor.RED + "" + event.getNewName() + net.md_5.bungee.api.ChatColor.YELLOW + " by " + net.md_5.bungee.api.ChatColor.WHITE + event.getSender().getName() + ChatColor.YELLOW + ".");
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onFactionRenameMonitor(FactionRenameEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof KothFaction) {
            ((KothFaction)faction).getCaptureZone().setName(event.getNewName());
        }
    }


    private long getLastLandChangedMeta(Player player) {
        List<MetadataValue> value = player.getMetadata(LAND_CHANGED_META_KEY);

        long millis = System.currentTimeMillis();
        long remaining = value == null || value.isEmpty() ? 0L : value.get(0).asLong() - millis;

        if (remaining <= 0L) { // update the metadata.
            player.setMetadata(LAND_CHANGED_META_KEY, new FixedMetadataValue(plugin, millis + LAND_CHANGE_MSG_THRESHOLD));
        }

        return remaining;
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onCaptureZoneEnter(CaptureZoneEnterEvent event) {
        Player player = event.getPlayer();
        if (this.getLastLandChangedMeta(player) <= 0 && this.plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()) {
            player.sendMessage(net.md_5.bungee.api.ChatColor.YELLOW + "Now entering capture zone: " + event.getCaptureZone().getDisplayName() + net.md_5.bungee.api.ChatColor.YELLOW + '(' + event.getFaction().getName() + net.md_5.bungee.api.ChatColor.YELLOW + ')');
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onCaptureZoneLeave(CaptureZoneLeaveEvent event) {
        Player player = event.getPlayer();
        if (this.getLastLandChangedMeta(player) <= 0 && this.plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()) {
            player.sendMessage(net.md_5.bungee.api.ChatColor.YELLOW + "Now leaving capture zone: " + event.getCaptureZone().getDisplayName() + net.md_5.bungee.api.ChatColor.YELLOW + '(' + event.getFaction().getName() + net.md_5.bungee.api.ChatColor.YELLOW + ')');
        }
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    private void onPlayerClaimEnter(PlayerClaimEnterEvent event) {
        Player player;
        Faction toFaction = event.getToFaction();
        if (toFaction.isSafezone()) {
            player = event.getPlayer();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setFireTicks(0);
            player.setSaturation(4.0f);
        }
        if (this.getLastLandChangedMeta(player = event.getPlayer()) <= 0) {
            if(ConfigurationService.KIT_MAP) {
                Faction fromFaction = event.getFromFaction();
                player.sendMessage(ChatColor.YELLOW + "Leaving: " + fromFaction.getDisplayName(player) + ChatColor.YELLOW + ", Entering: " + toFaction.getDisplayName(player));
            } else {
                Faction fromFaction = event.getFromFaction();
                player.sendMessage(net.md_5.bungee.api.ChatColor.YELLOW + "Now leaving: " + fromFaction.getDisplayName(player) + net.md_5.bungee.api.ChatColor.YELLOW + " (" + (fromFaction.isDeathban() ? new StringBuilder().append(net.md_5.bungee.api.ChatColor.RED).append("Deathban").toString() : new StringBuilder().append(net.md_5.bungee.api.ChatColor.GREEN).append("Non-Deathban").toString()) + net.md_5.bungee.api.ChatColor.YELLOW + ')');
                player.sendMessage(net.md_5.bungee.api.ChatColor.YELLOW + "Now entering: " + toFaction.getDisplayName(player) + net.md_5.bungee.api.ChatColor.YELLOW + " (" + (toFaction.isDeathban() ? new StringBuilder().append(net.md_5.bungee.api.ChatColor.RED).append("Deathban").toString() : new StringBuilder().append(net.md_5.bungee.api.ChatColor.GREEN).append("Non-Deathban").toString()) + net.md_5.bungee.api.ChatColor.YELLOW + ')');

            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
        Optional<Player> optionalPlayer = event.getPlayer();
        if (optionalPlayer.isPresent()) {
            this.plugin.getUserManager().getUser(optionalPlayer.get().getUniqueId()).setLastFactionLeaveMillis(System.currentTimeMillis());
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onPlayerPreFactionJoin(PlayerJoinFactionEvent event) {
        Faction faction = event.getFaction();
        Optional<Player> optionalPlayer = event.getPlayer();
        if (faction instanceof PlayerFaction && optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            PlayerFaction playerFaction = (PlayerFaction)faction;
            if (!this.plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.getRegenStatus() == RegenStatus.PAUSED) {
                event.setCancelled(true);
                player.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You cannot join factions that are not regenerating DTR.");
                return;
            }
            long difference = this.plugin.getUserManager().getUser(player.getUniqueId()).getLastFactionLeaveMillis() - System.currentTimeMillis() + FACTION_JOIN_WAIT_MILLIS;
            if (difference > 0 && !player.hasPermission("hcf.faction.argument.staff.forcejoin")) {
                event.setCancelled(true);
                player.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You cannot join factions after just leaving within " + FACTION_JOIN_WAIT_WORDS + ". " + "You have to wait another " + DurationFormatUtils.formatDurationWords(difference, true, true) + '.');
            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onFactionLeave(PlayerLeaveFactionEvent event) {
        Optional<Player> optional;
        Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction && (optional = event.getPlayer()).isPresent()) {
            Player player = optional.get();
            if (this.plugin.getFactionManager().getFactionAt(player.getLocation()).equals(faction)) {
                event.setCancelled(true);
                player.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You cannot leave your faction whilst you remain in its' territory.");
            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction != null) {
            playerFaction.printDetails(player);
            playerFaction.broadcast(net.md_5.bungee.api.ChatColor.YELLOW + "Member Online: " + net.md_5.bungee.api.ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + net.md_5.bungee.api.ChatColor.GOLD + '.', player.getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction != null) {
            playerFaction.broadcast(net.md_5.bungee.api.ChatColor.YELLOW + "Member Offline: " + net.md_5.bungee.api.ChatColor.RED + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + net.md_5.bungee.api.ChatColor.GOLD + '.');
        }
    }
}