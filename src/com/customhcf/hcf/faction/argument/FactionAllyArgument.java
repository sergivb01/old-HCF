
package com.customhcf.hcf.faction.argument;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.ConfigurationService;
import com.customhcf.hcf.faction.event.FactionRelationCreateEvent;
import com.customhcf.hcf.faction.struct.Relation;
import com.customhcf.hcf.faction.struct.Role;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.util.command.CommandArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class FactionAllyArgument
extends CommandArgument {
    private static final Relation RELATION = Relation.ALLY;
    private final HCF plugin;

    public FactionAllyArgument(HCF plugin) {
        super("ally", "Make an ally pact with other factions.", new String[]{"alliance"});
        this.plugin = plugin;
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <factionName>";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Player player = (Player)sender;
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage((Object)ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage((Object)ChatColor.RED + "You must be an officer to make relation wishes.");
            return true;
        }
        Faction containingFaction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (!(containingFaction instanceof PlayerFaction)) {
            sender.sendMessage((Object)ChatColor.RED + "Player faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        PlayerFaction targetFaction = (PlayerFaction)containingFaction;
        if (playerFaction.equals(targetFaction)) {
            sender.sendMessage((Object)ChatColor.RED + "You cannot send " + RELATION.getDisplayName() + (Object)ChatColor.RED + " requests to your own faction.");
            return true;
        }
        Collection<UUID> allied = playerFaction.getAllied();
        if (allied.size() >= ConfigurationService.MAX_ALLIES_PER_FACTION) {
            sender.sendMessage((Object)ChatColor.RED + "Your faction cant have more allies than " + ConfigurationService.MAX_ALLIES_PER_FACTION + '.');
            return true;
        }
        if (targetFaction.getAllied().size() >= ConfigurationService.MAX_ALLIES_PER_FACTION) {
            sender.sendMessage(targetFaction.getDisplayName(sender) + (Object)ChatColor.RED + " has reached their maximum alliance limit, which is " + ConfigurationService.MAX_ALLIES_PER_FACTION + '.');
            return true;
        }
        if (allied.contains(targetFaction.getUniqueID())) {
            sender.sendMessage((Object)ChatColor.RED + "Your faction already is " + RELATION.getDisplayName() + 'd' + (Object)ChatColor.RED + " with " + targetFaction.getDisplayName(playerFaction) + (Object)ChatColor.RED + '.');
            return true;
        }
        if (targetFaction.getRequestedRelations().remove(playerFaction.getUniqueID()) != null) {
            FactionRelationCreateEvent event = new FactionRelationCreateEvent(playerFaction, targetFaction, RELATION);
            Bukkit.getPluginManager().callEvent((Event)event);
            targetFaction.getRelations().put(playerFaction.getUniqueID(), RELATION);
            targetFaction.broadcast((Object)ChatColor.YELLOW + "Your faction is now " + RELATION.getDisplayName() + 'd' + (Object)ChatColor.YELLOW + " with " + playerFaction.getDisplayName(targetFaction) + (Object)ChatColor.YELLOW + '.');
            playerFaction.getRelations().put(targetFaction.getUniqueID(), RELATION);
            playerFaction.broadcast((Object)ChatColor.YELLOW + "Your faction is now " + RELATION.getDisplayName() + 'd' + (Object)ChatColor.YELLOW + " with " + targetFaction.getDisplayName(playerFaction) + (Object)ChatColor.YELLOW + '.');
            return true;
        }
        if (playerFaction.getRequestedRelations().putIfAbsent(targetFaction.getUniqueID(), RELATION) != null) {
            sender.sendMessage((Object)ChatColor.YELLOW + "Your faction has already requested to " + RELATION.getDisplayName() + (Object)ChatColor.YELLOW + " with " + targetFaction.getDisplayName(playerFaction) + (Object)ChatColor.YELLOW + '.');
            return true;
        }
        playerFaction.broadcast(targetFaction.getDisplayName(playerFaction) + (Object)ChatColor.YELLOW + " were informed that you wish to be " + RELATION.getDisplayName() + (Object)ChatColor.YELLOW + '.');
        targetFaction.broadcast(playerFaction.getDisplayName(targetFaction) + (Object)ChatColor.YELLOW + " has sent a request to be " + RELATION.getDisplayName() + (Object)ChatColor.YELLOW + ". Use " + (Object)ConfigurationService.ALLY_COLOUR + "/faction " + this.getName() + ' ' + playerFaction.getName() + (Object)ChatColor.YELLOW + " to accept.");
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        Player player = (Player)sender;
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            return Collections.emptyList();
        }
        ArrayList<String> results = new ArrayList<String>();
        return results;
    }
}

