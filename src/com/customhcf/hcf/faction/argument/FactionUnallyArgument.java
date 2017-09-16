
package com.customhcf.hcf.faction.argument;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.FactionMember;
import com.customhcf.hcf.faction.event.FactionRelationRemoveEvent;
import com.customhcf.hcf.faction.struct.Relation;
import com.customhcf.hcf.faction.struct.Role;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class FactionUnallyArgument
extends CommandArgument {
    private static ImmutableList<String> COMPLETIONS;
    private final HCF plugin;

    public FactionUnallyArgument(HCF plugin) {
        super("unally", "Remove an ally pact with other factions.");
        this.plugin = plugin;
        this.aliases = new String[]{"unalliance", "neutral"};
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <all|factionName>";
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
            sender.sendMessage((Object)ChatColor.RED + "You must be a faction officer to edit relations.");
            return true;
        }
        Relation relation = Relation.ALLY;
        HashSet<PlayerFaction> targetFactions = new HashSet<PlayerFaction>();
        if (args[1].equalsIgnoreCase("all")) {
            List<PlayerFaction> allies = playerFaction.getAlliedFactions();
            if (allies.isEmpty()) {
                sender.sendMessage((Object)ChatColor.RED + "Your faction has no allies.");
                return true;
            }
            targetFactions.addAll(allies);
        } else {
            Faction searchedFaction = this.plugin.getFactionManager().getContainingFaction(args[1]);
            if (!(searchedFaction instanceof PlayerFaction)) {
                sender.sendMessage((Object)ChatColor.RED + "Player faction named or containing member with IGN or UUID " + args[1] + " not found.");
                return true;
            }
            targetFactions.add((PlayerFaction)searchedFaction);
        }
        for (PlayerFaction targetFaction : targetFactions) {
            if (playerFaction.getRelations().remove(targetFaction.getUniqueID()) == null || targetFaction.getRelations().remove(playerFaction.getUniqueID()) == null) {
                sender.sendMessage((Object)ChatColor.RED + "Your faction is not " + relation.getDisplayName() + (Object)ChatColor.RED + " with " + targetFaction.getDisplayName(playerFaction) + (Object)ChatColor.RED + '.');
                return true;
            }
            FactionRelationRemoveEvent event = new FactionRelationRemoveEvent(playerFaction, targetFaction, Relation.ALLY);
            Bukkit.getPluginManager().callEvent((Event)event);
            if (event.isCancelled()) {
                sender.sendMessage((Object)ChatColor.RED + "Could not drop " + relation.getDisplayName() + " with " + targetFaction.getDisplayName(playerFaction) + (Object)ChatColor.RED + ".");
                return true;
            }
            playerFaction.broadcast((Object)ChatColor.YELLOW + "Your faction has broken its " + relation.getDisplayName() + (Object)ChatColor.YELLOW + " with " + targetFaction.getDisplayName(playerFaction) + (Object)ChatColor.YELLOW + '.');
            targetFaction.broadcast((Object)ChatColor.YELLOW + playerFaction.getDisplayName(targetFaction) + (Object)ChatColor.YELLOW + " has dropped their " + relation.getDisplayName() + (Object)ChatColor.YELLOW + " with your faction.");
        }
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
        return Lists.newArrayList((Iterable)Iterables.concat(COMPLETIONS, (Iterable)playerFaction.getAlliedFactions().stream().map(Faction::getName).collect(Collectors.toList())));
    }
}

