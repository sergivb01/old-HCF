package com.customhcf.hcf.scoreboard.provider;

import com.customhcf.base.BasePlugin;
import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.ConfigurationService;
import com.customhcf.hcf.Utils.Cooldowns;
import com.customhcf.hcf.Utils.DateTimeFormats;
import com.customhcf.hcf.Utils.DurationFormatter;
import com.customhcf.hcf.classes.PvpClass;
import com.customhcf.hcf.classes.bard.BardClass;
import com.customhcf.hcf.classes.type.MinerClass;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.hcf.kothgame.CaptureZone;
import com.customhcf.hcf.kothgame.EventTimer;
import com.customhcf.hcf.kothgame.eotw.EOTWHandler;
import com.customhcf.hcf.kothgame.faction.ConquestFaction;
import com.customhcf.hcf.kothgame.faction.EventFaction;
import com.customhcf.hcf.kothgame.tracker.ConquestTracker;
import com.customhcf.hcf.scoreboard.SidebarEntry;
import com.customhcf.hcf.scoreboard.SidebarProvider;
import com.customhcf.hcf.timer.GlobalTimer;
import com.customhcf.hcf.timer.PlayerTimer;
import com.customhcf.hcf.timer.Timer;
import com.customhcf.hcf.timer.type.NotchAppleTimer;
import com.customhcf.hcf.timer.type.SotwTimer;
import com.customhcf.hcf.user.FactionUser;
import com.customhcf.util.BukkitUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TimerSidebarProvider implements SidebarProvider
{
	public static final ThreadLocal<DecimalFormat> CONQUEST_FORMATTER;
	private static final SidebarEntry EMPTY_ENTRY_FILLER;
	private final HCF plugin;
	protected static final String STRAIGHT_LINE;

	public TimerSidebarProvider(final HCF plugin) {
		super();
		this.plugin = plugin;
	}

	private static String handleBardFormat(final long millis, final boolean trailingZero) {
		return (trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(millis * 0.001);
	}

	public String getTitle()
	{
		return HCF.getPlugin().scoreboardTitle;
	}


	@Override
	public List<SidebarEntry> getLines(final Player player) {
		List<SidebarEntry> lines = new ArrayList<SidebarEntry>();
		final EOTWHandler.EotwRunnable eotwRunnable = this.plugin.getEotwHandler().getRunnable();
		final PvpClass pvpClass = this.plugin.getPvpClassManager().getEquippedClass(player);
		EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
		List<SidebarEntry> conquestLines = null;
		final EventFaction eventFaction = eventTimer.getEventFaction();
		final SotwTimer.SotwRunnable sotwRunnable = this.plugin.getSotwTimer().getSotwRunnable();
		final int lives = HCF.getPlugin().getDeathbanManager().getLives(player.getUniqueId());
		PermissionUser user = PermissionsEx.getUser(player.getName());
		List<String> groups = user.getParentIdentifiers();
		FactionUser hcf = HCF.getPlugin().getUserManager().getUser(player.getUniqueId());
		PlayerFaction faction = HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId());




		if (sotwRunnable != null) {
			lines.add(new SidebarEntry(ChatColor.GREEN.toString() + ChatColor.BOLD, "SOTW" + ChatColor.GRAY + ": ", ChatColor.GOLD + DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true)));
		}

//		if (pvpClass != null) {
//			lines.add(new SidebarEntry(ChatColor.YELLOW + "", HCF.getPlugin().armor, ChatColor.GRAY + ": " + ChatColor.RED + pvpClass.getName()));
//		}


		if ((pvpClass instanceof MinerClass)) {
			lines.add(new SidebarEntry(ChatColor.GRAY + "", ChatColor.AQUA + "" + ChatColor.BOLD + "Miner Class", ChatColor.GRAY + ":"));
			lines.add(new SidebarEntry(ChatColor.GRAY + " » ", ChatColor.AQUA + "Diamonds", ChatColor.GRAY + ": " + ChatColor.RED + player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE)));
		}
		if (pvpClass != null && pvpClass instanceof BardClass) {
			final BardClass bardClass = (BardClass) pvpClass;
			lines.add(new SidebarEntry(ChatColor.AQUA.toString() + ChatColor.BOLD, "Bard Energy", ChatColor.GRAY + ": " + ChatColor.RED + handleBardFormat(bardClass.getEnergyMillis(player), true)));
			final long remaining2 = bardClass.getRemainingBuffDelay(player);
			if (remaining2 > 0L) {
				lines.add(new SidebarEntry(ChatColor.AQUA.toString() + ChatColor.BOLD, "Buff Cooldown", ChatColor.GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining2, true)));
			}
		}


		final Collection<Timer> timers = this.plugin.getTimerManager().getTimers();
		for (final Timer timer : timers) {
			if (timer instanceof EventTimer) {
				EventTimer event = (EventTimer) timer;
				if (event.getEventFaction() instanceof ConquestFaction) {
					continue;
				}
			}
			if (timer instanceof PlayerTimer && !(timer instanceof NotchAppleTimer)) {
				final PlayerTimer playerTimer = (PlayerTimer) timer;
				final long remaining2 = playerTimer.getRemaining(player);
				if (remaining2 <= 0L) {
					continue;
				}
				String timerName = playerTimer.getName();
				if (timerName.length() > 14) {
					timerName = timerName.substring(0, timerName.length());
				}
				lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), "" + ChatColor.BOLD + timerName, ChatColor.GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining2, true)));
			} else if ((timer instanceof GlobalTimer)) {
				GlobalTimer playerTimer2 = (GlobalTimer) timer;
				long remaining2 = playerTimer2.getRemaining1();
				if (remaining2 > 0L) {
					String timerName = playerTimer2.getName();
					if (timerName.length() > 14) {
						timerName = timerName.substring(0, timerName.length());
					}
					lines.add(new SidebarEntry(ChatColor.RED.toString() + "" + playerTimer2.getScoreboardPrefix(), timerName, ChatColor.GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining2, true)));
				}
			}
		}

		Collection<Timer> timers1 = this.plugin.getTimerManager().getTimers();
		for (Timer timer1 : timers1) {
			if (timer1 instanceof EventTimer) {
				EventTimer event = (EventTimer) timer1;
				if (event.getEventFaction() instanceof ConquestFaction) {
					continue;
				}
			}
			if ((timer1 instanceof GlobalTimer)) {
				GlobalTimer playerTimer3 = (GlobalTimer) timer1;
				long remaining3 = playerTimer3.getRemaining();
				if (remaining3 > 0L) {
					String timerName1 = playerTimer3.getName();
					if (timerName1.length() > 14) {
						timerName1 = timerName1.substring(0, timerName1.length());
					}
                    lines.add(new SidebarEntry(playerTimer3.getScoreboardPrefix(), playerTimer3 + ChatColor.GRAY, ": " + ChatColor.RED + (playerTimer.getName().equalsIgnoreCase("SpawnTag") ? DurationFormatUtils.formatDuration(remaining, "mm:ss") : DurationFormatter.getRemaining(remaining, true))));
					lines.add(new SidebarEntry(ChatColor.RED.toString() + "" + playerTimer3.getScoreboardPrefix(), timerName1, ChatColor.GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining3, true)));
				}
			}
		}

		if (Cooldowns.isOnCooldown("revive_cooldown", player)) {
			lines.add(new SidebarEntry(ChatColor.BLUE + "Revive", ChatColor.GRAY + ": " + ChatColor.RED, "00:" + Cooldowns.getCooldownForPlayerInt("revive_cooldown", player) / 60));
		}

		if (eotwRunnable != null) {
			long remaining3 = eotwRunnable.getTimeUntilStarting();
			if (remaining3 > 0L) {
				lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " Starts", " In " + HCF.getRemaining(remaining3, true)));
			} else if ((remaining3 = eotwRunnable.getTimeUntilCappable()) > 0L) {
				lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " Cappable ", "In " + HCF.getRemaining(remaining3, true)));
			} else {
                lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " is ", "currently active"));
            }
		}



