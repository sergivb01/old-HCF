
package com.customhcf.hcf.timer.type;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.ConfigurationService;
import com.customhcf.hcf.combatlog.CombatLogListener;
import com.customhcf.hcf.faction.LandMap;
import com.customhcf.hcf.timer.PlayerTimer;
import com.customhcf.hcf.timer.TimerRunnable;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class StuckTimer
extends PlayerTimer
implements Listener {
    private final ConcurrentMap<Object, Object> startedLocations;

    public StuckTimer() {
        super(ConfigurationService.STUCK_TIMER, TimeUnit.MINUTES.toMillis(2) + TimeUnit.SECONDS.toMillis(45), false);
        this.startedLocations = CacheBuilder.newBuilder().expireAfterWrite(this.defaultCooldown + 5000, TimeUnit.MILLISECONDS).build().asMap();
    }

    @Override
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.STUCK_COLOUR;
    }

    @Override
    public TimerRunnable clearCooldown(UUID uuid) {
        TimerRunnable runnable = super.clearCooldown(uuid);
        if (runnable != null) {
            this.startedLocations.remove(uuid);
            return runnable;
        }
        return null;
    }

    @Override
    public boolean setCooldown(@Nullable Player player, UUID playerUUID, long millis, boolean force) {
        if (player != null && super.setCooldown(player, playerUUID, millis, force)) {
            this.startedLocations.put(playerUUID, (Object)player.getLocation());
            return true;
        }
        return false;
    }

    private void checkMovement(Player player, Location from, Location to) {
        UUID uuid = player.getUniqueId();
        if (this.getRemaining(uuid) > 0) {
            if (from == null) {
                this.clearCooldown(uuid);
                return;
            }
            int xDiff = Math.abs(from.getBlockX() - to.getBlockX());
            int yDiff = Math.abs(from.getBlockY() - to.getBlockY());
            int zDiff = Math.abs(from.getBlockZ() - to.getBlockZ());
            if (xDiff > 5 || yDiff > 5 || zDiff > 5) {
                this.clearCooldown(uuid);
                player.sendMessage((Object)ChatColor.RED + "You moved more than " + (Object)ChatColor.BOLD + 5 + (Object)ChatColor.RED + " blocks. " + this.getDisplayName() + (Object)ChatColor.RED + " timer ended.");
            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (this.getRemaining(uuid) > 0) {
            Location from = (Location)this.startedLocations.get(uuid);
            this.checkMovement(player, from, event.getTo());
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (this.getRemaining(uuid) > 0) {
            Location from = (Location)this.startedLocations.get(uuid);
            this.checkMovement(player, from, event.getTo());
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (this.getRemaining(event.getPlayer().getUniqueId()) > 0) {
            this.clearCooldown(uuid);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (this.getRemaining(event.getPlayer().getUniqueId()) > 0) {
            this.clearCooldown(uuid);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        Player player;
        Entity entity = event.getEntity();
        if (entity instanceof Player && this.getRemaining(player = (Player)entity) > 0) {
            player.sendMessage((Object)ChatColor.RED + "You were damaged, " + this.getDisplayName() + (Object)ChatColor.RED + " timer ended.");
            this.clearCooldown(player);
        }
    }

    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer((UUID)userUUID);
        if (player == null) {
            return;
        }
        Location nearest = LandMap.getNearestSafePosition(player, player.getLocation(), 124);
        if (nearest == null) {
            CombatLogListener.safelyDisconnect(player, (Object)ChatColor.RED + "Unable to find a safe location, you have been safely logged out.");
            player.sendMessage((Object)ChatColor.RED + "No safe-location found.");
            return;
        }
        if (player.teleport(nearest, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
            player.sendMessage((Object)ChatColor.YELLOW + this.getDisplayName() + (Object)ChatColor.YELLOW + " timer has teleported you to the nearest safe area.");
        }
    }

    public void run(Player player) {
        long remainingMillis = this.getRemaining(player);
        if (remainingMillis > 0) {
            player.sendMessage(this.getDisplayName() + (Object)ChatColor.BLUE + " timer is teleporting you in " + (Object)ChatColor.BOLD + HCF.getRemaining(remainingMillis, true, false) + (Object)ChatColor.BLUE + '.');
        }
    }
}

