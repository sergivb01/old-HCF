
package com.customhcf.hcf.kothgame.argument;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.kothgame.EventType;
import com.customhcf.hcf.kothgame.faction.ConquestFaction;
import com.customhcf.hcf.kothgame.faction.KothFaction;
import com.customhcf.hcf.palace.PalaceFaction;
import com.customhcf.util.command.CommandArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventCreateArgument
extends CommandArgument {
    private final HCF plugin;

    public EventCreateArgument(HCF plugin) {
        super("create", "Defines a new event", new String[]{"make", "define"});
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + this.getName();
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <eventName> <Conquest|KOTH|PALACE>";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String upperCase;
        if (args.length < 3) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (faction != null) {
            sender.sendMessage((Object)ChatColor.RED + "There is already a faction named " + args[1] + '.');
            return true;
        }
        switch (upperCase = args[2].toUpperCase()) {
            case "CONQUEST": {
                faction = new ConquestFaction(args[1]);
                break;
            }
            case "KOTH": {
                faction = new KothFaction(args[1]);
                break;
            }
            case "PALACE": {
                faction = new PalaceFaction(args[1]);
                break;
            }
            default: {
                sender.sendMessage(this.getUsage(label));
                return true;
            }
        }
        this.plugin.getFactionManager().createFaction(faction, sender);
        sender.sendMessage((Object)ChatColor.YELLOW + "Created event faction " + (Object)ChatColor.WHITE + faction.getDisplayName(sender) + (Object)ChatColor.YELLOW + " with type " + WordUtils.capitalizeFully((String)args[2]) + '.');
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3) {
            return Collections.emptyList();
        }
        EventType[] eventTypes = EventType.values();
        ArrayList<String> results = new ArrayList<String>(eventTypes.length);
        for (EventType eventType : eventTypes) {
            results.add(eventType.name());
        }
        return results;
    }
}

