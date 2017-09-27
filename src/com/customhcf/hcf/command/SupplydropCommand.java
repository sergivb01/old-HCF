package com.customhcf.hcf.command;

import com.customhcf.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SupplydropCommand
        implements CommandExecutor {
    private final HCF plugin;

    public SupplydropCommand(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by a player.");
            return true;
        }
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&l" + sender.getName() + " &ehas dropped a supply drop at &b&lx,y,z"));
        return true;
    }
}