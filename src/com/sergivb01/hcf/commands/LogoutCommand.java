package com.sergivb01.hcf.commands;

import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.timer.type.LogoutTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LogoutCommand
		implements CommandExecutor,
		TabCompleter{
	private final HCF plugin;

	public LogoutCommand(HCF plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
			return true;
		}
		Player player = (Player) sender;
		LogoutTimer logoutTimer = this.plugin.getTimerManager().logoutTimer;
		if(!logoutTimer.setCooldown(player, player.getUniqueId())){
			sender.sendMessage(ChatColor.RED + "Your Logout timer is already active.");
			return true;
		}
		sender.sendMessage(ChatColor.RED + "Your Logout timer has started.");
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		return Collections.emptyList();
	}
}