
package com.customhcf.hcf.faction.argument;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.utils.ConfigurationService;
import com.customhcf.hcf.faction.FactionMember;
import com.customhcf.hcf.faction.struct.ChatChannel;
import com.customhcf.hcf.faction.struct.Relation;
import com.customhcf.hcf.faction.struct.Role;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.util.command.CommandArgument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionAcceptArgument
extends CommandArgument {
    private final HCF plugin;

    public FactionAcceptArgument(HCF plugin) {
        super("accept", "Accept a join request from an existing faction.", new String[]{"join", "a"});
        this.plugin = plugin;
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <factionName>";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Player player = (Player)sender;
        if (this.plugin.getFactionManager().getPlayerFaction(player) != null) {
            sender.sendMessage(ChatColor.RED + "You are already in a faction.");
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "You can only join player factions.");
            return true;
        }
        PlayerFaction targetFaction = (PlayerFaction)faction;
        if (targetFaction.getMembers().size() >= ConfigurationService.FACTION_PLAYER_LIMIT) {
            sender.sendMessage(faction.getDisplayName(sender) + ChatColor.RED + " is full. Faction limits are at " + ConfigurationService.FACTION_PLAYER_LIMIT + '.');
            return true;
        }
        if (!targetFaction.isOpen() && !targetFaction.getInvitedPlayerNames().contains(player.getName())) {
            sender.sendMessage(ChatColor.RED + faction.getDisplayName(sender) + ChatColor.RED + " has not invited you.");
            return true;
        }
        if (targetFaction.isLocked()) {
            sender.sendMessage(ChatColor.RED + "You cannot join locked factions.");
            return true;
        }
        if (targetFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER))) {
            targetFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.YELLOW + " has joined the faction.");
        }
        return true;
    }
}

