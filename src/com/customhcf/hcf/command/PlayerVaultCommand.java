package com.customhcf.hcf.command;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerVaultCommand implements CommandExecutor {
    private final HCF plugin;

    public PlayerVaultCommand(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "Twat.");
            return false;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction;
        Location location = player.getLocation();
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        if (!(factionAt.isSafezone() || (playerFaction = this.plugin.getFactionManager().getPlayerFaction(player)) != null && playerFaction.equals(factionAt))) {
            player.sendMessage(ChatColor.RED + "Your vault can be opened only in safe-zones or your own claims.");
            return false;
        }

        player.openInventory(player.getEnderChest());

        return true;
    }
}