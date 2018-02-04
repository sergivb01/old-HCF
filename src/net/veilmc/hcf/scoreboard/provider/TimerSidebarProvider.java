package net.veilmc.hcf.scoreboard.provider;

import net.veilmc.base.BasePlugin;
import net.veilmc.base.user.BaseUser;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.classes.PvpClass;
import net.veilmc.hcf.classes.bard.BardClass;
import net.veilmc.hcf.classes.type.MinerClass;
import net.veilmc.hcf.faction.type.PlayerFaction;
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
import net.veilmc.hcf.timer.type.SpawnTagTimer;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.hcf.utils.DateTimeFormats;
import net.veilmc.hcf.utils.DurationFormatter;
import net.veilmc.util.BukkitUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
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

import static net.veilmc.hcf.HCF.HOUR;

public class TimerSidebarProvider implements SidebarProvider {

    private static DecimalFormat df = new DecimalFormat("#.##");

    private static final ThreadLocal<DecimalFormat> CONQUEST_FORMATTER;
    private static final SidebarEntry EMPTY_ENTRY_FILLER;
    private final HCF plugin;
    private static final String STRAIGHT_LINE;

    public TimerSidebarProvider(final HCF plugin) {
        super();
        this.plugin = plugin;
    }

    private static String handleBardFormat(final long millis, final boolean trailingZero) {
        return (trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(millis * 0.001);
    }

    public String getTitle() {
        return HCF.getPlugin().scoreboardTitle;
    }

    @Override
    public List<SidebarEntry> getLines(final Player player) {
        BaseUser baseUser = BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId());
        List<SidebarEntry> lines = new ArrayList<>();
        final EOTWHandler.EotwRunnable eotwRunnable = this.plugin.getEotwHandler().getRunnable();
        final PvpClass pvpClass = this.plugin.getPvpClassManager().getEquippedClass(player);
        EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
        final EventFaction eventFaction = eventTimer.getEventFaction();
        final SotwTimer.SotwRunnable sotwRunnable = this.plugin.getSotwTimer().getSotwRunnable();


        if (sotwRunnable != null) {
            lines.add(new SidebarEntry(ChatColor.GREEN.toString() + ChatColor.BOLD, "SOTW" + ChatColor.DARK_GRAY + ": ", ChatColor.GOLD + DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true)));
        }

