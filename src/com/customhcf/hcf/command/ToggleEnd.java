
package com.customhcf.hcf.command;

import com.customhcf.base.BasePlugin;
import com.customhcf.base.ServerHandler;
import com.customhcf.hcf.HCF;

import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ToggleEnd
implements CommandExecutor,
TabCompleter {
    private final HCF plugin;

    public ToggleEnd(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean newMode = !BasePlugin.getPlugin().getServerHandler().isEnd();
        BasePlugin.getPlugin().getServerHandler().setEnd(newMode);
        Bukkit.broadcastMessage((String)((Object)ChatColor.YELLOW + "The End is now " + (!newMode ? new StringBuilder().append((Object)ChatColor.RED).append("closed").toString() : new StringBuilder().append((Object)ChatColor.GREEN).append("open").toString()) + (Object)ChatColor.YELLOW + '.'));
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

