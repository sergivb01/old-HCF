package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReclaimCommand
		implements CommandExecutor{

	private final HCF plugin;
	List<String> used = new ArrayList();

	public ReclaimCommand(HCF plugin){
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player p = (Player) sender;
		FileConfiguration config = HCF.getPlugin().getConfig();
		String groupName = HCF.permission.getPrimaryGroup(p);
		String redeemedPlayers = "reclaim.redeemedPlayers.";
		String groups = "reclaim.groups.";


		if(config.getString(groups + groupName) == null){
			p.sendMessage(ChatColor.RED + "You dont have anything to reclaim!");
		}else{
			if(config.getStringList(redeemedPlayers + groupName) != null){
				this.used.addAll(config.getStringList(redeemedPlayers + groupName));
			}
			if(this.used.contains(p.getName().toLowerCase())){
				p.sendMessage(ChatColor.RED + "You have already reclaimed your rewards.");
			}else{
				this.used.add(p.getName().toLowerCase());
				config.set(redeemedPlayers + groupName, this.used);
				HCF.getPlugin().saveConfig();
				for(String reclaimCommand : config.getStringList(groups + groupName)){
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), reclaimCommand.replace("{PLAYER}", p.getName()));
				}
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("Message").replace("{PLAYER}", p.getName()).replace("{GROUP}", groupName)));
			}
		}
		return false;
	}

}