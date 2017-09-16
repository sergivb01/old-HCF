
package com.customhcf.hcf.command;

import com.customhcf.base.BasePlugin;
import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.ConfigurationService;
import com.customhcf.hcf.user.FactionUser;
import com.customhcf.util.chat.ClickAction;
import com.customhcf.util.chat.Text;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerStats
implements CommandExecutor {
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player player = (Player)cs;
        if (args.length == 0) {
            player.sendMessage((Object)ChatColor.RED + "Usage: /" + s + " [player]");
            return true;
        }
        if (args.length == 1) {
            if (Bukkit.getPlayer((String)args[0]) == null) {
                if (Bukkit.getOfflinePlayer((String)args[0]) == null) {
                    player.sendMessage((Object)ChatColor.GOLD + "Player named or with UUID '" + (Object)ChatColor.WHITE + args[0] + (Object)ChatColor.GOLD + "' not found");
                    return true;
                }
                this.sendInformation(player, Bukkit.getOfflinePlayer((String)args[0]));
                return true;
            }
            this.sendInformation(player, (OfflinePlayer)Bukkit.getPlayer((String)args[0]));
            return true;
        }
        return false;
    }



    public void sendInformation(Player player, OfflinePlayer target) {
        FactionUser hcf = HCF.getPlugin().getUserManager().getUser(target.getUniqueId());
        final int targetLives = HCF.getPlugin().getDeathbanManager().getLives(target.getUniqueId());
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        if (HCF.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()) != null) {
            player.sendMessage((Object)HCF.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()).getRelation((CommandSender)player).toChatColour() + target.getName());
            new Text((Object)ChatColor.YELLOW + "  Faction: " + HCF.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()).getDisplayName((CommandSender)player)).setHoverText((Object)ChatColor.GRAY + "Click to view Faction").setClick(ClickAction.RUN_COMMAND, "/f who " + HCF.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()).getName()).send((CommandSender)player);
        } else {
            player.sendMessage((Object)ChatColor.RED + target.getName());
        }
        if (targetLives > 0) {
            player.sendMessage(ChatColor.YELLOW + "  Available Lives: " + targetLives );
        }
        player.sendMessage((Object)ChatColor.YELLOW + "  Playtime: " + (Object)ChatColor.RED + DurationFormatUtils.formatDurationWords((long)BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(target.getUniqueId()), (boolean)true, (boolean)true));
        if (hcf.getDiamondsMined() > 0) {
            player.sendMessage((Object)ChatColor.YELLOW + "  Diamonds Mined: " + (Object)ChatColor.AQUA + hcf.getDiamondsMined());
        }
        if (hcf.getDeathban() != null) {
            new Text((Object)ChatColor.YELLOW + "  Deathbanned: " + (hcf.getDeathban().isActive() ? new StringBuilder().append((Object)ChatColor.GREEN).append("true").toString() : new StringBuilder().append((Object)ChatColor.RED).append("false").toString())).setHoverText((Object)ChatColor.AQUA + "Un-Deathbanned at: " + HCF.getRemaining(hcf.getDeathban().getExpiryMillis(), true, true)).send((CommandSender)player);
        } else {
            if (!ConfigurationService.KIT_MAP) {
                player.sendMessage((Object) ChatColor.YELLOW + "  Deathbanned: " + (Object) ChatColor.RED + "false");
            }
        }
        if (hcf.getKills() > 0) {
            player.sendMessage((Object)ChatColor.YELLOW + "  Kills: " + (Object)ChatColor.GREEN + hcf.getKills());
        }
        if (hcf.getDeaths() > 0) {
            player.sendMessage((Object)ChatColor.YELLOW + "  Deaths: " + (Object)ChatColor.RED + hcf.getDeaths());
        }
        player.sendMessage((Object)ChatColor.YELLOW + "  Balance: " + (Object)ChatColor.RED + HCF.getPlugin().getEconomyManager().getBalance(target.getUniqueId()));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
    }

}

