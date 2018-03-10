package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.utils.Chat;
import net.veilmc.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

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
        Player s = (Player)sender;
        if (s.hasPermission("hcf.command.supplydrop")) {
            Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9&lSUPPLYDROP &7» &3" + s.getName() + " &fhas dropped a supply drop at &b&l" + s.getLocation().getBlockX() + ", " + s.getLocation().getBlockZ()));
            Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            return true;
        }
        return true;
    }
}