package net.veilmc.hcf.scoreboard.provider;

import net.veilmc.base.BasePlugin;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.classes.PvpClass;
import net.veilmc.hcf.classes.bard.BardClass;
import net.veilmc.hcf.classes.type.MinerClass;
import net.veilmc.hcf.faction.type.PlayerFaction;
import net.veilmc.hcf.kothgame.CaptureZone;
import net.veilmc.hcf.kothgame.EventTimer;
import net.veilmc.hcf.kothgame.eotw.EOTWHandler;
import net.veilmc.hcf.kothgame.faction.ConquestFaction;
import net.veilmc.hcf.kothgame.faction.EventFaction;
import net.veilmc.hcf.kothgame.tracker.ConquestTracker;
import net.veilmc.hcf.scoreboard.SidebarEntry;
import net.veilmc.hcf.scoreboard.SidebarProvider;
import net.veilmc.hcf.timer.GlobalTimer;
import net.veilmc.hcf.timer.PlayerTimer;
import net.veilmc.hcf.timer.Timer;
import net.veilmc.hcf.timer.type.NotchAppleTimer;
import net.veilmc.hcf.timer.type.SotwTimer;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.hcf.utils.DateTimeFormats;
import net.veilmc.hcf.utils.DurationFormatter;
import net.veilmc.util.BukkitUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TimerSidebarProvider implements SidebarProvider
{
    private final HCF plugin;
    private static final String STRAIGHT_LINE;
    private static DecimalFormat df = new DecimalFormat("#.##");

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


		if (sotwRunnable != null) {
			lines.add(new SidebarEntry(ChatColor.GREEN.toString() + ChatColor.BOLD, "SOTW" + ChatColor.GRAY + ": ", ChatColor.GOLD + DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true)));
		}

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
					lines.add(new SidebarEntry(ChatColor.RED.toString() + "" + playerTimer3.getScoreboardPrefix(), timerName1, ChatColor.GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining3, true)));
				}
			}
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
//		}*/


        long autore = BasePlugin.getPlugin().getAutoRestartHandler().getRemainingMilliseconds();
        if((autore <= 300000) && (autore > 0)){
            long remainingTicks = BasePlugin.getPlugin().getAutoRestartHandler().getRemainingTicks();
            long remainingMillis = remainingTicks * 50;
		    lines.add(new SidebarEntry(ChatColor.DARK_RED + "" + ChatColor.BOLD, "Reboot: ", ChatColor.RED + DurationFormatUtils.formatDuration(remainingMillis, (remainingMillis >= HCF.HOUR ? "HH:" : "") + "mm:ss")));
        }

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
                    lines.add(new SidebarEntry("  " + conquestZone.getColor() + ChatColor.BOLD, conquestZone.getName(), ChatColor.GRAY + ": " + DurationFormatter.getRemaining(captureZone.getRemainingCaptureMillis(), true)));
                }


			if(BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isStaffUtil()){
				lines.add(new SidebarEntry(ChatColor.GRAY, ChatColor.GRAY + "" + TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
			}

		}


		if (player.hasPermission("command.staffmode") && BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isStaffUtil()) {
            lines.add(new SidebarEntry(ChatColor.YELLOW + "" + ChatColor.BOLD + "Mod Mode: "));

            lines.add(new SidebarEntry(ChatColor.WHITE + " » " + ChatColor.YELLOW.toString(), "Vanished" + ChatColor.GRAY + ": ", BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isVanished() ? (ChatColor.GREEN + "True") : (ChatColor.RED + "Visible")));

            if (BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isStaffUtil()) {
                lines.add(new SidebarEntry(ChatColor.WHITE + " » " + ChatColor.YELLOW.toString(), "Channel" + ChatColor.GRAY + ": ", BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isInStaffChat() ? (ChatColor.AQUA + "Staff Chat") : (ChatColor.GREEN + "Global")));
            }
            if (HCF.getPlugin().getServerHandler().isChatDisabled()) {
                lines.add(new SidebarEntry("§f » §eChat", "§7: §cLocked", "§c (" + HCF.getRemaining(HCF.getPlugin().getServerHandler().getChatDisabledMillis() - System.currentTimeMillis(), true) + ")"));
            }
            if (isChatSlowed()) {
                lines.add(new SidebarEntry("§f » §eChat", "§7: §cSlowed ", "(" + BasePlugin.getPlugin().getServerHandler().getChatSlowedDelay() + "s)"));
            }

            lines.add(new SidebarEntry("§f » §eTPS", "§7: ", "§c" + df.format(Bukkit.spigot().getTPS()[0])));
        } else if(BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isVanished()) {
            lines.add(new SidebarEntry(ChatColor.WHITE + " » " + ChatColor.YELLOW.toString(), "Vanished" + ChatColor.GRAY + ": ",ChatColor.GREEN + "True"));
        }

		if (ConfigurationService.KIT_MAP) {
			lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " ", ChatColor.GREEN + "Kills" + ": " + ChatColor.YELLOW, player.getStatistic(Statistic.PLAYER_KILLS)));
			lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " ", ChatColor.GREEN + "Deaths" + ": " + ChatColor.YELLOW, player.getStatistic(Statistic.DEATHS)));

//			Integer k = Integer.valueOf(player.getStatistic(Statistic.PLAYER_KILLS));
//			double d = Integer.valueOf(player.getStatistic(Statistic.DEATHS));
//			double kd = k / d;
//			if (df.format(kd).matches(".*\\d+.*")){
//				lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " ", ChatColor.GREEN + "KDR" + ": " + ChatColor.YELLOW, df.format(kd)));
//			} else {
//				lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " ", ChatColor.GREEN + "KDR" + ": " + ChatColor.YELLOW, "0"));
//			}
		}

        if (!lines.isEmpty()) {
			lines.add(0, new SidebarEntry(ChatColor.GRAY, TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
			lines.add(lines.size(), new SidebarEntry(ChatColor.GRAY,ChatColor.STRIKETHROUGH + TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
		}
		return lines;
	}

    private boolean isChatSlowed() { return BasePlugin.getPlugin().getServerHandler().getRemainingChatSlowedMillis() > 0; }

	static {
        ThreadLocal.withInitial(() -> new DecimalFormat("##.#"));
        new SidebarEntry(" ", " ", " ");
		STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 14);
	}
}