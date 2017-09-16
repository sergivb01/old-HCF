package com.customhcf.hcf.faction.argument.staff;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.util.command.CommandArgument;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionLockArgument
extends CommandArgument {
    private final HCF plugin;

    public FactionLockArgument(HCF plugin) {
        super("lock", " Lock all factions");
        this.plugin = plugin;
        this.permission = "hcf.command.faction.argument." + this.getName();
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <on|off|factionName>";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        if (this.plugin.getFactionManager().getFaction(args[1]) == null) {
            if (args[1].equalsIgnoreCase("on")) {
                Bukkit.broadcastMessage((String)((Object)ChatColor.RED + "All Factions are being locked!"));
                for (Faction faction : this.plugin.getFactionManager().getFactions()) {
                    if (!(faction instanceof PlayerFaction)) continue;
                    faction.setLocked(true);
                }
            }else if (args[1].equalsIgnoreCase("off")) {
                Bukkit.broadcastMessage((String)((Object)ChatColor.GREEN + "All Factions are being un-locked!"));
                for (Faction faction : this.plugin.getFactionManager().getFactions()) {
                    if (!(faction instanceof PlayerFaction)) continue;
                    faction.setLocked(false);
                }
            }else{
                sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
            }
        } else if (this.plugin.getFactionManager().getFaction(args[1]).isLocked()) {
            sender.sendMessage((Object)ChatColor.GREEN + args[1] + " is now un-locked.");
            this.plugin.getFactionManager().getFaction(args[1]).setLocked(false);
        } else {
            sender.sendMessage((Object)ChatColor.RED + args[1] + " is now locked.");
            this.plugin.getFactionManager().getFaction(args[1]).setLocked(true);
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 2 ? null : Collections.emptyList();
    }
}
