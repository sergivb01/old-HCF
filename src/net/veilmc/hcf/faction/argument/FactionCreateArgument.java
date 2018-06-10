package net.veilmc.hcf.faction.argument;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.type.PlayerFaction;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.util.JavaUtils;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionCreateArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionCreateArgument(HCF plugin){
		super("create", "Create a faction.", new String[]{"make", "define"});
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <factionName>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command may only be executed by players.");
			return true;
		}
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		String name = args[1];
		if(ConfigurationService.DISALLOWED_FACTION_NAMES.contains(name.toLowerCase())){
			sender.sendMessage(ChatColor.RED + "'" + name + "' is a blocked faction name.");
			return true;
		}
		if(name.length() < 3){
			sender.sendMessage(ChatColor.RED + "Faction names must have at least " + 3 + " characters.");
			return true;
		}
		if(name.length() > 16){
			sender.sendMessage(ChatColor.RED + "Faction names cannot be longer than " + 16 + " characters.");
			return true;
		}
		if(!JavaUtils.isAlphanumeric(name)){
			sender.sendMessage(ChatColor.RED + "Faction names may only be alphanumeric.");
			return true;
		}
		if(this.plugin.getFactionManager().getFaction(name) != null){
			sender.sendMessage(ChatColor.RED + "Faction '" + name + "' already exists.");
			return true;
		}
		if(this.plugin.getFactionManager().getPlayerFaction((Player) sender) != null){
			sender.sendMessage(ChatColor.RED + "You are already in a faction.");
			return true;
		}
		this.plugin.getFactionManager().createFaction(new PlayerFaction(name), sender);
		return true;
	}
}

