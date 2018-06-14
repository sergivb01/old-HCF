package net.veilmc.hcf.commands;

import net.veilmc.base.BasePlugin;
import net.veilmc.base.command.BaseCommand;
import net.veilmc.base.user.ServerParticipator;
import net.veilmc.hcf.payloads.Cache;
import net.veilmc.hcf.payloads.types.Payload;
import net.veilmc.hcf.payloads.types.StaffChatPayload;
import net.veilmc.hcf.utils.StringUtils;
import net.veilmc.hcf.utils.config.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public class StaffChatCommand implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args){

		ServerParticipator target;
		ServerParticipator participator = BasePlugin.getPlugin().getUserManager().getParticipator(sender);
		if(participator == null){
			sender.sendMessage(RED + "You are not allowed to do this.");
			return true;
		}

		if(args.length <= 0){
			if(!(sender instanceof Player)){
				sender.sendMessage(RED + "Usage: /sc <player|message...>");
				return true;
			}
			target = participator;
		}else{
			Player targetPlayer = Bukkit.getPlayerExact(args[0]);
			if(!BaseCommand.canSee(sender, targetPlayer) || !sender.hasPermission("command.staffchat.others")){
				if(ConfigurationService.REDIS_ENABLED){
					Payload payload = new StaffChatPayload(participator.getUniqueId(), participator.getName(), StringUtils.join(args));
					payload.send();
					Cache.addPayload(payload);
					return true;
				}

				Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("hcf.command.staffchat")).collect(Collectors.toList())
						.forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&5[StaffChat] &d" + participator.getName() + "&7: " + StringUtils.join(args)
						)));
			}
			target = BasePlugin.getPlugin().getUserManager().getUser(targetPlayer.getUniqueId());
		}
		boolean newStaffChat = !target.isInStaffChat() || args.length >= 2 && Boolean.parseBoolean(args[1]);
		target.setInStaffChat(newStaffChat);
		sender.sendMessage(GREEN + "Staff chat mode of " + target.getName() + " set to " + newStaffChat + '.');
		return true;
	}

}
