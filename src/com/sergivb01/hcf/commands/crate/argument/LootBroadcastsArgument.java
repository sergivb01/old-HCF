package com.sergivb01.hcf.commands.crate.argument;

import com.sergivb01.hcf.utils.config.ConfigurationService;
import com.sergivb01.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LootBroadcastsArgument
		extends CommandArgument{
	public LootBroadcastsArgument(){
		super("broadcasts", "Toggle broadcasts for key announcements", new String[]{"togglealerts", "togglebroadcasts"});
		this.permission = "hcf.commands.loot.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		ConfigurationService.CRATE_BROADCASTS = !ConfigurationService.CRATE_BROADCASTS;
		boolean newBroadcasts = ConfigurationService.CRATE_BROADCASTS;
		sender.sendMessage(ChatColor.GOLD + "Crate keys " + (newBroadcasts ? "now" : "no longer") + " broadcasts reward messages.");
		return true;
	}
}

