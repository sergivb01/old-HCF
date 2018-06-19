package com.sergivb01.hcf.commands.spawn;

import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.utils.config.ConfigurationService;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.TimeUnit;

public class SpawnCommand
		implements CommandExecutor{

	private final HCF plugin;

	public SpawnCommand(HCF plugin){
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		final Player player = (Player) sender;
		World world = player.getWorld();
		Location spawn = world.getSpawnLocation().clone().add(0.5, 0.5, 0.5);
		if(player.getGameMode().equals(GameMode.CREATIVE)){
			player.teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
			player.sendMessage(ChatColor.YELLOW + "You have been teleported to spawn.");
			return true;
		}
		if(ConfigurationService.KIT_MAP){
			if(this.plugin.getTimerManager().spawnTagTimer.getRemaining(player) > 0L){
				player.sendMessage(ChatColor.RED + "You can not do this while your " + ChatColor.BOLD + "Spawn Tag" + ChatColor.RED + " is active.");
				return false;
			}else{
				this.plugin.getTimerManager().teleportTimer.teleport(player, Bukkit.getWorld("world").getSpawnLocation(), TimeUnit.SECONDS.toMillis(15L), ChatColor.YELLOW + "Teleporting to spawn in " + ChatColor.LIGHT_PURPLE + "15 seconds.", PlayerTeleportEvent.TeleportCause.COMMAND);
				return true;
			}
		}else{
			if(this.plugin.getUserManager().getUser(player.getUniqueId()).getSpawnTokens() >= 1){
				if(this.plugin.getTimerManager().spawnTagTimer.getRemaining(player) > 0L){
					player.sendMessage(ChatColor.RED + "You can not do this while your " + ChatColor.BOLD + "Spawn Tag" + ChatColor.RED + " is active.");
					return false;
				}else{
					if(this.plugin.getEotwHandler().isEndOfTheWorld(true)){
						player.sendMessage(ChatColor.RED + "You can not use Spawn tokens while in EOTW.");
						return true;
					}
					Integer oldToken = this.plugin.getUserManager().getUser(player.getUniqueId()).getSpawnTokens();
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have used a spawn token."));
					this.plugin.getTimerManager().teleportTimer.teleport(player, Bukkit.getWorld("world").getSpawnLocation(), TimeUnit.SECONDS.toMillis(3L), ChatColor.YELLOW + "Teleporting to spawn in " + ChatColor.LIGHT_PURPLE + "3 seconds.", PlayerTeleportEvent.TeleportCause.COMMAND);
					this.plugin.getUserManager().getUser(player.getUniqueId()).setSpawnTokens(oldToken - 1);
					player.sendMessage("You now have " + this.plugin.getUserManager().getUser(player.getUniqueId()).getSpawnTokens() + " tokens left.");
					return true;
				}
			}else{
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have enough tokens. You have " + this.plugin.getUserManager().getUser(player.getUniqueId()).getSpawnTokens()));
				return true;
			}
		}
	}

}