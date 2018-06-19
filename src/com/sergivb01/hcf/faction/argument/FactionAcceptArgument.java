package com.sergivb01.hcf.faction.argument;

import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.faction.FactionMember;
import com.sergivb01.hcf.faction.struct.ChatChannel;
import com.sergivb01.hcf.faction.struct.Relation;
import com.sergivb01.hcf.faction.struct.Role;
import com.sergivb01.hcf.faction.type.Faction;
import com.sergivb01.hcf.faction.type.PlayerFaction;
import com.sergivb01.hcf.utils.config.ConfigurationService;
import com.sergivb01.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionAcceptArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionAcceptArgument(HCF plugin){
		super("accept", "Accept a join request from an existing faction.", new String[]{"join", "a"});
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <factionName>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
			return true;
		}
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Player player = (Player) sender;
		if(this.plugin.getFactionManager().getPlayerFaction(player) != null){
			sender.sendMessage(ChatColor.RED + "You are already in a faction.");
			return true;
		}
		Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
		if(faction == null){
			sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
			return true;
		}
		if(!(faction instanceof PlayerFaction)){
			sender.sendMessage(ChatColor.RED + "You can only join player factions.");
			return true;
		}
		PlayerFaction targetFaction = (PlayerFaction) faction;
		if(targetFaction.getMembers().size() >= ConfigurationService.FACTION_PLAYER_LIMIT){
			sender.sendMessage(faction.getDisplayName(sender) + ChatColor.RED + " is full. Faction limits are at " + ConfigurationService.FACTION_PLAYER_LIMIT + '.');
			return true;
		}
		if(!targetFaction.isOpen() && !targetFaction.getInvitedPlayerNames().contains(player.getName())){
			sender.sendMessage(ChatColor.RED + faction.getDisplayName(sender) + ChatColor.RED + " has not invited you.");
			return true;
		}
		if(targetFaction.isLocked()){
			sender.sendMessage(ChatColor.RED + "This faction has been locked, please contact staff if you believe this is an error.");
			return true;
		}
		if(targetFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER))){
			targetFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.YELLOW + " has joined the faction.");
		}
		return true;
	}
}