//		if (eventFaction instanceof ConquestFaction) {
//			lines.add(lines.size(), new SidebarEntry(ChatColor.GRAY + " "));
//			final ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
//			//conquestFaction = (ConquestFaction)eventFaction;
//			conquestLines = new ArrayList();
//			conquestLines.add(new SidebarEntry(ChatColor.GOLD.toString(), ChatColor.BOLD + "Conquest Event", ""));
//			conquestLines.add(new SidebarEntry(" " + ChatColor.RED.toString() + CONQUEST_FORMATTER.get().format(conquestFaction.getRed().getRemainingCaptureMillis() / 1000.0D) + "s", ChatColor.GOLD + " | ", ChatColor.YELLOW.toString() + CONQUEST_FORMATTER.get().format(conquestFaction.getYellow().getRemainingCaptureMillis() / 1000.0D) + "s"));
//			conquestLines.add(new SidebarEntry(" " + ChatColor.GREEN.toString() + CONQUEST_FORMATTER.get().format(conquestFaction.getGreen().getRemainingCaptureMillis() / 1000.0D) + "s", ChatColor.GOLD + " | " + ChatColor.RESET, ChatColor.AQUA.toString() + CONQUEST_FORMATTER.get().format(conquestFaction.getBlue().getRemainingCaptureMillis() / 1000.0D) + "s"));
//
//			final ConquestFaction conquestFaction1 = (ConquestFaction)eventFaction;
//			final ConquestTracker conquestTracker1 = (ConquestTracker)conquestFaction.getEventType().getEventTracker();
//			List<Map.Entry<PlayerFaction, Integer>> entries = new ArrayList<Map.Entry<PlayerFaction, Integer>>(conquestTracker1.getFactionPointsMap().entrySet());
//			final int max = BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isStaffUtil() ? 1 : 4;
//			if (entries.size() > max) {
//				entries = entries.subList(0, max);
//			}
//			int i = 0;
//			for (final Map.Entry<PlayerFaction, Integer> entry : entries) {
//				lines.add(new SidebarEntry(" " + ChatColor.GOLD + ChatColor.BOLD.toString() + (i + 1) + ". ", entry.getKey().getDisplayName(player), ChatColor.GRAY + ": " + entry.getValue()));
//				++i;
//			}
//
//
//		}*?

		else if (eventFaction instanceof ConquestFaction) {
			if (!lines.isEmpty()) {
				lines.add(new SidebarEntry(ChatColor.GRAY, ChatColor.GRAY + TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
			}
			lines.add(new SidebarEntry(ChatColor.GOLD + ChatColor.BOLD.toString(), "Conquest", ChatColor.GRAY + ":"));
			final ConquestFaction conquestFaction = (ConquestFaction)eventFaction;
			final ConquestTracker conquestTracker = (ConquestTracker)conquestFaction.getEventType().getEventTracker();
			List<Map.Entry<PlayerFaction, Integer>> entries = new ArrayList<Map.Entry<PlayerFaction, Integer>>(conquestTracker.getFactionPointsMap().entrySet());
			final int max = BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isStaffUtil() ? 1 : 4;
			if (entries.size() > max) {
				entries = entries.subList(0, max);
			}
			int i = 0;
			for (final Map.Entry<PlayerFaction, Integer> entry : entries) {
				lines.add(new SidebarEntry(" " + ChatColor.GOLD + ChatColor.BOLD.toString() + (i + 1) + ". ", entry.getKey().getDisplayName(player), ChatColor.GRAY + ": " + entry.getValue()));
				++i;
			}
			if (!entries.isEmpty()) {
				lines.add(new SidebarEntry(ChatColor.GRAY + "", TimerSidebarProvider.STRAIGHT_LINE + ChatColor.GRAY, TimerSidebarProvider.STRAIGHT_LINE));
			}


			for (final CaptureZone captureZone : conquestFaction.getCaptureZones()) {
				final ConquestFaction.ConquestZone conquestZone = conquestFaction.getZone(captureZone);
                /*final long time = Math.max(captureZone.getRemainingCaptureMillis(), 0L);
                final String left = HCF.getRemaining(time, false);*/
				lines.add(new SidebarEntry("  " + conquestZone.getColor() + ChatColor.BOLD, conquestZone.getName(), ChatColor.GRAY + ": " + DurationFormatter.getRemaining(captureZone.getRemainingCaptureMillis(), true)));
			}


			if(BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isStaffUtil()){
				lines.add(new SidebarEntry(ChatColor.GRAY, ChatColor.GRAY + "" + TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
			}

		}




		if (player.hasPermission("command.staffmode") && BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isStaffUtil()) {
			lines.add(new SidebarEntry(ChatColor.YELLOW + "" + ChatColor.BOLD + "Mod Mode: "));
			if (player.hasPermission("command.vanish")) {
				lines.add(new SidebarEntry(ChatColor.WHITE + " » " + ChatColor.YELLOW.toString(), "Vanished" + ChatColor.GRAY + ": ", BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isVanished() ? (ChatColor.GREEN + "True") : (ChatColor.RED + "Visible")));
			}
			if (HCF.getPlugin().getServerHandler().isChatDisabled()) {
				lines.add(new SidebarEntry("§f » §eChat", "§7: §cLocked", "§c (" + HCF.getRemaining(HCF.getPlugin().getServerHandler().getChatDisabledMillis() - System.currentTimeMillis(), true) + ")"));
			} else if (HCF.getPlugin().getServerHandler().isChatSlowed()) {
                lines.add(new SidebarEntry("§f » §eChat", "§7: §cSlowed ", "(" + BasePlugin.getPlugin().getServerHandler().getChatSlowedDelay() + "s)"));
            }
//			} else {
//				lines.add(new SidebarEntry("§f » §eChat", "§7: §7Normal ", ""));
//			}
			lines.add(new SidebarEntry("§f » §eGamemode" , "§7: ", (player.getGameMode() == GameMode.CREATIVE) ? (ChatColor.GREEN + "Creative") : (ChatColor.RED + "Survival")));
		} else if(BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isVanished()) {
            lines.add(new SidebarEntry(ChatColor.WHITE + " » " + ChatColor.YELLOW.toString(), "Vanished" + ChatColor.GRAY + ": ", BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isVanished() ? (ChatColor.GREEN + "True") : (ChatColor.RED + "Visible")));
        }
		if (ConfigurationService.KIT_MAP) {
//			Integer k = Integer.valueOf(player.getStatistic(Statistic.PLAYER_KILLS));
//			double d = Integer.valueOf(player.getStatistic(Statistic.DEATHS));
//			double kd = k / d;
//			DecimalFormat df = new DecimalFormat("#.##");
			lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " ", ChatColor.GREEN + "Kills" + ": " + ChatColor.YELLOW, Integer.valueOf(player.getStatistic(Statistic.PLAYER_KILLS))));
			lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " ", ChatColor.GREEN + "Deaths" + ": " + ChatColor.YELLOW, Integer.valueOf(player.getStatistic(Statistic.DEATHS))));

//			if (df.format(kd).matches(".*\\d+.*")){
//				lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " ", ChatColor.GREEN + "KDR" + ": " + ChatColor.YELLOW, df.format(kd)));
//			} else {
//				lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " ", ChatColor.GREEN + "KDR" + ": " + ChatColor.YELLOW, "0"));
//			}

		}
		if (conquestLines != null && !conquestLines.isEmpty()) {
			conquestLines.addAll(lines);
			lines = conquestLines;
		}
		if (!lines.isEmpty()) {
			lines.add(0, new SidebarEntry(ChatColor.GRAY, TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
			lines.add(lines.size(), new SidebarEntry(ChatColor.GRAY,ChatColor.STRIKETHROUGH + TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
		}
		return lines;
	}

	static {
		CONQUEST_FORMATTER = new ThreadLocal<DecimalFormat>() {
			@Override
			protected DecimalFormat initialValue() {
				return new DecimalFormat("##.#");
			}
		};
		EMPTY_ENTRY_FILLER = new SidebarEntry(" ", " ", " ");
		STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 14);
	}
}