package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
<<<<<<< HEAD
=======
import net.veilmc.hcf.HCF;
>>>>>>> origin/new
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SafestopCommand
		implements CommandExecutor,
		TabCompleter{


	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		for(Player abc : Bukkit.getOnlinePlayers()){
			Bukkit.dispatchCommand(abc, "lobby");
		}
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "savedata");
		HCF.getPlugin().getUserManager().saveUserData();
		HCF.getPlugin().getFactionManager().saveFactionData();
		HCF.getPlugin().getEconomyManager().saveEconomyData();
<<<<<<< HEAD
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "The server has been stopped by " + sender.getName());
		Bukkit.broadcastMessage(" ");
=======
		for(String messages : HCF.getInstance().getConfig().getStringList("messages.autosave.message")){
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages).replace("%PLAYER%", sender.getName()));
		}
>>>>>>> origin/new
		Bukkit.shutdown();
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		return Collections.emptyList();
	}
}

