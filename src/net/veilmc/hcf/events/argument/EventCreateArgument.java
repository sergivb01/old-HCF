package net.veilmc.hcf.events.argument;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.events.EventType;
import net.veilmc.hcf.events.faction.ConquestFaction;
import net.veilmc.hcf.events.faction.KothFaction;
import net.veilmc.hcf.events.palace.PalaceFaction;
import net.veilmc.hcf.faction.type.Faction;
import net.veilmc.util.command.CommandArgument;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventCreateArgument
		extends CommandArgument{
	private final HCF plugin;

	public EventCreateArgument(HCF plugin){
		super("create", "Defines a new event", new String[]{"make", "define"});
		this.plugin = plugin;
		this.permission = "hcf.commands.event.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <eventName> <Conquest|KOTH|PALACE>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		String upperCase;
		if(args.length < 3){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
		if(faction != null){
			sender.sendMessage(ChatColor.RED + "There is already a faction named " + args[1] + '.');
			return true;
		}
		switch(upperCase = args[2].toUpperCase()){
			case "CONQUEST":{
				faction = new ConquestFaction(args[1]);
				break;
			}
			case "KOTH":{
				faction = new KothFaction(args[1]);
				break;
			}
			case "PALACE":{
				faction = new PalaceFaction(args[1]);
				break;
			}
			default:{
				sender.sendMessage(this.getUsage(label));
				return true;
			}
		}
		this.plugin.getFactionManager().createFaction(faction, sender);
		sender.sendMessage(ChatColor.YELLOW + "Created event faction " + ChatColor.WHITE + faction.getDisplayName(sender) + ChatColor.YELLOW + " with type " + WordUtils.capitalizeFully(args[2]) + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 3){
			return Collections.emptyList();
		}
		EventType[] eventTypes = EventType.values();
		ArrayList<String> results = new ArrayList<String>(eventTypes.length);
		for(EventType eventType : eventTypes){
			results.add(eventType.name());
		}
		return results;
	}
}

