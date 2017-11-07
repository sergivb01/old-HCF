package net.veilmc.hcf.faction.argument;

import net.veilmc.hcf.HCF;
import net.veilmc.util.BukkitUtils;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GRAY + " ");
        sender.sendMessage(ChatColor.WHITE + " * " + ChatColor.YELLOW.toString() + ChatColor.BOLD + HCF.getPlugin().getDescription().getName());
        sender.sendMessage(ChatColor.WHITE + " * " + ChatColor.GRAY.toString() + "Version: " + ChatColor.GOLD + HCF.getPlugin().getDescription().getVersion());
        sender.sendMessage(ChatColor.WHITE + " * " + ChatColor.GRAY.toString() + "Authors: " + ChatColor.GOLD + "Move2Linux & LTD");
        sender.sendMessage(ChatColor.GRAY + " ");
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }


}

