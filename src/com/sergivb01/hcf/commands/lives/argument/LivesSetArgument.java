package com.sergivb01.hcf.commands.lives.argument;

import com.google.common.primitives.Ints;
import com.sergivb01.base.BaseConstants;
import com.sergivb01.hcf.HCF;
import com.sergivb01.util.BukkitUtils;
import com.sergivb01.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class LivesSetArgument
		extends CommandArgument{
	private final HCF plugin;

	public LivesSetArgument(HCF plugin){
		super("set", "Set the lives of a player");
		this.plugin = plugin;
		this.permission = "hcf.commands.lives.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <playerName> <amount>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length < 3){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Integer amount = Ints.tryParse(args[2]);
		if(amount == null){
			sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
			return true;
		}
		OfflinePlayer target = BukkitUtils.offlinePlayerWithNameOrUUID(args[1]);
		if(!target.hasPlayedBefore() && !target.isOnline()){
			sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[1]));
			return true;
		}
		this.plugin.getDeathbanManager().setLives(target.getUniqueId(), amount);
		sender.sendMessage(ChatColor.YELLOW + target.getName() + " now has " + ChatColor.GOLD + amount + ChatColor.YELLOW + " lives.");
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		return args.length == 2 ? null : Collections.emptyList();
	}
}

