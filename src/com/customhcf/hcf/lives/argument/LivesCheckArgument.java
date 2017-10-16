
package com.customhcf.hcf.lives.argument;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.deathban.DeathbanManager;
import com.customhcf.util.command.CommandArgument;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LivesCheckArgument extends CommandArgument
{
    private final HCF plugin;

    public LivesCheckArgument(final HCF plugin) {
        super("check", "Check Lives");
        this.plugin = plugin;
        this.permission = "hcf.command.lives.argument." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [playerName]";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        OfflinePlayer target;
        if (args.length > 1) {
            target = Bukkit.getOfflinePlayer(args[1]);
        }
        else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
                return true;
            }
            target = (OfflinePlayer)sender;
        }
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
            return true;
        }
        final int targetLives = this.plugin.getDeathbanManager().getLives(target.getUniqueId());
        sender.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.YELLOW + " has " + ChatColor.LIGHT_PURPLE + targetLives + ChatColor.YELLOW + ' ' + ((targetLives == 1) ? "life" : "lives") + '.');
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}