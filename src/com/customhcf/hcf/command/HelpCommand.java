
package com.customhcf.hcf.command;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.ConfigurationService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class HelpCommand
implements CommandExecutor,
TabCompleter {
    private final ChatColor MAIN_COLOR = ChatColor.YELLOW;
    private final ChatColor SECONDARY_COLOR = ChatColor.GOLD;
    private final String EXTRA_COLOR = ChatColor.RED.toString() + (Object)ChatColor.BOLD;
    private final ChatColor VALUE_COLOR = ChatColor.RED;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        sender.sendMessage(HCF.getPlugin().helpTitle);
        sender.sendMessage((Object)this.MAIN_COLOR + " Border Size: ");
        sender.sendMessage((Object)this.SECONDARY_COLOR + "  " + "World" + ": " + (Object)this.VALUE_COLOR + HCF.getPlugin().getServerHandler().getWorldBorder());
        sender.sendMessage((Object)this.SECONDARY_COLOR + "  " + "Nether" + ": " + (Object)this.VALUE_COLOR + HCF.getPlugin().getServerHandler().getNetherBorder());
        sender.sendMessage((Object)this.SECONDARY_COLOR + "  " + "End" + ": " + (Object)this.VALUE_COLOR + HCF.getPlugin().getServerHandler().getEndBorder());
        sender.sendMessage((Object)this.MAIN_COLOR + " End Portal Location: ");
        sender.sendMessage((Object)this.SECONDARY_COLOR + "  Location: " + (Object)this.VALUE_COLOR + ConfigurationService.HELP_ENDPORTAL_LOCATION);
        sender.sendMessage((Object)this.MAIN_COLOR + " End Exit Location: ");
        sender.sendMessage((Object)this.SECONDARY_COLOR + "  Location: " + (Object)this.VALUE_COLOR + ConfigurationService.HELP_END_EXIT);
        sender.sendMessage((Object)this.MAIN_COLOR + " Teamspeak: " + (Object)this.VALUE_COLOR + "ts.veilhcf.us");
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

