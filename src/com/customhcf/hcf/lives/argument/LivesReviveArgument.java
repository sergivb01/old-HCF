
package com.customhcf.hcf.lives.argument;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.ConfigurationService;
import com.customhcf.hcf.deathban.Deathban;
import com.customhcf.hcf.faction.struct.Relation;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.hcf.user.FactionUser;
import com.customhcf.util.command.CommandArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LivesReviveArgument
extends CommandArgument {
    private static final String REVIVE_BYPASS_PERMISSION = "hcf.revive.bypass";
    private static final String REVIVE_DTR = "hcf.revive.dtr";
    private static final String PROXY_CHANNEL_NAME = "BungeeCord";
    private final HCF plugin;

    public LivesReviveArgument(HCF plugin) {
        super("revive", "Revive a death-banned player");
        this.plugin = plugin;
        this.permission = "hcf.command.lives.argument." + this.getName();
        plugin.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)plugin, "BungeeCord");
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <playerName>";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer((String)args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage((Object)ChatColor.GOLD + "Player '" + (Object)ChatColor.WHITE + args[1] + (Object)ChatColor.GOLD + "' not found.");
            return true;
        }
        UUID targetUUID = target.getUniqueId();
        FactionUser factionTarget = this.plugin.getUserManager().getUser(targetUUID);
        Deathban deathban = factionTarget.getDeathban();
        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage((Object)ChatColor.RED + target.getName() + " is not death-banned.");
            return true;
        }
        Relation relation = Relation.ENEMY;
        if (sender instanceof Player) {
            if (!sender.hasPermission("hcf.revive.bypass") && this.plugin.getEotwHandler().isEndOfTheWorld()) {
                sender.sendMessage((Object)ChatColor.RED + "You cannot revive players during EOTW.");
                return true;
            }
            if (!sender.hasPermission("hcf.revive.bypass")) {
                Player player = (Player)sender;
                UUID playerUUID = player.getUniqueId();
                int selfLives = this.plugin.getDeathbanManager().getLives(playerUUID);
                if (selfLives <= 0) {
                    sender.sendMessage((Object)ChatColor.RED + "You do not have any lives.");
                    return true;
                }
                this.plugin.getDeathbanManager().setLives(playerUUID, selfLives - 1);
                PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                relation = playerFaction == null ? Relation.ENEMY : playerFaction.getFactionRelation(this.plugin.getFactionManager().getPlayerFaction(targetUUID));
                sender.sendMessage((Object)ChatColor.YELLOW + "You have revived " + (Object)relation.toChatColour() + target.getName() + (Object)ChatColor.YELLOW + '.');
            } else if (sender.hasPermission("hcf.revive.dtr")) {
                if (this.plugin.getFactionManager().getPlayerFaction(targetUUID) != null) {
                    this.plugin.getFactionManager().getPlayerFaction(targetUUID).setDeathsUntilRaidable(this.plugin.getFactionManager().getPlayerFaction(targetUUID).getDeathsUntilRaidable() + 1.0);
                }
                sender.sendMessage((Object)ChatColor.YELLOW + "You have revived and added DTR to " + (Object)relation.toChatColour() + target.getName() + (Object)ChatColor.YELLOW + '.');
            } else {
                sender.sendMessage((Object)ChatColor.YELLOW + "You have revived " + (Object)relation.toChatColour() + target.getName() + (Object)ChatColor.YELLOW + '.');
            }
        } else {
            sender.sendMessage((Object)ChatColor.YELLOW + "You have revived " + (Object)ConfigurationService.ENEMY_COLOUR + target.getName() + (Object)ChatColor.YELLOW + '.');
        }
        factionTarget.removeDeathban();
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        ArrayList<String> results = new ArrayList<String>();
        Collection<FactionUser> factionUsers = this.plugin.getUserManager().getUsers().values();
        for (FactionUser factionUser : factionUsers) {
            String offlineName;
            OfflinePlayer offlinePlayer;
            Deathban deathban = factionUser.getDeathban();
            if (deathban == null || !deathban.isActive() || (offlineName = (offlinePlayer = Bukkit.getOfflinePlayer((UUID)factionUser.getUserUUID())).getName()) == null) continue;
            results.add(offlinePlayer.getName());
        }
        return results;
    }
}

