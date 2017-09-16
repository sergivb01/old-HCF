
package com.customhcf.hcf.faction.argument;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.FactionMember;
import com.customhcf.hcf.faction.claim.Claim;
import com.customhcf.hcf.faction.struct.Role;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.util.chat.ClickAction;
import com.customhcf.util.chat.Text;
import com.customhcf.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionUnclaimArgument
extends CommandArgument {
    private static final ImmutableList<String> COMPLETIONS;
    private static final HashSet<String> stuff;
    private final HCF plugin;

    public FactionUnclaimArgument(HCF plugin) {
        super("unclaim", "Unclaims land from your faction.");
        this.plugin = plugin;
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " ";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "Only players can un-claim land from a faction.");
            return true;
        }
        Player player = (Player)sender;
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage((Object)ChatColor.RED + "You are not in a faction.");
            return true;
        }
        FactionMember factionMember = playerFaction.getMember(player);
        if (factionMember.getRole() != Role.LEADER) {
            sender.sendMessage((Object)ChatColor.RED + "You must be a faction leader to unclaim land.");
            return true;
        }
        Set<Claim> factionClaims = playerFaction.getClaims();
        if (factionClaims.isEmpty()) {
            sender.sendMessage((Object)ChatColor.RED + "Your faction does not own any claims.");
            return true;
        }
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("yes") && stuff.contains(player.getName())) {
                for (Claim claims : factionClaims) {
                    playerFaction.removeClaim(claims, (CommandSender)player);
                }
                factionClaims.clear();
                return true;
            }
            if (args[1].equalsIgnoreCase("no") && stuff.contains(player.getName())) {
                stuff.remove(player.getName());
                player.sendMessage((Object)ChatColor.YELLOW + "You have been removed the unclaim-set.");
                return true;
            }
        }
        stuff.add(player.getName());
        new Text((Object)ChatColor.YELLOW + "Do you want to unclaim " + (Object)ChatColor.BOLD + "all" + (Object)ChatColor.YELLOW + " of your land?").send((CommandSender)player);
        new Text((Object)ChatColor.YELLOW + "If so, " + (Object)ChatColor.DARK_GREEN + "/f unclaim yes" + (Object)ChatColor.YELLOW + " otherwise do" + (Object)ChatColor.DARK_RED + " /f unclaim no" + (Object)ChatColor.GRAY + " (Click here to unclaim)").setHoverText((Object)ChatColor.GOLD + "Click here to unclaim all").setClick(ClickAction.RUN_COMMAND, "/f unclaim yes").send((CommandSender)player);
        return true;
    }

    static {
        stuff = new HashSet();
        COMPLETIONS = ImmutableList.of("all");
    }
}

