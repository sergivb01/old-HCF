package com.sergivb01.hcf.events;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.commands.crate.Key;
import com.sergivb01.hcf.events.faction.ConquestFaction;
import com.sergivb01.hcf.events.faction.EventFaction;
import com.sergivb01.hcf.events.faction.KothFaction;
import com.sergivb01.hcf.events.palace.PalaceFaction;
import com.sergivb01.hcf.faction.event.CaptureZoneEnterEvent;
import com.sergivb01.hcf.faction.event.CaptureZoneLeaveEvent;
import com.sergivb01.hcf.faction.type.Faction;
import com.sergivb01.hcf.faction.type.PlayerFaction;
import com.sergivb01.hcf.timer.GlobalTimer;
import com.sergivb01.hcf.utils.DateTimeFormats;
import com.sergivb01.hcf.utils.config.ConfigurationService;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EventTimer
		extends GlobalTimer
		implements Listener{
	private static final long RESCHEDULE_FREEZE_MILLIS = TimeUnit.SECONDS.toMillis(15);
	private static final String RESCHEDULE_FREEZE_WORDS = DurationFormatUtils.formatDurationWords((long) RESCHEDULE_FREEZE_MILLIS, (boolean) true, (boolean) true);
	private final HCF plugin;
	private long startStamp;
	private long lastContestedEventMillis;
	private EventFaction eventFaction;

	public EventTimer(final HCF plugin){
		super(ConfigurationService.EVENT_TIMER, 0);
		this.plugin = plugin;
		new BukkitRunnable(){

			public void run(){
				LocalDateTime scheduledTime;
				Faction faction;
				Map.Entry<LocalDateTime, String> entry;
				if(EventTimer.this.eventFaction != null){
					EventTimer.this.eventFaction.getEventType().getEventTracker().tick(EventTimer.this, EventTimer.this.eventFaction);
					return;
				}
				LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
				int day = now.getDayOfYear();
				int hour = now.getHour();
				int minute = now.getMinute();
				//Iterator<Map.Entry<LocalDateTime, String>> iterator = plugin.eventScheduler.getScheduleMap().entrySet().iterator();
				//while(!(!iterator.hasNext() || day == (scheduledTime = (entry = iterator.next()).getKey()).getDayOfYear() && hour == scheduledTime.getHour() && minute == scheduledTime.getMinute() && (faction = plugin.getFactionManager().getFaction(entry.getValue())) instanceof EventFaction && EventTimer.this.tryContesting((EventFaction) faction, Bukkit.getConsoleSender()))){

				//	}
			}
		}.runTaskTimer(plugin, 20, 20);
	}

	public EventFaction getEventFaction(){
		return this.eventFaction;
	}

	@Override
	public ChatColor getScoreboardPrefix(){
		return ConfigurationService.EVENT_COLOUR;
	}

	@Override
	public String getName(){
		return this.eventFaction == null ? "Event" : ChatColor.BOLD.toString() + this.eventFaction.getName();
	}

	@Override
	public boolean clearCooldown(){
		boolean result = super.clearCooldown();
		if(this.eventFaction != null){
			for(CaptureZone captureZone : this.eventFaction.getCaptureZones()){
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
	public void onDecay(LeavesDecayEvent e){
		if(this.plugin.getFactionManager().getFactionAt(e.getBlock()) != null){
			e.setCancelled(true);
		}
	}

	@Override
	public long getRemaining(){
		if(this.eventFaction == null){
			return 0L;
		}
		if(this.eventFaction instanceof KothFaction){
			return ((KothFaction) this.eventFaction).getCaptureZone().getRemainingCaptureMillis();
		}
		return super.getRemaining();
	}

	@Override
	public long getRemaining1(){
		if(this.eventFaction == null){
			return 0L;
		}
		if(this.eventFaction instanceof PalaceFaction){
			return ((PalaceFaction) this.eventFaction).getCaptureZone().getRemainingCaptureMillis();
		}
		return super.getRemaining();
	}

	public void handleWinner(final Player winner){
		if(this.eventFaction == null){
			return;
		}
		final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner.getUniqueId());
		Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + this.eventFaction.getEventType().getDisplayName() + ChatColor.GRAY + "] " + ChatColor.GOLD + ((playerFaction == null) ? winner.getName() : playerFaction.getName() + ChatColor.GRAY + " [" + winner.getName() + "]") + ChatColor.YELLOW + " has captured " + ChatColor.LIGHT_PURPLE + this.eventFaction.getName() + ChatColor.YELLOW + " after " + DurationFormatUtils.formatDurationWords(this.getUptime(), true, true) + ChatColor.YELLOW + " of up-time");
		final World world = winner.getWorld();
		final Location location = winner.getLocation();
		final Key key = this.plugin.getKeyManager().getKey(ChatColor.stripColor(this.eventFaction.getEventType().getDisplayName()));


		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "event cancel");
		if(key.getName().equalsIgnoreCase("koth")){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key " + winner.getName() + " " + key.getName() + " 5");
		}
		if(key.getName().equalsIgnoreCase("conquest")){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key " + winner.getName() + " " + key.getName() + " 8");
		}

	}


	public void handleWinner1(final Player winner){
		if(this.eventFaction == null){
			return;
		}
		final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner.getUniqueId());
		//Bukkit.broadcastMessage(ConfigurationService.KOTH_PLAYER_CAP.replace("%player%", winner.getName()).replace("%koth%", this.eventFaction.getEventType().getDisplayName()));
		Bukkit.broadcastMessage(ConfigurationService.BASECOLOUR + "[" + this.eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + ((playerFaction == null) ? winner.getName() : playerFaction.getName() + ChatColor.GRAY + " [" + winner.getName() + "]") + ChatColor.GOLD + " has captured " + ChatColor.LIGHT_PURPLE + this.eventFaction.getName() + ChatColor.GOLD + '.');
		final World world = winner.getWorld();
		final Location location = winner.getLocation();
		this.clearCooldown();

	}

	public boolean tryContesting(final EventFaction eventFaction, final CommandSender sender){
		if(this.eventFaction != null){
			sender.sendMessage(ChatColor.RED + "There is already an active event, use /event cancel to end it.");
			return false;
		}
		if(eventFaction instanceof KothFaction){
			final KothFaction kothFaction = (KothFaction) eventFaction;
			if(kothFaction.getCaptureZone() == null){
				sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as its' capture zone is not set.");
				return false;
			}
		}
		if(eventFaction instanceof PalaceFaction){
			final PalaceFaction palaceFaction = (PalaceFaction) eventFaction;
			if(palaceFaction.getCaptureZone() == null){
				sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as its' capture zone is not set.");
				return false;
			}
		}else if(eventFaction instanceof ConquestFaction){
			final ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
			final Collection<ConquestFaction.ConquestZone> zones = conquestFaction.getConquestZones();
			for(final ConquestFaction.ConquestZone zone : ConquestFaction.ConquestZone.values()){
				if(!zones.contains(zone)){
					sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as capture zone '" + zone.getDisplayName() + ChatColor.RED + "' is not set.");
					return false;
				}
			}
		}
		final long millis = System.currentTimeMillis();
		if(this.lastContestedEventMillis + EventTimer.RESCHEDULE_FREEZE_MILLIS - millis > 0L){
			sender.sendMessage(ChatColor.RED + "Cannot reschedule events within " + EventTimer.RESCHEDULE_FREEZE_WORDS + '.');
			return false;
		}
		this.lastContestedEventMillis = millis;
		this.startStamp = millis;
		this.eventFaction = eventFaction;
		eventFaction.getEventType().getEventTracker().onContest(eventFaction, this);
		if(eventFaction instanceof ConquestFaction){
			this.setRemaining(1000L, true);
			this.setPaused(true);
		}
		final Collection<CaptureZone> captureZones = eventFaction.getCaptureZones();
		for(final CaptureZone captureZone : captureZones){
			if(captureZone.isActive()){
				final Player player = Iterables.getFirst(captureZone.getCuboid().getPlayers(), null);
				if(player == null){
					continue;
				}
				if(!eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)){
					continue;
				}
				captureZone.setCappingPlayer(player);
			}
		}
		eventFaction.setDeathban(true);
		return true;
	}

	public long getUptime(){
		return System.currentTimeMillis() - this.startStamp;
	}

	public long getStartStamp(){
		return this.startStamp;
	}

	private void handleDisconnect(final Player player){
		Preconditions.checkNotNull((Object) player);
		if(this.eventFaction == null){
			return;
		}
		final Collection<CaptureZone> captureZones = this.eventFaction.getCaptureZones();
		for(final CaptureZone captureZone : captureZones){
			if(Objects.equal(captureZone.getCappingPlayer(), player)){
				this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction);
				captureZone.setCappingPlayer(null);
				break;
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(final PlayerDeathEvent event){
		this.handleDisconnect(event.getEntity());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerLogout(final PlayerQuitEvent event){
		this.handleDisconnect(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(final PlayerKickEvent event){
		this.handleDisconnect(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerTeleport(final PlayerTeleportEvent event){
		this.handleDisconnect(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerPortal(final PlayerPortalEvent event){
		this.handleDisconnect(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCaptureZoneEnter(final CaptureZoneEnterEvent event){
		if(this.eventFaction == null){
			return;
		}
		final CaptureZone captureZone = event.getCaptureZone();
		if(!this.eventFaction.getCaptureZones().contains(captureZone)){
			return;
		}
		final Player player = event.getPlayer();
		if(captureZone.getCappingPlayer() == null && this.eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)){
			captureZone.setCappingPlayer(player);
		}
	}


	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCaptureZoneLeave(final CaptureZoneLeaveEvent event){
		if(Objects.equal(event.getFaction(), this.eventFaction)){
			final Player player = event.getPlayer();
			final CaptureZone captureZone = event.getCaptureZone();
			if(Objects.equal(player, captureZone.getCappingPlayer()) && this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction)){
				captureZone.setCappingPlayer(null);
				for(final Player target : captureZone.getCuboid().getPlayers()){
					if(target != null && !target.equals(player) && this.eventFaction.getEventType().getEventTracker().onControlTake(target, captureZone)){
						captureZone.setCappingPlayer(target);
						break;
					}
				}
			}
		}
	}
}


