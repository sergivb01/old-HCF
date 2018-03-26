package net.veilmc.hcf.kothgame.argument;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.type.Faction;
import net.veilmc.hcf.kothgame.faction.EventFaction;
import net.veilmc.hcf.kothgame.faction.KothFaction;
import net.veilmc.hcf.palace.PalaceFaction;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class EventListArgument extends CommandArgument{
	private final HCF plugin;

	public EventListArgument(HCF plugin){
		super("list", "Check the uptime of an event");
		this.plugin = plugin;
		this.permission = "hcf.command.event.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		List<Faction> events = this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).collect(Collectors.toList());

		sender.sendMessage(ChatColor.GREEN + "Current events:");
		for(Faction factionEvent : events){
			sender.sendMessage(ChatColor.GREEN + factionEvent.getName() + ChatColor.DARK_GRAY +
					" (" + ChatColor.YELLOW + getFactionEventType(factionEvent) + ChatColor.DARK_GRAY + ")");
		}

		return true;
	}

	private String getFactionEventType(Faction factionEvent){
		if(factionEvent instanceof KothFaction){
			return "Koth";
		}else if(factionEvent instanceof PalaceFaction){
			return "Palace";
		}else{
			return "Conquest";
		}

	}


}

