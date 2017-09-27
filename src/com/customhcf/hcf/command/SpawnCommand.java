
package com.customhcf.hcf.command;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.utils.ConfigurationService;
import com.customhcf.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpawnCommand implements CommandExecutor, TabCompleter
{
    private static final long KIT_MAP_TELEPORT_DELAY;
    final HCF plugin;

    public SpawnCommand(final HCF plugin) {
        super();
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;

        World world = player.getWorld();
        Location spawn = world.getSpawnLocation().clone().add(0.5, 0.5, 0.5);
        if(player.getGameMode().equals(GameMode.CREATIVE)) {
            player.teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
            player.sendMessage(ChatColor.YELLOW + "You have been teleported to spawn.");
            return true;
        }
        if (ConfigurationService.KIT_MAP) {
            this.plugin.getTimerManager().teleportTimer.teleport(player, Bukkit.getWorld("world").getSpawnLocation(), TimeUnit.SECONDS.toMillis(15L), ChatColor.YELLOW + "Teleporting to spawn in " + ChatColor.LIGHT_PURPLE + "15 seconds.", PlayerTeleportEvent.TeleportCause.COMMAND);
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "This server does not have a spawn command, you must travel there. " + "Spawn can be found at " + ChatColor.GRAY + '(' + spawn.getBlockX() + ", " + spawn.getBlockZ() + ')');
            return true;
        }
//        if (args.length > 0) {
//            world = Bukkit.getWorld(World.Environment.NORMAL.name());
//            spawn = world.getSpawnLocation().clone().add(0.5, 0.0, 0.5);
//        }
//        player.teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 1 || !sender.hasPermission(command.getPermission() + ".teleport")) {
            return Collections.emptyList();
        }
        return (List<String>) BukkitUtils.getCompletions(args, (List)Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
    }

    static {
        KIT_MAP_TELEPORT_DELAY = TimeUnit.SECONDS.toMillis(10L);
    }
}
