
package com.customhcf.hcf.faction.argument.staff;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.FactionMember;
import com.customhcf.hcf.faction.struct.Role;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.util.command.CommandArgument;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionForceLeaderArgument
extends CommandArgument {
    private final HCF plugin;

    public FactionForceLeaderArgument(HCF plugin) {
        super("forceleader", "Forces the leader of a faction.");
        this.plugin = plugin;
        this.permission = "hcf.command.faction.argument." + this.getName();
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <playerName>";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        PlayerFaction playerFaction = this.plugin.getFactionManager().getContainingPlayerFaction(args[1]);
        if (playerFaction == null) {
            sender.sendMessage((Object)ChatColor.RED + "Faction containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        FactionMember factionMember = playerFaction.getMember(args[1]);
        if (factionMember == null) {
            sender.sendMessage((Object)ChatColor.RED + "Faction containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        if (factionMember.getRole() == Role.LEADER) {
            sender.sendMessage((Object)ChatColor.RED + factionMember.getName() + " is already the leader of " + playerFaction.getDisplayName(sender) + (Object)ChatColor.RED + '.');
            return true;
        }
        FactionMember leader = playerFaction.getLeader();
        String oldLeaderName = leader == null ? "none" : leader.getName();
        String newLeaderName = factionMember.getName();
        if (leader != null) {
            leader.setRole(Role.CAPTAIN);
        }
        factionMember.setRole(Role.LEADER);
        playerFaction.broadcast((Object)ChatColor.YELLOW + sender.getName() + " has forcefully set the leader to " + newLeaderName + '.');
        sender.sendMessage(ChatColor.GOLD.toString() + (Object)ChatColor.BOLD + "Leader of " + playerFaction.getName() + "was forcefully set from " + oldLeaderName + " to " + newLeaderName + '.');
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 2 ? null : Collections.emptyList();
    }
}

