
package com.customhcf.hcf.command;

import java.util.Collections;
import java.util.List;

import com.mysql.jdbc.StringUtils;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.ConfigurationService;
import com.customhcf.hcf.timer.type.SotwTimer;
import com.customhcf.util.BukkitUtils;
import com.customhcf.util.JavaUtils;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class SotwCommand implements CommandExecutor, TabCompleter
{
    private static final List<String> COMPLETIONS;
    private final HCF plugin;

    public SotwCommand(final HCF plugin) {
        super();
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {


        if(ConfigurationService.KIT_MAP) {
            sender.sendMessage(org.bukkit.ChatColor.RED + "There is no need for this command on VeilMC kitmap.");
            return false;
        }


        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " +  "start h:m:s ");
                    sender.sendMessage(ChatColor.RED + "Example: /sotw start 3h3m");
                    return true;
                }
                final long duration = JavaUtils.parse(args[1]);
                if (duration == -1L) {
                    sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is an invalid duration.");
                    return true;
                }
                if (duration < 1000L) {
                    sender.sendMessage(ChatColor.RED + "SOTW protection time must last for at least 20 ticks.");
                    return true;
                }
                final SotwTimer.SotwRunnable sotwRunnable = this.plugin.getSotwTimer().getSotwRunnable();
                if (sotwRunnable != null) {
                    sender.sendMessage(ChatColor.RED + "SOTW protection is already enabled, use /" + label + " cancel to end it.");
                    return true;
                }
                this.plugin.getSotwTimer().start(duration);
                sender.sendMessage(ConfigurationService.SOTW_STARTED.replace("%time%", DurationFormatUtils.formatDurationWords(duration, true, true)));
                //sender.sendMessage(ChatColor.RED + "Started SOTW protection for " + DurationFormatUtils.formatDurationWords(duration, true, true) + ".");
                return true;
            }
            else if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("cancel")) {
                if (this.plugin.getSotwTimer().cancel()) {
                	sender.sendMessage(ConfigurationService.SOTW_CANCELLED);
                    return true;
                }
            	sender.sendMessage(ConfigurationService.SOTW_NOT_ACTIVE);
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " +  " start h:m:s ");
        sender.sendMessage(ChatColor.RED + "Example: /sotw start 3h3m");
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 1) ? BukkitUtils.getCompletions(args, SotwCommand.COMPLETIONS) : Collections.emptyList();
    }

    static {
        COMPLETIONS = ImmutableList.of("start", "end");
    }
}