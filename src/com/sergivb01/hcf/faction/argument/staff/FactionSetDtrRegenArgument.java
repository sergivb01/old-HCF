package com.sergivb01.hcf.faction.argument.staff;

import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.faction.FactionManager;
import com.sergivb01.hcf.faction.type.Faction;
import com.sergivb01.hcf.faction.type.PlayerFaction;
import com.sergivb01.hcf.utils.config.ConfigurationService;
import com.sergivb01.util.JavaUtils;
import com.sergivb01.util.command.CommandArgument;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionSetDtrRegenArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionSetDtrRegenArgument(HCF plugin){
		super("setdtrregen", "Sets the DTR cooldown of a faction.", new String[]{"setdtrregeneration"});
		this.plugin = plugin;
		this.permission = "hcf.commands.faction.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <playerName|factionName> <newRegen>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(ConfigurationService.KIT_MAP){
			sender.sendMessage(ChatColor.RED + "There is no need for this commands on kitmap.");
			return false;
		}
		if(args.length < 3){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		long newRegen = JavaUtils.parse(args[2]);
		if(newRegen == -1){
			sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
			return true;
		}
		if(newRegen > FactionManager.MAX_DTR_REGEN_MILLIS){
			sender.sendMessage(ChatColor.RED + "Cannot set factions DTR regen above " + FactionManager.MAX_DTR_REGEN_WORDS + ".");
			return true;
		}
		Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
		if(faction == null){
			sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
			return true;
		}
		if(!(faction instanceof PlayerFaction)){
			sender.sendMessage(ChatColor.RED + "This type of faction does not use DTR.");
			return true;
		}
		PlayerFaction playerFaction = (PlayerFaction) faction;
		long previousRegenRemaining = playerFaction.getRemainingRegenerationTime();
		playerFaction.setRemainingRegenerationTime(newRegen);
		Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set DTR regen of " + faction.getName() + " from " + DurationFormatUtils.formatDurationWords(previousRegenRemaining, true, true) + " to " + DurationFormatUtils.formatDurationWords(newRegen, true, true) + '.');
		return true;
	}
}

