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
<<<<<<< HEAD
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GRAY + " ");
        sender.sendMessage(ChatColor.WHITE + " * " + ChatColor.YELLOW.toString() + ChatColor.BOLD + HCF.getPlugin().getDescription().getName());
        sender.sendMessage(ChatColor.WHITE + " * " + ChatColor.GRAY.toString() + "Version: " + ChatColor.GOLD + HCF.getPlugin().getDescription().getVersion());
        sender.sendMessage(ChatColor.WHITE + " * " + ChatColor.GRAY.toString() + "Authors: " + ChatColor.GOLD + "Move2Linux & LTD");
        sender.sendMessage(ChatColor.GRAY + " ");
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
=======
        Player p = (Player)sender;
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThis server is running &6" + HCF.getPlugin().getDescription().getName() + "&e. Version: &6" + HCF.getPlugin().getDescription().getVersion()));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eCreated for use on &6Veil Network &7(veilmc.net/veilhcf.us)"));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
>>>>>>> 7f1c2655ebf03a56a444119ebb563b178094a16d
        return true;
    }


}

