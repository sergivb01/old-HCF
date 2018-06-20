package com.sergivb01.hcf.events.koth.argument;

import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.events.CaptureZone;
import com.sergivb01.hcf.events.faction.KothFaction;
import com.sergivb01.hcf.faction.type.Faction;
import com.sergivb01.util.JavaUtils;
import com.sergivb01.util.command.CommandArgument;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KothSetCapDelayArgument
		extends CommandArgument{
	private final HCF plugin;

	public KothSetCapDelayArgument(HCF plugin){
		super("setcapdelay", "Sets the cap delay of a KOTH");
		this.plugin = plugin;
		this.aliases = new String[]{"setcapturedelay"};
		this.permission = "hcf.commands.koth.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <kothName> <capDelay>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length < 3){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
		if(faction == null || !(faction instanceof KothFaction)){
			sender.sendMessage(ChatColor.RED + "There is not a KOTH arena named '" + args[1] + "'.");
			return true;
		}
		long duration = JavaUtils.parse(StringUtils.join(args, ' ', 2, args.length));
		if(duration == -1){
			sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
			return true;
		}
		KothFaction kothFaction = (KothFaction) faction;
		CaptureZone captureZone = kothFaction.getCaptureZone();
		if(captureZone == null){
			sender.sendMessage(ChatColor.RED + kothFaction.getDisplayName(sender) + ChatColor.RED + " does not have a capture zone.");
			return true;
		}
		if(captureZone.isActive() && duration < captureZone.getRemainingCaptureMillis()){
			captureZone.setRemainingCaptureMillis(duration);
		}
		captureZone.setDefaultCaptureMillis(duration);
		sender.sendMessage(ChatColor.YELLOW + "Set the capture delay of KOTH arena " + ChatColor.WHITE + kothFaction.getDisplayName(sender) + ChatColor.YELLOW + " to " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(duration, true, true) + ChatColor.WHITE + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 2){
			return Collections.emptyList();
		}
		return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof KothFaction).map(Faction::getName).collect(Collectors.toList());
	}
}

