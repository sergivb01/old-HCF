package net.veilmc.hcf.commands;

import net.veilmc.hcf.payloads.Cache;
import net.veilmc.hcf.payloads.types.RequestPayload;
import net.veilmc.hcf.utils.StringUtils;
import net.veilmc.hcf.utils.config.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

import static org.bukkit.ChatColor.RED;

public class RequestCommand implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(RED + "This command may not be executed by Console.");
			return true;
		}

		Player player = (Player) sender;
		if(args.length < 1){
			player.sendMessage(RED + "Invalid usage: `/request <message...>`");
			return true;
		}

		String reason = StringUtils.join(args);


		if(!Cache.canExecute(player)){
			player.sendMessage(RED + "You hav already used the report or request command in the past 5 minutes! Please wait and try again.");
			return true;
		}

		Cache.addPlayerDelay(player);

		if(ConfigurationService.REDIS_ENABLED){
			new RequestPayload(player.getName(), player.getUniqueId(), reason).send();
			return true;
		}

		Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("hcf.utils.staff")).collect(Collectors.toList())
				.forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"Player " + player.getName() + " has requested help: " + reason
				)));

		return true;
	}


}
