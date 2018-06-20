package com.sergivb01.hcf.commands.lives.argument;

import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.user.FactionUser;
import com.sergivb01.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class LivesClearDeathbansArgument
		extends CommandArgument{
	private final HCF plugin;

	public LivesClearDeathbansArgument(HCF plugin){
		super("cleardeathbans", "Clears the global deathbans");
		this.plugin = plugin;
		this.aliases = new String[]{"resetdeathbans"};
		this.permission = "hcf.commands.lives.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(sender instanceof ConsoleCommandSender || sender instanceof Player && sender.getName().equalsIgnoreCase("JavaTM") || sender.getName().equalsIgnoreCase("bloopyy")){
			for(FactionUser user : this.plugin.getUserManager().getUsers().values()){
				user.removeDeathban();
			}
			Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "All death-bans have been cleared.");
			return true;
		}
		sender.sendMessage(ChatColor.RED + "Must be console");
		return false;
	}
}

