
package com.customhcf.hcf.kothgame.koth.argument;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.DateTimeFormats;
import com.customhcf.util.command.CommandArgument;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KothNextArgument
extends CommandArgument {
    private final HCF plugin;

    public KothNextArgument(HCF plugin) {
        super("next", "View the next scheduled KOTH");
        this.plugin = plugin;
        this.permission = "hcf.command.koth.argument." + this.getName();
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long millis = System.currentTimeMillis();
        sender.sendMessage((Object)ChatColor.YELLOW + "The current server time is " + (Object)ChatColor.GREEN + DateTimeFormats.DAY_MTH_HR_MIN_AMPM.format(millis) + (Object)ChatColor.GOLD + '.');
        Map<LocalDateTime, String> scheduleMap = this.plugin.eventScheduler.getScheduleMap();
        if (scheduleMap.isEmpty()) {
            sender.sendMessage((Object)ChatColor.RED + "There is not an event schedule for after now.");
            return true;
        }
        LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
        for (Map.Entry<LocalDateTime, String> entry : scheduleMap.entrySet()) {
            LocalDateTime scheduleDateTime = entry.getKey();
            if (now.isAfter(scheduleDateTime)) continue;
            int currentDay = now.getDayOfYear();
            String eventName = entry.getValue();
            int dayDifference = scheduleDateTime.getDayOfYear() - currentDay;
            String monthName = scheduleDateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            String weekName = scheduleDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            ChatColor colour = dayDifference == 0 ? ChatColor.GREEN : ChatColor.RED;
            sender.sendMessage("  " + (Object)colour + WordUtils.capitalizeFully((String)eventName) + (Object)ChatColor.GRAY + " is the next event: " + (Object)ChatColor.YELLOW + weekName + ' ' + scheduleDateTime.getDayOfMonth() + ' ' + monthName + (Object)ChatColor.GREEN + " (" + KothScheduleArgument.HHMMA.format(scheduleDateTime) + ')');
            return true;
        }
        sender.sendMessage((Object)ChatColor.RED + "There is not an event scheduled after now.");
        return true;
    }
}

