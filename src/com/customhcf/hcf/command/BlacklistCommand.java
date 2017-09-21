package com.customhcf.hcf.command;

import com.customhcf.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlacklistCommand implements CommandExecutor {
    private final HCF plugin;

    public BlacklistCommand(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + args[0] + " has been Blacklisted from the Network ");
        Bukkit.broadcastMessage(" ");
        Bukkit.dispatchCommand(sender, "banip " + args[0] + " Blacklisted -s");
        return true;
    }
}