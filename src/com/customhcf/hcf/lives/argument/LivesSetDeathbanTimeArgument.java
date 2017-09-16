
package com.customhcf.hcf.lives.argument;

import com.customhcf.hcf.Utils.ConfigurationService;
import com.customhcf.util.JavaUtils;
import com.customhcf.util.command.CommandArgument;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LivesSetDeathbanTimeArgument
extends CommandArgument {
    public LivesSetDeathbanTimeArgument() {
        super("setdeathbantime", "Sets the base deathban time");
        this.permission = "hcf.command.lives.argument." + this.getName();
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <time>";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        long duration = JavaUtils.parse((String)args[1]);
        if (duration == -1) {
            sender.sendMessage((Object)ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
            return true;
        }
        ConfigurationService.DEFAULT_DEATHBAN_DURATION = duration;
        Command.broadcastCommandMessage((CommandSender)sender, (String)((Object)ChatColor.YELLOW + "Base death-ban time set to " + DurationFormatUtils.formatDurationWords((long)duration, (boolean)true, (boolean)true) + " (not including multipliers, etc)."));
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

