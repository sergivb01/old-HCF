
package com.customhcf.hcf.kothgame.argument;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.DateTimeFormats;
import com.customhcf.hcf.kothgame.EventTimer;
import com.customhcf.hcf.kothgame.faction.EventFaction;
import com.customhcf.util.command.CommandArgument;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventUptimeArgument
extends CommandArgument {
    private final HCF plugin;

    public EventUptimeArgument(HCF plugin) {
        super("uptime", "Check the uptime of an event");
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + this.getName();
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        EventFaction eventFaction;
        EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
        if (eventTimer.getRemaining() <= 0) {
            sender.sendMessage((Object)ChatColor.RED + "There is not a running event.");
            return true;
        }
        sender.sendMessage((Object)ChatColor.YELLOW + "Up-time of " + eventTimer.getName() + " timer" + ((eventFaction = eventTimer.getEventFaction()) == null ? "" : new StringBuilder().append(": ").append((Object)ChatColor.BLUE).append('(').append(eventFaction.getDisplayName(sender)).append((Object)ChatColor.BLUE).append(')').toString()) + (Object)ChatColor.YELLOW + " is " + (Object)ChatColor.GRAY + DurationFormatUtils.formatDurationWords((long)eventTimer.getUptime(), (boolean)true, (boolean)true) + (Object)ChatColor.YELLOW + ", started at " + (Object)ChatColor.GOLD + DateTimeFormats.HR_MIN_AMPM_TIMEZONE.format(eventTimer.getStartStamp()) + (Object)ChatColor.YELLOW + '.');
        return true;
    }
}

