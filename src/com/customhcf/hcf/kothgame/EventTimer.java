
package com.customhcf.hcf.kothgame;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.ConfigurationService;
import com.customhcf.hcf.Utils.DateTimeFormats;
import com.customhcf.hcf.crate.Key;
import com.customhcf.hcf.faction.event.CaptureZoneEnterEvent;
import com.customhcf.hcf.faction.event.CaptureZoneLeaveEvent;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.hcf.kothgame.faction.ConquestFaction;
import com.customhcf.hcf.kothgame.faction.EventFaction;
import com.customhcf.hcf.kothgame.faction.KothFaction;
import com.customhcf.hcf.listener.EventSignListener;
import com.customhcf.hcf.palace.PalaceFaction;
import com.customhcf.hcf.timer.GlobalTimer;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

public class EventTimer
extends GlobalTimer
implements Listener {
    private static final long RESCHEDULE_FREEZE_MILLIS = TimeUnit.SECONDS.toMillis(15);
    private static final String RESCHEDULE_FREEZE_WORDS = DurationFormatUtils.formatDurationWords((long) RESCHEDULE_FREEZE_MILLIS, (boolean) true, (boolean) true);
    private final HCF plugin;
    private long startStamp;
    private long lastContestedEventMillis;
    private EventFaction eventFaction;

    public EventTimer(final HCF plugin) {
        super(ConfigurationService.EVENT_TIMER, 0);
        this.plugin = plugin;
        new BukkitRunnable() {

            public void run() {
                LocalDateTime scheduledTime;
                Faction faction;
                Map.Entry<LocalDateTime, String> entry;
                if (EventTimer.this.eventFaction != null) {
                    EventTimer.this.eventFaction.getEventType().getEventTracker().tick(EventTimer.this, EventTimer.this.eventFaction);
                    return;
                }
                LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
                int day = now.getDayOfYear();
                int hour = now.getHour();
                int minute = now.getMinute();
                Iterator<Map.Entry<LocalDateTime, String>> iterator = plugin.eventScheduler.getScheduleMap().entrySet().iterator();
                while (!(!iterator.hasNext() || day == (scheduledTime = (entry = iterator.next()).getKey()).getDayOfYear() && hour == scheduledTime.getHour() && minute == scheduledTime.getMinute() && (faction = plugin.getFactionManager().getFaction(entry.getValue())) instanceof EventFaction && EventTimer.this.tryContesting((EventFaction) faction, (CommandSender) Bukkit.getConsoleSender()))) {
                }
            }
        }.runTaskTimer((Plugin) plugin, 20, 20);
    }

    public EventFaction getEventFaction() {
        return this.eventFaction;
    }

    @Override
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.EVENT_COLOUR;
    }

    @Override
    public String getName() {
        return this.eventFaction == null ? "Event" : this.eventFaction.getName();
    }

    @Override
    public boolean clearCooldown() {
        boolean result = super.clearCooldown();
        if (this.eventFaction != null) {
            for (CaptureZone captureZone : this.eventFaction.getCaptureZones()) {
                captureZone.setCappingPlayer(null);
            }
            this.eventFaction.setDeathban(true);
            this.eventFaction.getEventType().getEventTracker().stopTiming();
            this.eventFaction = null;
            this.eventFaction = null;
            this.startStamp = -1;
            result = true;
        }
        return result;
    }

    @EventHandler
    public void onDecay(LeavesDecayEvent e) {
        if (this.plugin.getFactionManager().getFactionAt(e.getBlock()) != null) {
            e.setCancelled(true);
        }
    }

    @Override
    public long getRemaining() {
        if (this.eventFaction == null) {
            return 0L;
        }
        if (this.eventFaction instanceof KothFaction) {
            return ((KothFaction) this.eventFaction).getCaptureZone().getRemainingCaptureMillis();
        }
        return super.getRemaining();
    }

    @Override
    public long getRemaining1() {
        if (this.eventFaction == null) {
            return 0L;
        }
        if (this.eventFaction instanceof PalaceFaction) {
            return ((PalaceFaction) this.eventFaction).getCaptureZone().getRemainingCaptureMillis();
        }
        return super.getRemaining();
    }

    public void handleWinner(final Player winner) {
        if (this.eventFaction == null) {
            return;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner);
        Bukkit.broadcastMessage(ConfigurationService.BASECOLOUR + "[" + this.eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + ((playerFaction == null) ? winner.getName() : playerFaction.getName()) + ChatColor.GOLD + " has captured " + ChatColor.LIGHT_PURPLE + this.eventFaction.getName() + ChatColor.GOLD + " after " + ConfigurationService.BASECOLOUR + DurationFormatUtils.formatDurationWords(this.getUptime(), true, true) + ChatColor.GOLD + " of up-time" + ChatColor.GOLD + '.');
        final World world = winner.getWorld();
        final Location location = winner.getLocation();
        final Key key = this.plugin.getKeyManager().getKey(ChatColor.stripColor(this.eventFaction.getEventType().getDisplayName()));
//        Preconditions.checkNotNull((Object)key, (Object)"Key on: EventTime error.");
//        final ItemStack stack = key.getItemStack().clone();
//        final Map<Integer, ItemStack> excess = (Map<Integer, ItemStack>)winner.getInventory().addItem(new ItemStack[] { stack, EventSignListener.getEventSign(this.eventFaction.getName(), winner.getName()) });
//        for (final ItemStack entry : excess.values()) {
//            world.dropItemNaturally(location, entry);

//        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "event cancel");
        if(key.getName().toString().equalsIgnoreCase("koth")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key " + winner.getName() + " " + key.getName() + " 5");
            return;
        }
        if(key.getName().toString().equalsIgnoreCase("conquest")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key " + winner.getName() + " " + key.getName() + " 8");
            return;
        } else {
            winner.sendMessage(ChatColor.RED + "An error occured fetching your " + key.getName() + " keys. Please contact staff..");
            return;
        }
//        this.clearCooldown();
    }


    public void handleWinner1(final Player winner) {
        if (this.eventFaction == null) {
            return;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner);
        //Bukkit.broadcastMessage(ConfigurationService.KOTH_PLAYER_CAP.replace("%player%", winner.getName()).replace("%koth%", this.eventFaction.getEventType().getDisplayName()));
        Bukkit.broadcastMessage(ConfigurationService.BASECOLOUR + "[" + this.eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + ((playerFaction == null) ? winner.getName() : playerFaction.getName()) + ChatColor.GOLD + " has captured " + ChatColor.LIGHT_PURPLE + this.eventFaction.getName() + ChatColor.GOLD + '.');
        final World world = winner.getWorld();
        final Location location = winner.getLocation();
        this.clearCooldown();

    }

    public boolean tryContesting(final EventFaction eventFaction, final CommandSender sender) {
        if (this.eventFaction != null) {
            sender.sendMessage(ChatColor.RED + "There is already an active event, use /event cancel to end it.");
            return false;
        }
        if (eventFaction instanceof KothFaction) {
            final KothFaction kothFaction = (KothFaction) eventFaction;
            if (kothFaction.getCaptureZone() == null) {
                sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as its' capture zone is not set.");
                return false;
            }
        }
        if (eventFaction instanceof PalaceFaction) {
            final PalaceFaction palaceFaction = (PalaceFaction) eventFaction;
            if (palaceFaction.getCaptureZone() == null) {
                sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as its' capture zone is not set.");
                return false;
            }
        } else if (eventFaction instanceof ConquestFaction) {
            final ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
            final Collection<ConquestFaction.ConquestZone> zones = conquestFaction.getConquestZones();
            for (final ConquestFaction.ConquestZone zone : ConquestFaction.ConquestZone.values()) {
                if (!zones.contains(zone)) {
                    sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as capture zone '" + zone.getDisplayName() + ChatColor.RED + "' is not set.");
                    return false;
                }
            }
        }
        final long millis = System.currentTimeMillis();
        if (this.lastContestedEventMillis + EventTimer.RESCHEDULE_FREEZE_MILLIS - millis > 0L) {
            sender.sendMessage(ChatColor.RED + "Cannot reschedule events within " + EventTimer.RESCHEDULE_FREEZE_WORDS + '.');
            return false;
        }
        this.lastContestedEventMillis = millis;
        this.startStamp = millis;
        this.eventFaction = eventFaction;
        eventFaction.getEventType().getEventTracker().onContest(eventFaction, this);
        if (eventFaction instanceof ConquestFaction) {
            this.setRemaining(1000L, true);
            this.setPaused(true);
        }
        final Collection<CaptureZone> captureZones = eventFaction.getCaptureZones();
        for (final CaptureZone captureZone : captureZones) {
            if (captureZone.isActive()) {
                final Player player = (Player) Iterables.getFirst((Iterable) captureZone.getCuboid().getPlayers(), (Object) null);
                if (player == null) {
                    continue;
                }
                if (!eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)) {
                    continue;
                }
                captureZone.setCappingPlayer(player);
            }
        }
        eventFaction.setDeathban(true);
        return true;
    }

    public long getUptime() {
        return System.currentTimeMillis() - this.startStamp;
    }

    public long getStartStamp() {
        return this.startStamp;
    }

    private void handleDisconnect(final Player player) {
        Preconditions.checkNotNull((Object) player);
        if (this.eventFaction == null) {
            return;
        }
        final Collection<CaptureZone> captureZones = this.eventFaction.getCaptureZones();
        for (final CaptureZone captureZone : captureZones) {
            if (Objects.equal((Object) captureZone.getCappingPlayer(), (Object) player)) {
                this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction);
                captureZone.setCappingPlayer(null);
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEnderpearl(final ProjectileLaunchEvent event){
        ProjectileSource source;
        EnderPearl enderPearl;
        Projectile projectile = event.getEntity();
        if (projectile instanceof EnderPearl && (source = (enderPearl = (EnderPearl)projectile).getShooter()) instanceof Player) {
            this.handleDisconnect((Player)projectile.getShooter());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        this.handleDisconnect(event.getEntity());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        this.handleDisconnect(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        this.handleDisconnect(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneEnter(final CaptureZoneEnterEvent event) {
        if (this.eventFaction == null) {
            return;
        }
        final CaptureZone captureZone = event.getCaptureZone();
        if (!this.eventFaction.getCaptureZones().contains(captureZone)) {
            return;
        }
        final Player player = event.getPlayer();
        if (captureZone.getCappingPlayer() == null && this.eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)) {
            captureZone.setCappingPlayer(player);
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneLeave(final CaptureZoneLeaveEvent event) {
        if (Objects.equal((Object) event.getFaction(), (Object) this.eventFaction)) {
            final Player player = event.getPlayer();
            final CaptureZone captureZone = event.getCaptureZone();
            if (Objects.equal((Object) player, (Object) captureZone.getCappingPlayer()) && this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction)) {
                captureZone.setCappingPlayer(null);
                for (final Player target : captureZone.getCuboid().getPlayers()) {
                    if (target != null && !target.equals(player) && this.eventFaction.getEventType().getEventTracker().onControlTake(target, captureZone)) {
                        captureZone.setCappingPlayer(target);
                        break;
                    }
                }
            }
        }
    }
}


