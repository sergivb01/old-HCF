
package com.customhcf.hcf.command;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.utils.ConfigurationService;
import com.customhcf.hcf.timer.type.PvpProtectionTimer;
import com.customhcf.util.BukkitUtils;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class PvpTimerCommand
implements CommandExecutor,
TabCompleter {
    private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("enable", "time");
    private final HCF plugin;

    public PvpTimerCommand(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player player = (Player)sender;
        PvpProtectionTimer pvpTimer = this.plugin.getTimerManager().pvpProtectionTimer;
        if (args.length < 1) {
            this.printUsage(sender, label, pvpTimer);
            return true;
        }
        if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("off")) {
            if (pvpTimer.getRemaining(player) > 0) {
                sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is now off.");
                pvpTimer.clearCooldown(player);
                return true;
            }
            if (pvpTimer.getLegible().remove(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + "You will no longer be legible for your " + pvpTimer.getDisplayName() + ChatColor.YELLOW + " when you leave spawn.");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is currently not active.");
            return true;
        }
        if (!(args[0].equalsIgnoreCase("remaining") || args[0].equalsIgnoreCase("time") || args[0].equalsIgnoreCase("left") || args[0].equalsIgnoreCase("check"))) {
            this.printUsage(sender, label, pvpTimer);
            return true;
        }
        long remaining = pvpTimer.getRemaining(player);
        if (remaining <= 0) {
            sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is currently not active.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Your " + pvpTimer.getDisplayName() + ChatColor.YELLOW + " timer is active for another " + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.YELLOW + (pvpTimer.isPaused(player) ? " and is currently paused" : "") + '.');
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? BukkitUtils.getCompletions(args, COMPLETIONS) : Collections.emptyList();
    }

    private void printUsage(CommandSender sender, String label, PvpProtectionTimer pvpTimer) {
        sender.sendMessage(ConfigurationService.FIRST_LINE);
        sender.sendMessage(ConfigurationService.SECOND_LINE);
        sender.sendMessage(ConfigurationService.THIRD_LINE);
        sender.sendMessage(ConfigurationService.FOURTH_LINE);
        sender.sendMessage(ConfigurationService.FIFTH_LINE);
    }
}

