
package com.customhcf.hcf.faction.argument.staff;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.util.command.CommandArgument;
import com.google.common.primitives.Doubles;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionSetDeathbanMultiplierArgument
extends CommandArgument {
    private static final double MIN_MULTIPLIER = 0.0;
    private static final double MAX_MULTIPLIER = 5.0;
    private final HCF plugin;

    public FactionSetDeathbanMultiplierArgument(HCF plugin) {
        super("setdeathbanmultiplier", "Sets the deathban multiplier of a faction.");
        this.plugin = plugin;
        this.permission = "hcf.command.faction.argument." + this.getName();
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <playerName|factionName> <newMultiplier>";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
            sender.sendMessage((Object)ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        Double multiplier = Doubles.tryParse((String)args[2]);
        if (multiplier == null) {
            sender.sendMessage((Object)ChatColor.RED + "'" + args[2] + "' is not a valid number.");
            return true;
        }
        if (multiplier < 0.0) {
            sender.sendMessage((Object)ChatColor.RED + "Deathban multipliers may not be less than " + 0.0 + '.');
            return true;
        }
        if (multiplier > 5.0) {
            sender.sendMessage((Object)ChatColor.RED + "Deathban multipliers may not be more than " + 5.0 + '.');
            return true;
        }
        double previousMultiplier = faction.getDeathbanMultiplier();
        faction.setDeathbanMultiplier(multiplier);
        Command.broadcastCommandMessage((CommandSender)sender, (String)((Object)ChatColor.YELLOW + "Set deathban multiplier of " + faction.getName() + " from " + previousMultiplier + " to " + multiplier + '.'));
        return true;
    }
}

