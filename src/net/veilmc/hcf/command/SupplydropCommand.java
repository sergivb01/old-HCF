package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
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
        DecimalFormat df = new DecimalFormat("##");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSUPPLYDROP &7» &d" + s.getName() + " &ehas dropped a supply drop at &b&l" + df.format(s.getLocation().getX()) + ", " + df.format(s.getLocation().getZ())));
        return true;
    }
}