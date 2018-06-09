package net.veilmc.hcf.faction.argument.staff;

import com.google.common.primitives.Doubles;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.type.Faction;
import net.veilmc.hcf.faction.type.PlayerFaction;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactionSetDtrArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionSetDtrArgument(HCF plugin){
		super("setdtr", "Sets the DTR of a faction.");
		this.plugin = plugin;
		this.permission = "hcf.command.faction.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <playerName|factionName> <newDtr>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(ConfigurationService.KIT_MAP){
			sender.sendMessage(ChatColor.RED + "There is no need for this command on VeilMC kitmap.");
			return false;
		}
		if(args.length < 3){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Double newDTR = Doubles.tryParse(args[2]);
		if(newDTR == null){
			switch(args[2]){
				case "-i":
					Faction factionincrease = this.plugin.getFactionManager().getContainingFaction(args[1]);
					PlayerFaction playerFactionincrease = (PlayerFaction) factionincrease;
					double previousDtr = playerFactionincrease.getDeathsUntilRaidable();
					playerFactionincrease.setDeathsUntilRaidable(previousDtr + 1);
					sender.sendMessage(ChatColor.YELLOW + "You have increased the DTR of " + playerFactionincrease.getName() + " by 1.");
					return true;
				case "-d":
					Faction factiondecrearse = this.plugin.getFactionManager().getContainingFaction(args[1]);
					PlayerFaction playerFactiondecrearse = (PlayerFaction) factiondecrearse;
					double previousDtrdecrearse = playerFactiondecrearse.getDeathsUntilRaidable();
					playerFactiondecrearse.setDeathsUntilRaidable(previousDtrdecrearse - 1);
					sender.sendMessage(ChatColor.YELLOW + "You have decreased the DTR of " + playerFactiondecrearse.getName() + " by 1.");
					return true;
			}
			sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
			return true;
		}
		if(args[1].equalsIgnoreCase("all")){
			for(Faction faction : this.plugin.getFactionManager().getFactions()){
				if(!(faction instanceof PlayerFaction)) continue;
				((PlayerFaction) faction).setDeathsUntilRaidable(newDTR);
			}
			Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set DTR of all factions to " + newDTR + '.');
			return true;
		}
		Faction faction2 = this.plugin.getFactionManager().getContainingFaction(args[1]);
		if(faction2 == null){
			sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
			return true;
		}
		if(!(faction2 instanceof PlayerFaction)){
			sender.sendMessage(ChatColor.RED + "You can only set DTR of player factions.");
			return true;
		}
		PlayerFaction playerFaction = (PlayerFaction) faction2;
		double previousDtr = playerFaction.getDeathsUntilRaidable();
		newDTR = playerFaction.setDeathsUntilRaidable(newDTR);
		Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set DTR of " + faction2.getName() + " from " + previousDtr + " to " + newDTR + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 2 || !(sender instanceof Player)){
			return Collections.emptyList();
		}
		if(args[1].isEmpty()){
			return null;
		}
		Player player = (Player) sender;
		ArrayList<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
		for(Player target : Bukkit.getOnlinePlayers()){
			if(!player.canSee(target) || results.contains(target.getName())) continue;
			results.add(target.getName());
		}
		return results;
	}
}

