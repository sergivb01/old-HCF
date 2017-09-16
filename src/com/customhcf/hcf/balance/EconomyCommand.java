package com.customhcf.hcf.balance;

import com.customhcf.base.BaseConstants;
import com.customhcf.hcf.HCF;
import com.customhcf.util.BukkitUtils;
import com.customhcf.util.JavaUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class EconomyCommand
implements CommandExecutor,
TabCompleter {
    private static final ImmutableList<String> COMPLETIONS;
    private static final ImmutableList<String> GIVE;
    private static final ImmutableList<String> TAKE;
    private final HCF plugin;

    public EconomyCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer target;
        boolean hasStaffPermission = sender.hasPermission(command.getPermission() + ".staff");
        if (args.length > 0 && hasStaffPermission) {
            target = BukkitUtils.offlinePlayerWithNameOrUUID((String)args[0]);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage((Object)ChatColor.RED + "Usage: /" + label + " <playerName>");
                return true;
            }
            target = (OfflinePlayer)sender;
        }
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }
        UUID uuid = target.getUniqueId();
        int balance = this.plugin.getEconomyManager().getBalance(uuid);
        if (args.length < 2 || !hasStaffPermission) {
            sender.sendMessage((Object)ChatColor.YELLOW + (sender.equals((Object)target) ? "Your balance" : new StringBuilder().append("Balance of ").append(target.getName()).toString()) + " is " + (Object)ChatColor.LIGHT_PURPLE + '$' + balance + (Object)ChatColor.YELLOW + '.');
            return true;
        }
        if (GIVE.contains((Object)args[1].toLowerCase())) {
            if (args.length < 3) {
                sender.sendMessage((Object)ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
                return true;
            }
            Integer amount = Ints.tryParse((String)args[2]);
            if (amount == null) {
                sender.sendMessage((Object)ChatColor.RED + "'" + args[2] + "' is not a valid number.");
                return true;
            }
            int newBalance = this.plugin.getEconomyManager().addBalance(uuid, amount);
            sender.sendMessage(new String[]{(Object)ChatColor.YELLOW + "Added " + '$' + JavaUtils.format((Number)amount) + " to balance of " + target.getName() + '.', (Object)ChatColor.YELLOW + "Balance of " + target.getName() + " is now " + '$' + newBalance + '.'});
            return true;
        }
        if (TAKE.contains((Object)args[1].toLowerCase())) {
            if (args.length < 3) {
                sender.sendMessage((Object)ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
                return true;
            }
            Integer amount = Ints.tryParse((String)args[2]);
            if (amount == null) {
                sender.sendMessage((Object)ChatColor.RED + "'" + args[2] + "' is not a valid number.");
                return true;
            }
            int newBalance = this.plugin.getEconomyManager().subtractBalance(uuid, amount);
            sender.sendMessage(new String[]{(Object)ChatColor.YELLOW + "Taken " + '$' + JavaUtils.format((Number)amount) + " from balance of " + target.getName() + '.', (Object)ChatColor.YELLOW + "Balance of " + target.getName() + " is now " + '$' + newBalance + '.'});
            return true;
        }
        if (!args[1].equalsIgnoreCase("set")) {
            sender.sendMessage((Object)ChatColor.GOLD + (sender.equals((Object)target) ? "Your balance" : new StringBuilder().append("Balance of ").append(target.getName()).toString()) + " is " + (Object)ChatColor.WHITE + '$' + balance + (Object)ChatColor.GOLD + '.');
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
            return true;
        }
        Integer amount = Ints.tryParse((String)args[2]);
        if (amount == null) {
            sender.sendMessage((Object)ChatColor.RED + "'" + args[2] + "' is not a valid number.");
            return true;
        }
        int newBalance = this.plugin.getEconomyManager().setBalance(uuid, amount);
        sender.sendMessage((Object)ChatColor.YELLOW + "Set balance of " + target.getName() + " to " + '$' + JavaUtils.format((Number)newBalance) + '.');
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return args.length == 2 ? BukkitUtils.getCompletions((String[])args, COMPLETIONS) : Collections.emptyList();
    }

    static {
        TAKE = ImmutableList.of("take", "negate", "minus", "subtract");
        GIVE = ImmutableList.of("give", "add");
        COMPLETIONS = ImmutableList.of("add","set", "take");
    }
}

