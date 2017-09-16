
package com.customhcf.hcf.faction.argument.staff;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.util.command.CommandArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FactionRemoveArgument
extends CommandArgument {
    private final ConversationFactory factory;
    private final HCF plugin;

    public FactionRemoveArgument(HCF plugin) {
        super("remove", "Remove a faction.");
        this.plugin = plugin;
        this.aliases = new String[]{"delete", "forcedisband", "forceremove"};
        this.permission = "hcf.command.faction.argument." + this.getName();
        this.factory = new ConversationFactory((Plugin)plugin).withFirstPrompt((Prompt)new RemoveAllPrompt(plugin)).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <all|factionName>";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        if (args[1].equalsIgnoreCase("all")) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage((Object)ChatColor.RED + "This command can be only executed from console.");
                return true;
            }
            Conversable conversable = (Conversable)sender;
            conversable.beginConversation(this.factory.buildConversation(conversable));
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
            sender.sendMessage((Object)ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        if (this.plugin.getFactionManager().removeFaction(faction, sender)) {
            Command.broadcastCommandMessage((CommandSender)sender, (String)((Object)ChatColor.YELLOW + "Disbanded faction " + faction.getName() + (Object)ChatColor.YELLOW + '.'));
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        Player player = (Player)sender;
        ArrayList<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!player.canSee(target) || results.contains(target.getName())) continue;
            results.add(target.getName());
        }
        return results;
    }

    private static class RemoveAllPrompt
    extends StringPrompt {
        private final HCF plugin;

        public RemoveAllPrompt(HCF plugin) {
            this.plugin = plugin;
        }

        public String getPromptText(ConversationContext context) {
            return (Object)ChatColor.YELLOW + "Are you sure you want to do this? " + (Object)ChatColor.RED + (Object)ChatColor.BOLD + "All factions" + (Object)ChatColor.YELLOW + " will be cleared. " + "Type " + (Object)ChatColor.GREEN + "yes" + (Object)ChatColor.YELLOW + " to confirm or " + (Object)ChatColor.RED + "no" + (Object)ChatColor.YELLOW + " to deny.";
        }

        public Prompt acceptInput(ConversationContext context, String string) {
            String lowerCase;
            switch (lowerCase = string.toLowerCase()) {
                case "yes": {
                    Conversable conversable;
                    for (Faction faction : this.plugin.getFactionManager().getFactions()) {
                        this.plugin.getFactionManager().removeFaction(faction, (CommandSender)Bukkit.getConsoleSender());
                    }
                    Bukkit.broadcastMessage((String)(ChatColor.GOLD.toString() + (Object)ChatColor.BOLD + "All factions have been disbanded" + ((conversable = context.getForWhom()) instanceof CommandSender ? new StringBuilder().append(" by ").append(((CommandSender)conversable).getName()).toString() : "") + '.'));
                    return Prompt.END_OF_CONVERSATION;
                }
                case "no": {
                    context.getForWhom().sendRawMessage((Object)ChatColor.BLUE + "Cancelled the process of disbanding all factions.");
                    return Prompt.END_OF_CONVERSATION;
                }
            }
            context.getForWhom().sendRawMessage((Object)ChatColor.RED + "Unrecognized response. Process of disbanding all factions cancelled.");
            return Prompt.END_OF_CONVERSATION;
        }
    }

}

