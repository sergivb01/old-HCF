package com.sergivb01.hcf.scoreboard.provider;

import com.sergivb01.base.BasePlugin;
import com.sergivb01.base.user.BaseUser;
import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.classes.PvpClass;
import com.sergivb01.hcf.classes.bard.BardClass;
import com.sergivb01.hcf.classes.type.MinerClass;
import com.sergivb01.hcf.events.EventTimer;
import com.sergivb01.hcf.events.eotw.EOTWHandler;
import com.sergivb01.hcf.events.faction.ConquestFaction;
import com.sergivb01.hcf.events.faction.EventFaction;
import com.sergivb01.hcf.events.tracker.ConquestTracker;
import com.sergivb01.hcf.faction.type.PlayerFaction;
import com.sergivb01.hcf.scoreboard.SidebarEntry;
import com.sergivb01.hcf.scoreboard.SidebarProvider;
import com.sergivb01.hcf.timer.GlobalTimer;
import com.sergivb01.hcf.timer.PlayerTimer;
import com.sergivb01.hcf.timer.Timer;
import com.sergivb01.hcf.timer.type.NotchAppleTimer;
import com.sergivb01.hcf.timer.type.SotwTimer;
import com.sergivb01.hcf.timer.type.SpawnTagTimer;
import com.sergivb01.hcf.user.FactionUser;
import com.sergivb01.hcf.utils.DateTimeFormats;
import com.sergivb01.hcf.utils.DurationFormatter;
import com.sergivb01.hcf.utils.config.ConfigurationService;
import com.sergivb01.util.BukkitUtils;
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

import static com.sergivb01.hcf.HCF.HOUR;

public class TimerSidebarProvider implements SidebarProvider{
	private static final ThreadLocal<DecimalFormat> CONQUEST_FORMATTER;
	private static final String STRAIGHT_LINE;
	private static DecimalFormat df = new DecimalFormat("#.##");

