package com.sergivb01.hcf.commands.crate.argument;

import com.sergivb01.hcf.HCF;
import com.sergivb01.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class LootBankArgument
		extends CommandArgument{
	private final HCF plugin;

	public LootBankArgument(HCF plugin){
		super("bank", "Check the loot keys in your bank account");
		this.plugin = plugin;
		this.permission = "hcf.commands.loot.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
			return true;
		}
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		Map<String, Integer> crateKeyMap = this.plugin.getKeyManager().getDepositedCrateMap(uuid);
		if(crateKeyMap.isEmpty()){
			sender.sendMessage(ChatColor.RED + "There are no keys in your bank account.");
			return true;
		}
		for(Map.Entry<String, Integer> entry : crateKeyMap.entrySet()){
			sender.sendMessage(ChatColor.YELLOW + entry.getKey() + ": " + ChatColor.GOLD + entry.getValue());
		}
		return true;
	}
}

