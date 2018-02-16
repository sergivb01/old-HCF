package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.type.Faction;
import net.veilmc.hcf.faction.type.PlayerFaction;
import net.veilmc.hcf.utils.ConfigurationService;
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

        if(!ConfigurationService.KIT_MAP){
            sender.sendMessage(ChatColor.RED + "This command can be executed on Kits only.");
            return true;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction;
        Location location = player.getLocation();
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        if (!(factionAt.isSafezone() || (playerFaction = this.plugin.getFactionManager().getPlayerFaction(player)) != null && playerFaction.equals(factionAt))) {
            player.sendMessage(ChatColor.RED + "Your vault can be opened only in safe-zones or your own claim.");
            return false;
        }

        player.openInventory(player.getEnderChest());

        return true;
    }
}