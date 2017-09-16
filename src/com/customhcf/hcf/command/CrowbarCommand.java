
package com.customhcf.hcf.command;

import com.customhcf.hcf.crowbar.Crowbar;
import com.customhcf.util.BukkitUtils;
import com.google.common.base.Optional;
import com.google.common.primitives.Ints;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class CrowbarCommand
implements CommandExecutor,
TabCompleter {
    private final List<String> completions = Arrays.asList("spawn", "setspawners", "setendframes");

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: /" + label + " <spawn|setspawners|setendframes>");
            return true;
        }
        Player player = (Player)sender;
        if (args[0].equalsIgnoreCase("spawn")) {
            ItemStack stack = new Crowbar().getItemIfPresent();
            player.getInventory().addItem(new ItemStack[]{stack});
            sender.sendMessage((Object)ChatColor.YELLOW + "You have given yourself a " + stack.getItemMeta().getDisplayName() + (Object)ChatColor.YELLOW + '.');
            return true;
        }

        Optional<Crowbar> crowbarOptional = Crowbar.fromStack(player.getItemInHand());
        if (!crowbarOptional.isPresent()) {
            sender.sendMessage((Object)ChatColor.RED + "You are not holding a Crowbar.");
            return true;
        }
        if (args[0].equalsIgnoreCase("setspawners")) {
            if (args.length < 2) {
                sender.sendMessage((Object)ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
                return true;
            }
            Integer amount = Ints.tryParse((String)args[1]);
            if (amount == null) {
                sender.sendMessage((Object)ChatColor.RED + "'" + args[1] + "' is not a number.");
                return true;
            }
            if (amount < 0) {
                sender.sendMessage((Object)ChatColor.RED + "You cannot set Spawner uses to an amount less than " + 0 + '.');
                return true;
            }
            if (amount > 1) {
                sender.sendMessage((Object)ChatColor.RED + "Crowbars have maximum Spawner uses of " + 1 + '.');
                return true;
            }
            Crowbar crowbar = (Crowbar)crowbarOptional.get();
            crowbar.setSpawnerUses(amount);
            player.setItemInHand(crowbar.getItemIfPresent());
            sender.sendMessage((Object)ChatColor.YELLOW + "Set Spawner uses of held Crowbar to " + amount + '.');
            return true;
        }
        if (!args[0].equalsIgnoreCase("setendframes")) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: /" + label + " <spawn|setspawners|setendframes>");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
            return true;
        }
        Integer amount = Ints.tryParse((String)args[1]);
        if (amount == null) {
            sender.sendMessage((Object)ChatColor.RED + "'" + args[1] + "' is not a number.");
            return true;
        }
        if (amount < 0) {
            sender.sendMessage((Object)ChatColor.RED + "You cannot set End Frame uses to an amount less than " + 0 + '.');
            return true;
        }
        if (amount > 5) {
            sender.sendMessage((Object)ChatColor.RED + "Crowbars have maximum End Frame uses of " + 1 + '.');
            return true;
        }
        Crowbar crowbar = (Crowbar)crowbarOptional.get();
        crowbar.setEndFrameUses(amount);
        player.setItemInHand(crowbar.getItemIfPresent());
        sender.sendMessage((Object)ChatColor.YELLOW + "Set End Frame uses of held Crowbar to " + amount + '.');
        return true;
    }


    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? BukkitUtils.getCompletions((String[])args, this.completions) : Collections.emptyList();
    }
}

