package com.sergivb01.hcf.commands.death.argument;

import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.listeners.DeathListener;
import com.sergivb01.util.command.CommandArgument;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeathRefundArgument extends CommandArgument{
	private final HCF plugin;

	public DeathRefundArgument(final HCF plugin){
		super("refund", "Rollback an inventory");
		this.plugin = plugin;
		this.permission = "hcf.commands.death.argument." + this.getName();
	}

	public String getUsage(final String label){
		return '/' + label + ' ' + this.getName() + " <playerName> <reason>";
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args){
		Player p = (Player) sender;
		if(args.length < 3){
			sender.sendMessage(ChatColor.RED + getUsage(command.getLabel()));
			return true;
		}
		if(Bukkit.getPlayer(args[1]) == null){
			p.sendMessage(ChatColor.RED + "Error: Player isn't online.");
			return true;
		}
		Player target = Bukkit.getPlayer(args[1]);
		if(DeathListener.PlayerInventoryContents.containsKey(target.getUniqueId())){
			target.getInventory().setContents(DeathListener.PlayerInventoryContents.get(target.getUniqueId()));
			target.getInventory().setArmorContents(DeathListener.PlayerArmorContents.get(target.getUniqueId()));
			String reason = StringUtils.join(args, ' ', 2, args.length);

			Command.broadcastCommandMessage(p, ChatColor.YELLOW + "Returned " + target.getName() + "'s items for: " + reason);
			DeathListener.PlayerArmorContents.remove(target.getUniqueId());
			DeathListener.PlayerInventoryContents.remove(target.getUniqueId());
			return true;
		}
		p.sendMessage(ChatColor.RED + "Error: Inventory not found. (Already rolled back?)");

		return false;
	}
}