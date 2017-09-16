
package com.customhcf.hcf.kothgame.tracker;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.event.FactionRemoveEvent;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.hcf.kothgame.CaptureZone;
import com.customhcf.hcf.kothgame.EventTimer;
import com.customhcf.hcf.kothgame.EventType;
import com.customhcf.hcf.kothgame.faction.ConquestFaction;
import com.customhcf.hcf.kothgame.faction.EventFaction;
import com.customhcf.hcf.kothgame.tracker.EventTracker;
import com.customhcf.hcf.timer.TimerManager;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

public class ConquestTracker implements EventTracker, Listener
{
    public static final long DEFAULT_CAP_MILLIS;
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE;
    private static final Comparator<Map.Entry<PlayerFaction, Integer>> POINTS_COMPARATOR;
    private final Map<PlayerFaction, Integer> factionPointsMap;
    private final HCF plugin;

    public ConquestTracker(final HCF ins) {
        super();
        this.factionPointsMap = Collections.synchronizedMap(new LinkedHashMap<PlayerFaction, Integer>());
        this.plugin = ins;
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRemove(final FactionRemoveEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            synchronized (this.factionPointsMap) {
                this.factionPointsMap.remove(faction);
            }
        }
    }

    public Map<PlayerFaction, Integer> getFactionPointsMap() {
        return (Map<PlayerFaction, Integer>)ImmutableMap.copyOf((Map)this.factionPointsMap);
    }

    public int getPoints(final PlayerFaction faction) {
        synchronized (this.factionPointsMap) {
            return (int)MoreObjects.firstNonNull((Object)this.factionPointsMap.get(faction), (Object)0);
        }
    }

    public int setPoints(final PlayerFaction faction, final int amount) {
        if (amount < 0) {
            return amount;
        }
        synchronized (this.factionPointsMap) {
            this.factionPointsMap.put(faction, amount);
            final List<Map.Entry<PlayerFaction, Integer>> entries = (List<Map.Entry<PlayerFaction, Integer>>)Ordering.from((Comparator)ConquestTracker.POINTS_COMPARATOR).sortedCopy((Iterable)this.factionPointsMap.entrySet());
            this.factionPointsMap.clear();
            for (final Map.Entry<PlayerFaction, Integer> entry : entries) {
                this.factionPointsMap.put(entry.getKey(), entry.getValue());
            }
        }
        return amount;
    }

    public int takePoints(final PlayerFaction faction, final int amount) {
        return this.setPoints(faction, this.getPoints(faction) - amount);
    }

    public int addPoints(final PlayerFaction faction, final int amount) {
        return this.setPoints(faction, this.getPoints(faction) + amount);
    }

    @Override
    public EventType getEventType() {
        return EventType.CONQUEST;
    }

    @Override
    public void tick(final EventTimer eventTimer, final EventFaction eventFaction) {
        final ConquestFaction conquestFaction = (ConquestFaction)eventFaction;
        final List<CaptureZone> captureZones = conquestFaction.getCaptureZones();
        for (final CaptureZone captureZone : captureZones) {
            final Player cappingPlayer = captureZone.getCappingPlayer();
            if (cappingPlayer == null) {
                continue;
            }
            final long remainingMillis = captureZone.getRemainingCaptureMillis();
            if (remainingMillis <= 0L) {
                final UUID uuid = cappingPlayer.getUniqueId();
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
                if (playerFaction != null) {
                    final int newPoints = this.addPoints(playerFaction, 1);
                    if (newPoints >= 300) {
                        synchronized (this.factionPointsMap) {
                            this.factionPointsMap.clear();
                        }
                        this.plugin.getTimerManager().eventTimer.handleWinner(cappingPlayer);
                        return;
                    }
                    captureZone.setRemainingCaptureMillis(captureZone.getDefaultCaptureMillis());
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getName() + "§8] " + ChatColor.LIGHT_PURPLE + playerFaction.getName() + ChatColor.YELLOW + " gained " + 1 + " point for capturing " + captureZone.getDisplayName() + ChatColor.GOLD + ". " + ChatColor.RED + '(' + newPoints + '/' + 300 + ')');
                }
                return;
            }
            final int remainingSeconds = (int)Math.round(remainingMillis / 1000.0);
            if (remainingSeconds % 15 != 0) {
                continue;
            }
            final UUID uuid2 = cappingPlayer.getUniqueId();
            final PlayerFaction playerFaction2 = this.plugin.getFactionManager().getPlayerFaction(uuid2);
            playerFaction2.broadcast(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getName() + "§8] " + ChatColor.GOLD + cappingPlayer.getName() + "'s §eattempting to control " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.RED + '(' + remainingSeconds + "s)");
            cappingPlayer.sendMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getName() + "§8] " + ChatColor.YELLOW + "Attempting to control " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.RED + '(' + remainingSeconds + "s)");
        }
    }

    @Override
    public void onContest(final EventFaction eventFaction, final EventTimer eventTimer) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getName() + "§8] " + ChatColor.GOLD + eventFaction.getName() + " §ecan now be contested.");
    }

    @Override
    public boolean onControlTake(final Player player, final CaptureZone captureZone) {
        if (this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to capture for Conquest.");
            return false;
        }
        return true;
    }

    @Override
    public boolean onControlLoss(final Player player, final CaptureZone captureZone, final EventFaction eventFaction) {
        final long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > ConquestTracker.MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getName() + "§8] " + ChatColor.GOLD + player.getName() + " §ewas knocked off " + captureZone.getDisplayName() + ChatColor.YELLOW + '.');
        }
        return true;
    }

    @Override
    public void stopTiming() {
        synchronized (this.factionPointsMap) {
            this.factionPointsMap.clear();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Faction currentEventFac = this.plugin.getTimerManager().eventTimer.getEventFaction();
        if (currentEventFac instanceof ConquestFaction) {
            final Player player = event.getEntity();
            final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
            if (playerFaction != null) {
                final int oldPoints = this.getPoints(playerFaction);
                if (oldPoints == 0) {
                    return;
                }
                if (this.getPoints(playerFaction) <= 20) {
                    this.setPoints(playerFaction, 0);
                }
                else {
                    this.takePoints(playerFaction, 20);
                }
                event.setDeathMessage(ChatColor.YELLOW + "§8[§6§l" + currentEventFac.getName() + "§8] " + ChatColor.LIGHT_PURPLE + playerFaction.getName() + ChatColor.YELLOW + " lost " + ChatColor.RED + Math.min(20, oldPoints) + ChatColor.YELLOW + " points because " + player.getName() + " died." + ChatColor.RED + " (" + this.getPoints(playerFaction) + '/' + 300 + ')' + ChatColor.YELLOW + '.');
            }
        }
    }

    static {
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(5L);
        DEFAULT_CAP_MILLIS = TimeUnit.SECONDS.toMillis(30L);
        POINTS_COMPARATOR = ((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
    }
}