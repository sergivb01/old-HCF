
package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
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
    private final String EXTRA_COLOR = ChatColor.RED.toString() + ChatColor.BOLD;
    private final ChatColor VALUE_COLOR = ChatColor.RED;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        sender.sendMessage(HCF.getPlugin().helpTitle);
        sender.sendMessage(this.MAIN_COLOR + " Border Size: ");
        sender.sendMessage(this.SECONDARY_COLOR + "  " + "World" + ": " + this.VALUE_COLOR + HCF.getPlugin().getServerHandler().getWorldBorder());
        sender.sendMessage(this.SECONDARY_COLOR + "  " + "Nether" + ": " + this.VALUE_COLOR + HCF.getPlugin().getServerHandler().getNetherBorder());
        sender.sendMessage(this.SECONDARY_COLOR + "  " + "End" + ": " + this.VALUE_COLOR + HCF.getPlugin().getServerHandler().getEndBorder());
        sender.sendMessage(this.MAIN_COLOR + " End Portal Location: ");
        sender.sendMessage(this.SECONDARY_COLOR + "  Location: " + this.VALUE_COLOR + ConfigurationService.HELP_ENDPORTAL_LOCATION);
        sender.sendMessage(this.MAIN_COLOR + " End Exit Location: ");
        sender.sendMessage(this.SECONDARY_COLOR + "  Location: " + this.VALUE_COLOR + ConfigurationService.HELP_END_EXIT);
        sender.sendMessage(this.MAIN_COLOR + " Teamspeak: " + this.VALUE_COLOR + "ts.veilhcf.us");
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");*/

        for (String messages : HCF.getInstance().getConfig().getStringList("help")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages).replace("%OVERWORLD%", HCF.getPlugin().getServerHandler().getWorldBorder() + "")
                    .replace("%NETHER%", HCF.getPlugin().getServerHandler().getNetherBorder() + "")
                    .replace("%END%", HCF.getPlugin().getServerHandler().getEndBorder() + ""));
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

