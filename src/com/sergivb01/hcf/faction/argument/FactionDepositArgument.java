package com.sergivb01.hcf.faction.argument;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.faction.struct.Relation;
import com.sergivb01.hcf.faction.type.PlayerFaction;
import com.sergivb01.util.JavaUtils;
import com.sergivb01.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FactionDepositArgument
		extends CommandArgument{
	private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
	private final HCF plugin;

	public FactionDepositArgument(HCF plugin){
		super("deposit", "Deposits money to the faction balance.", new String[]{"d"});
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <all|amount>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Integer amount;
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
			return true;
		}
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Player player = (Player) sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if(playerFaction == null){
			sender.sendMessage(ChatColor.RED + "You are not in a faction.");
			return true;
		}
		UUID uuid = player.getUniqueId();
		int playerBalance = this.plugin.getEconomyManager().getBalance(uuid);
		if(args[1].equalsIgnoreCase("all")){
			amount = playerBalance;
		}else{
			amount = Ints.tryParse(args[1]);
			if(amount == null){
				sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
				return true;
			}
		}
		if(amount <= 0){
			sender.sendMessage(ChatColor.RED + "Amount must be positive.");
			return true;
		}
		if(playerBalance < amount){
			sender.sendMessage(ChatColor.RED + "You need at least " + '$' + JavaUtils.format(amount) + " to do this, you only have " + '$' + JavaUtils.format(playerBalance) + '.');
			return true;
		}
		this.plugin.getEconomyManager().subtractBalance(uuid, amount);
		playerFaction.setBalance(playerFaction.getBalance() + amount);
		playerFaction.broadcast(Relation.MEMBER.toChatColour() + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " has deposited " + ChatColor.GREEN + '$' + JavaUtils.format(amount) + ChatColor.YELLOW + " into the faction balance.");
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		return args.length == 2 ? COMPLETIONS : Collections.emptyList();
	}
}

