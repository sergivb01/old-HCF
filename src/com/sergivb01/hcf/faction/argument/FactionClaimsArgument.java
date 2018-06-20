package com.sergivb01.hcf.faction.argument;

import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.faction.claim.Claim;
import com.sergivb01.hcf.faction.type.ClaimableFaction;
import com.sergivb01.hcf.faction.type.Faction;
import com.sergivb01.hcf.faction.type.PlayerFaction;
import com.sergivb01.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FactionClaimsArgument extends CommandArgument{
	private final HCF plugin;

	public FactionClaimsArgument(final HCF plugin){
		super("claims", "View all claims for a faction.");
		this.plugin = plugin;
	}

	public String getUsage(final String label){
		return '/' + label + ' ' + this.getName() + " [factionName]";
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args){
		final PlayerFaction selfFaction = (sender instanceof Player) ? this.plugin.getFactionManager().getPlayerFaction((Player) sender) : null;
		ClaimableFaction targetFaction;
		if(args.length < 2){
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
				return true;
			}
			if(selfFaction == null){
				sender.sendMessage(ChatColor.RED + "You are not in a faction.");
				return true;
			}
			targetFaction = selfFaction;
		}else{
			final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
			if(faction == null){
				sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
				return true;
			}
			if(!(faction instanceof ClaimableFaction)){
				sender.sendMessage(ChatColor.RED + "You can only check the claims of factions that can have claims.");
				return true;
			}
			targetFaction = (ClaimableFaction) faction;
		}
		final Collection<Claim> claims = targetFaction.getClaims();
		if(claims.isEmpty()){
			sender.sendMessage(ChatColor.RED + "Faction " + targetFaction.getDisplayName(sender) + ChatColor.RED + " has no claimed land.");
			return true;
		}
		if(sender instanceof Player && !sender.isOp() && targetFaction instanceof PlayerFaction && ((PlayerFaction) targetFaction).getHome() == null && (selfFaction == null || !selfFaction.equals(targetFaction))){
			sender.sendMessage(ChatColor.RED + "You cannot view the claims of " + targetFaction.getDisplayName(sender) + ChatColor.RED + " because their home is unset.");
			return true;
		}
		sender.sendMessage(ChatColor.YELLOW + "Claims of " + targetFaction.getDisplayName(sender));
		for(final Claim claim : claims){
			sender.sendMessage(ChatColor.GRAY + " " + claim.getFormattedName());
		}
		return true;
	}

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args){
		if(args.length != 2 || !(sender instanceof Player)){
			return Collections.emptyList();
		}
		if(args[1].isEmpty()){
			return null;
		}
		final Player player = (Player) sender;
		final List<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
		for(final Player target : Bukkit.getServer().getOnlinePlayers()){
			if(player.canSee(target) && !results.contains(target.getName())){
				results.add(target.getName());
			}
		}
		return results;
	}
}