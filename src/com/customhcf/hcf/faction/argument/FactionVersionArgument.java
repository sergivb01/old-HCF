package com.customhcf.hcf.faction.argument;

import com.customhcf.hcf.HCF;
import com.customhcf.util.BukkitUtils;
import com.customhcf.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionVersionArgument
        extends CommandArgument {
    private final HCF plugin;

    public FactionVersionArgument(HCF plugin) {
        super("version", "View plugin information.");
        this.plugin = plugin;
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        player.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        player.sendMessage(ChatColor.GRAY + " ");
        player.sendMessage(ChatColor.WHITE + " * " + ChatColor.YELLOW.toString() + ChatColor.BOLD + HCF.getPlugin().getDescription().getName());
        player.sendMessage(ChatColor.WHITE + " * " + ChatColor.GRAY.toString() + "Version: " + ChatColor.GOLD + HCF.getPlugin().getDescription().getVersion());
        player.sendMessage(ChatColor.GRAY + " ");
        player.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }
}