	static{
		ThreadLocal.withInitial(() -> new DecimalFormat("##.#"));
		new SidebarEntry(" ", " ", " ");
		STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 14);
		CONQUEST_FORMATTER = ThreadLocal.withInitial(() -> new DecimalFormat("##.#"));
	}

	private final HCF plugin;

	public TimerSidebarProvider(final HCF plugin){
		super();
		this.plugin = plugin;
	}

	private static String handleBardFormat(final long millis, final boolean trailingZero){
		return (trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(millis * 0.001);
	}

	public static String getRemainingSpawn(long duration){
		return org.apache.commons.lang.time.DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
	}

	public String getTitle(){
		return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("scoreboard.title"));
	}

	@Override
	public List<SidebarEntry> getLines(final Player player){
		BaseUser baseUser = BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId());
		List<SidebarEntry> lines = new ArrayList<>();
		final EOTWHandler.EotwRunnable eotwRunnable = this.plugin.getEotwHandler().getRunnable();
		final PvpClass pvpClass = this.plugin.getPvpClassManager().getEquippedClass(player);
		EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
		final EventFaction eventFaction = eventTimer.getEventFaction();
		final SotwTimer.SotwRunnable sotwRunnable = this.plugin.getSotwTimer().getSotwRunnable();


		if(sotwRunnable != null){
			lines.add(new SidebarEntry(ChatColor.GREEN.toString() + ChatColor.BOLD, "SOTW" + ChatColor.DARK_GRAY + ": ", ChatColor.RED + DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true)));
		}

		if((pvpClass instanceof MinerClass)){
			lines.add(new SidebarEntry(ChatColor.GRAY + " » ", ChatColor.AQUA + "Diamonds", ChatColor.DARK_GRAY + ": " + ChatColor.RED + player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE)));
		}

		if(pvpClass != null && pvpClass instanceof BardClass){
			final BardClass bardClass = (BardClass) pvpClass;
			lines.add(new SidebarEntry(ChatColor.YELLOW.toString() + ChatColor.BOLD, "Bard Energy", ChatColor.DARK_GRAY + ": " + ChatColor.RED + handleBardFormat(bardClass.getEnergyMillis(player), true)));
			final long remaining = bardClass.getRemainingBuffDelay(player);
			if(remaining > 0L){
				lines.add(new SidebarEntry(ChatColor.YELLOW.toString() + ChatColor.BOLD, "Buff Cooldown", ChatColor.DARK_GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining, true)));
			}
		}


		final Collection<Timer> timers = this.plugin.getTimerManager().getTimers();
		for(final Timer timer : timers){
			if(timer instanceof EventTimer){
				EventTimer event = (EventTimer) timer;
				if(event.getEventFaction() instanceof ConquestFaction){
					continue;
				}
			}

			if(timer instanceof PlayerTimer && !(timer instanceof NotchAppleTimer)){
				final PlayerTimer playerTimer = (PlayerTimer) timer;
				final long remaining = playerTimer.getRemaining(player);
				if(remaining <= 0L){
					continue;
				}
				String timerName = playerTimer.getName();
				if(playerTimer instanceof SpawnTagTimer){
					lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), "" + ChatColor.BOLD + timerName, ChatColor.DARK_GRAY + ": " + ChatColor.RED + getRemainingSpawn(remaining)));
				}else{
					lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), "" + ChatColor.BOLD + timerName, ChatColor.DARK_GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining, true)));
				}

			}else if(timer instanceof GlobalTimer){
				GlobalTimer playerTimer = (GlobalTimer) timer;
				long remaining = playerTimer.getRemaining();
				if(remaining > 0L){
					String timerName1 = playerTimer.getName();
					lines.add(new SidebarEntry(ChatColor.RED.toString() + "" + playerTimer.getScoreboardPrefix(), timerName1, ChatColor.DARK_GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining, true)));
				}
			}
		}


		if(eotwRunnable != null){
			long remaining = eotwRunnable.getTimeUntilStarting();
			if(remaining > 0L){
				lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " Starts", " In " + HCF.getRemaining(remaining, true)));
			}else if((remaining = eotwRunnable.getTimeUntilCappable()) > 0L){
				lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " Cappable ", "In " + HCF.getRemaining(remaining, true)));
			}else{
				lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " is ", "currently active"));
			}
		}

		if(ConfigurationService.DEV){
			lines.add(new SidebarEntry(ChatColor.RED.toString() + ChatColor.BOLD, "DEVELOPMENT MODE", ""));
		}

		if(eventFaction instanceof ConquestFaction){
			final ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
			lines.add(new SidebarEntry(ChatColor.GOLD.toString(), ChatColor.BOLD + "Conquest Event", ""));
			lines.add(new SidebarEntry(" " + ChatColor.RED.toString() + CONQUEST_FORMATTER.get().format(conquestFaction.getRed().getRemainingCaptureMillis() / 1000.0D) + "s", ChatColor.DARK_GRAY + " ⎢ ", ChatColor.YELLOW.toString() + CONQUEST_FORMATTER.get().format(conquestFaction.getYellow().getRemainingCaptureMillis() / 1000.0D) + "s"));
			lines.add(new SidebarEntry(" " + ChatColor.GREEN.toString() + CONQUEST_FORMATTER.get().format(conquestFaction.getGreen().getRemainingCaptureMillis() / 1000.0D) + "s", ChatColor.DARK_GRAY + " ⎢ " + ChatColor.RESET, ChatColor.AQUA.toString() + CONQUEST_FORMATTER.get().format(conquestFaction.getBlue().getRemainingCaptureMillis() / 1000.0D) + "s"));

			final ConquestTracker conquestTracker1 = (ConquestTracker) conquestFaction.getEventType().getEventTracker();
			List<Map.Entry<PlayerFaction, Integer>> entries = new ArrayList<>(conquestTracker1.getFactionPointsMap().entrySet());
			final int max = baseUser.isStaffUtil() ? 1 : 4;
			if(entries.size() > max){
				entries = entries.subList(0, max);
			}
			int i = 0;
			for(final Map.Entry<PlayerFaction, Integer> entry : entries){
				if(!(i >= 4)){
					lines.add(new SidebarEntry(" " + ChatColor.GOLD + ChatColor.BOLD.toString() + (i + 1) + ". ", entry.getKey().getDisplayName(player), ChatColor.DARK_GRAY + ": " + ChatColor.RED + entry.getValue()));
					++i;
				}

			}

		}

		long autore = BasePlugin.getPlugin().getAutoRestartHandler().getRemainingMilliseconds();
		if((autore <= 300000) && (autore > 0)){
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


		if(baseUser.isVanished() && !baseUser.isStaffUtil()){
			lines.add(new SidebarEntry(ChatColor.GRAY + " » " + ChatColor.YELLOW.toString(), "Vanished" + ChatColor.GRAY + ": ", ChatColor.GREEN + "True"));
		}else if(baseUser.isStaffUtil()){
			lines.add(new SidebarEntry(ChatColor.GOLD + "" + ChatColor.BOLD + "Staff Mode: "));

			lines.add(new SidebarEntry(ChatColor.GRAY + " » " + ChatColor.YELLOW.toString(), "Vanished" + ChatColor.GRAY + ": ", baseUser.isVanished() ? (ChatColor.GREEN + "True") : (ChatColor.RED + "Visible")));
			lines.add(new SidebarEntry(ChatColor.GRAY + " » " + ChatColor.YELLOW.toString(), "Channel" + ChatColor.GRAY + ": ", baseUser.isInStaffChat() ? (ChatColor.AQUA + "Staff Chat") : (ChatColor.GREEN + "Global")));

			if(BasePlugin.getPlugin().getServerHandler().isChatDisabled()){
				lines.add(new SidebarEntry("§7 » §eChat", "§7: §fLocked ", "(" + HCF.getRemaining(BasePlugin.getPlugin().getServerHandler().getChatDisabledMillis() - System.currentTimeMillis(), true) + ")"));
			}

			if(isChatSlowed()){
				lines.add(new SidebarEntry("§7 » §eChat", "§7: §fSlowed ", "(" + BasePlugin.getPlugin().getServerHandler().getChatSlowedDelay() + "s)"));
			}

			lines.add(new SidebarEntry("§7 » §ePlayers", "§7: ", "§c" + Bukkit.getOnlinePlayers().size()));
		}


		final FactionUser factionUser = this.plugin.getUserManager().getUser(player.getUniqueId());

		if((ConfigurationService.KIT_MAP) && factionUser != null){
			lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " ", ChatColor.RED + "Balance: " + ChatColor.WHITE + "$", this.plugin.getEconomyManager().getBalance(player.getUniqueId())));
			lines.add(new SidebarEntry(ChatColor.RED, " Kills", ": " + ChatColor.WHITE + factionUser.getKills()));
			lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " ", ChatColor.RED + "Deaths" + ": " + ChatColor.WHITE, player.getStatistic(Statistic.DEATHS)));
		}

		if(!lines.isEmpty()){
			lines.add(0, new SidebarEntry(ChatColor.DARK_GRAY, TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
			lines.add(lines.size(), new SidebarEntry(ChatColor.DARK_GRAY, ChatColor.STRIKETHROUGH + TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
		}
		return lines;
	}

	private boolean isChatSlowed(){
		return BasePlugin.getPlugin().getServerHandler().getRemainingChatSlowedMillis() > 0;
	}

}