        if ((pvpClass instanceof MinerClass)) {
            lines.add(new SidebarEntry(ChatColor.DARK_GRAY + "", ChatColor.AQUA + "" + ChatColor.BOLD + "Miner Class", ChatColor.DARK_GRAY + ":"));
            lines.add(new SidebarEntry(ChatColor.DARK_GRAY + " » ", ChatColor.AQUA + "Diamonds", ChatColor.DARK_GRAY + ": " + ChatColor.RED + player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE)));
        }

        if (pvpClass != null && pvpClass instanceof BardClass) {
            final BardClass bardClass = (BardClass) pvpClass;
            lines.add(new SidebarEntry(ChatColor.AQUA.toString() + ChatColor.BOLD, "Bard Energy", ChatColor.DARK_GRAY + ": " + ChatColor.RED + handleBardFormat(bardClass.getEnergyMillis(player), true)));
            final long remaining = bardClass.getRemainingBuffDelay(player);
            if (remaining > 0L) {
                lines.add(new SidebarEntry(ChatColor.AQUA.toString() + ChatColor.BOLD, "Buff Cooldown", ChatColor.DARK_GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining, true)));
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
                final long remaining = playerTimer.getRemaining(player);
                if (remaining <= 0L) {
                    continue;
                }
                String timerName = playerTimer.getName();
                if (timerName.length() > 14) {
                    timerName = timerName.substring(0, timerName.length());
                }
                if(playerTimer instanceof SpawnTagTimer){
                    lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), "" + ChatColor.BOLD + timerName, ChatColor.DARK_GRAY + ": " + ChatColor.RED + getRemainingSpawn(remaining)));
                }else {
                    lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), "" + ChatColor.BOLD + timerName, ChatColor.DARK_GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining, true)));
                }

            }else if(timer instanceof GlobalTimer){
                GlobalTimer playerTimer = (GlobalTimer) timer;
                long remaining = playerTimer.getRemaining();
                if (remaining > 0L) {
                    String timerName1 = playerTimer.getName();
                    if (timerName1.length() > 14) {
                        timerName1 = timerName1.substring(0, timerName1.length());
                    }
                    lines.add(new SidebarEntry(ChatColor.RED.toString() + "" + playerTimer.getScoreboardPrefix(), timerName1, ChatColor.DARK_GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining, true)));
                }
            }
        }



        if (eotwRunnable != null) {
            long remaining = eotwRunnable.getTimeUntilStarting();
            if (remaining > 0L) {
                lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " Starts", " In " + HCF.getRemaining(remaining, true)));
            } else if ((remaining = eotwRunnable.getTimeUntilCappable()) > 0L) {
                lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " Cappable ", "In " + HCF.getRemaining(remaining, true)));
            } else {
                lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " is ", "currently active"));
            }
        }


        if (eventFaction instanceof ConquestFaction) {
            final ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
            lines.add(new SidebarEntry(ChatColor.GOLD.toString(), ChatColor.BOLD + "Conquest Event", ""));
            lines.add(new SidebarEntry(" " + ChatColor.RED.toString() + CONQUEST_FORMATTER.get().format(conquestFaction.getRed().getRemainingCaptureMillis() / 1000.0D) + "s", ChatColor.DARK_GRAY + " ⎢ ", ChatColor.YELLOW.toString() + CONQUEST_FORMATTER.get().format(conquestFaction.getYellow().getRemainingCaptureMillis() / 1000.0D) + "s"));
            lines.add(new SidebarEntry(" " + ChatColor.GREEN.toString() + CONQUEST_FORMATTER.get().format(conquestFaction.getGreen().getRemainingCaptureMillis() / 1000.0D) + "s", ChatColor.DARK_GRAY + " ⎢ " + ChatColor.RESET, ChatColor.AQUA.toString() + CONQUEST_FORMATTER.get().format(conquestFaction.getBlue().getRemainingCaptureMillis() / 1000.0D) + "s"));

            final ConquestTracker conquestTracker1 = (ConquestTracker) conquestFaction.getEventType().getEventTracker();
            List<Map.Entry<PlayerFaction, Integer>> entries = new ArrayList<>(conquestTracker1.getFactionPointsMap().entrySet());
            final int max = baseUser.isStaffUtil() ? 1 : 4;
            if (entries.size() > max) {
                entries = entries.subList(0, max);
            }
            int i = 0;
            for (final Map.Entry<PlayerFaction, Integer> entry : entries) {
                if(!(i >= 4)) {
                    lines.add(new SidebarEntry(" " + ChatColor.GOLD + ChatColor.BOLD.toString() + (i + 1) + ". ", entry.getKey().getDisplayName(player), ChatColor.DARK_GRAY + ": " + ChatColor.RED + entry.getValue()));
                    ++i;
                }

            }

        }


        long autore = BasePlugin.getPlugin().getAutoRestartHandler().getRemainingMilliseconds();
        if ((autore <= 300000) && (autore > 0)) {
            long remainingTicks = BasePlugin.getPlugin().getAutoRestartHandler().getRemainingTicks();
            long remainingMillis = remainingTicks * 50;
            lines.add(new SidebarEntry(ChatColor.DARK_RED + "" + ChatColor.BOLD, "Reboot: ", ChatColor.RED + DurationFormatUtils.formatDuration(remainingMillis, (remainingMillis >= HOUR ? "HH:" : "") + "mm:ss")));
        }

	/*	else if (eventFaction instanceof ConquestFaction) {
			if (!lines.isEmpty()) {
				lines.add(new SidebarEntry(ChatColor.DARK_GRAY, ChatColor.DARK_GRAY + TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
			}
			lines.add(new SidebarEntry(ChatColor.GOLD + ChatColor.BOLD.toString(), "Conquest", ChatColor.DARK_GRAY + ":"));
			final ConquestFaction conquestFaction = (ConquestFaction)eventFaction;
			final ConquestTracker conquestTracker = (ConquestTracker)conquestFaction.getEventType().getEventTracker();
			List<Map.Entry<PlayerFaction, Integer>> entries = new ArrayList<Map.Entry<PlayerFaction, Integer>>(conquestTracker.getFactionPointsMap().entrySet());
			final int max = baseUser.isStaffUtil() ? 1 : 4;

            if (entries.size() > max) {
				entries = entries.subList(0, max);
			}


            int i = 0;



            for (final Map.Entry<PlayerFaction, Integer> entry : entries) {
				lines.add(new SidebarEntry(" " + ChatColor.GOLD + ChatColor.BOLD.toString() + (i + 1) + ". ", entry.getKey().getDisplayName(player), ChatColor.DARK_GRAY + ": " + entry.getValue()));
				++i;
			}
			if (!entries.isEmpty()) {
				lines.add(new SidebarEntry(ChatColor.DARK_GRAY + "", TimerSidebarProvider.STRAIGHT_LINE + ChatColor.DARK_GRAY, TimerSidebarProvider.STRAIGHT_LINE));
			}

                for (final CaptureZone captureZone : conquestFaction.getCaptureZones()) {
                    final ConquestFaction.ConquestZone conquestZone = conquestFaction.getZone(captureZone);
                    lines.add(new SidebarEntry("  " + conquestZone.getColor() + ChatColor.BOLD, conquestZone.getName(), ChatColor.DARK_GRAY + ": " + DurationFormatter.getRemaining(captureZone.getRemainingCaptureMillis(), true)));
               }


			if(baseUser.isStaffUtil()){
				lines.add(new SidebarEntry(ChatColor.DARK_GRAY, ChatColor.DARK_GRAY + "" + TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
			}
*/


        if(baseUser.isVanished() || baseUser.isStaffUtil()){
            lines.add(new SidebarEntry(ChatColor.DARK_GRAY + " » " + ChatColor.BLUE.toString(), "Vanished" + ChatColor.DARK_GRAY + ": ", baseUser.isVanished() ? (ChatColor.GREEN + "True") : (ChatColor.RED + "Visible")));
        }

		if (player.hasPermission("command.staffmode") && baseUser.isStaffUtil()) {
            lines.add(new SidebarEntry(ChatColor.DARK_GRAY + "" + ChatColor.BLUE + "Staff Mode: "));

            if (baseUser.isStaffUtil()) {
                lines.add(new SidebarEntry(ChatColor.DARK_GRAY + " » " + ChatColor.BLUE.toString(), "Channel" + ChatColor.DARK_GRAY + ": ", baseUser.isInStaffChat() ? (ChatColor.AQUA + "Staff Chat") : (ChatColor.GREEN + "Global")));
            }
            if (HCF.getPlugin().getServerHandler().isChatDisabled()) {
                lines.add(new SidebarEntry("§8 » §9Chat", "§8: §fLocked ", "(" + HCF.getRemaining(HCF.getPlugin().getServerHandler().getChatDisabledMillis() - System.currentTimeMillis(), true) + ")"));
            }
            if (isChatSlowed()) {
                lines.add(new SidebarEntry("§8 » §9Chat", "§8: §fSlowed ", "(" + BasePlugin.getPlugin().getServerHandler().getChatSlowedDelay() + "s)"));
            }

            lines.add(new SidebarEntry("§8 » §9Players", "§8: ", "§c" + Bukkit.getOnlinePlayers().size()));
        }

		if (ConfigurationService.KIT_MAP) {
            lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " ", ChatColor.BLUE + "Balance: " + ChatColor.WHITE + "$", this.plugin.getEconomyManager().getBalance(player.getUniqueId())));
			lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " ", ChatColor.BLUE + "Kills" + ": " + ChatColor.WHITE, player.getStatistic(Statistic.PLAYER_KILLS)));
			lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " ", ChatColor.BLUE + "Deaths" + ": " + ChatColor.WHITE, player.getStatistic(Statistic.DEATHS)));

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
			lines.add(0, new SidebarEntry(ChatColor.DARK_GRAY, TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
			lines.add(lines.size(), new SidebarEntry(ChatColor.DARK_GRAY,ChatColor.STRIKETHROUGH + TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
		}
		return lines;
	}

    private boolean isChatSlowed() { return BasePlugin.getPlugin().getServerHandler().getRemainingChatSlowedMillis() > 0; }


    public static String getRemainingSpawn(long duration) {
        return org.apache.commons.lang.time.DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
    }

	static {
        ThreadLocal.withInitial(() -> new DecimalFormat("##.#"));
        new SidebarEntry(" ", " ", " ");
		STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 14);
	}
    static {
        CONQUEST_FORMATTER = new ThreadLocal<DecimalFormat>() {
            @Override
            protected DecimalFormat initialValue() {
                return new DecimalFormat("##.#");
            }
        };
        EMPTY_ENTRY_FILLER = new SidebarEntry(" ", " ", " ");
    }
}