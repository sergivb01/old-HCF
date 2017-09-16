
package com.customhcf.hcf.kothgame.argument;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.kothgame.faction.EventFaction;
import com.customhcf.util.command.CommandArgument;
import com.customhcf.util.cuboid.Cuboid;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventSetAreaArgument
extends CommandArgument {
    private static final int MIN_EVENT_CLAIM_AREA = 8;
    private final HCF plugin;

    public EventSetAreaArgument(HCF plugin) {
        super("setarea", "Sets the area of an event");
        this.plugin = plugin;
        this.aliases = new String[]{"setclaim", "setclaimarea", "setland"};
        this.permission = "hcf.command.event.argument." + this.getName();
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <kothName>";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "Only players can set event claim areas");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        WorldEditPlugin worldEditPlugin = this.plugin.getWorldEdit();
        if (worldEditPlugin == null) {
            sender.sendMessage((Object)ChatColor.RED + "WorldEdit must be installed to set event claim areas.");
            return true;
        }
        Player player = (Player)sender;
        Selection selection = worldEditPlugin.getSelection(player);
        if (selection == null) {
            sender.sendMessage((Object)ChatColor.RED + "You must make a WorldEdit selection to do this.");
            return true;
        }
        if (selection.getWidth() < 8 || selection.getLength() < 8) {
            sender.sendMessage((Object)ChatColor.RED + "Event claim areas must be at least " + 8 + 'x' + 8 + '.');
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (!(faction instanceof EventFaction)) {
            sender.sendMessage((Object)ChatColor.RED + "There is not an event faction named '" + args[1] + "'.");
            return true;
        }
        ((EventFaction)faction).setClaim(new Cuboid(selection.getMinimumPoint(), selection.getMaximumPoint()), (CommandSender)player);
        sender.sendMessage((Object)ChatColor.YELLOW + "Updated the claim for event " + faction.getName() + (Object)ChatColor.YELLOW + '.');
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
    }
}

